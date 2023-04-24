package software.tnb.aws.redshift.service;

import software.tnb.aws.common.client.AWSClient;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.redshift.account.RedshiftAccount;
import software.tnb.aws.redshift.validation.RedshiftValidation;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.DescribeClusterSnapshotsResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;

@AutoService(Redshift.class)
public class Redshift extends AWSService<RedshiftAccount, RedshiftDataClient, RedshiftValidation> {
    private RedshiftClient redshiftClient;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new AWS Redshift validation");
        redshiftClient = AWSClient.createDefaultClient(account(), RedshiftClient.class,
            getConfiguration().isLocalstack() ? localStack.clientUrl() : null);
        validation = new RedshiftValidation(redshiftClient, client(), account());
        LOG.debug("Clusters: " + redshiftClient.describeClusters().toString());
        resumeCluster();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        super.afterAll(extensionContext);
        pauseCluster();
        if (redshiftClient != null) {
            redshiftClient.close();
        }
    }

    public Cluster getCluster() {
        return redshiftClient.describeClusters().clusters().stream()
            .filter(cluster -> cluster.clusterIdentifier().equals(account.clusterIdentifier())).findFirst().get();
    }

    private void resumeCluster() {
        //resume cluster
        if (!getCluster().clusterAvailabilityStatus().equalsIgnoreCase("available")) {
            redshiftClient
                .resumeCluster(builder -> builder.clusterIdentifier(account.clusterIdentifier()).build());
            //wait for available
            WaitUtils.waitFor(() -> getCluster().clusterAvailabilityStatus().equalsIgnoreCase("available"), 30, 30000,
                "Waiting for Cluster " + getCluster().clusterIdentifier() + " to be available");
        }
    }

    private void pauseCluster() {
        if (!getCluster().clusterAvailabilityStatus().equalsIgnoreCase("paused")) {
            checkSnapshots();
            try {
                redshiftClient.pauseCluster(builder -> builder.clusterIdentifier(account.clusterIdentifier()).build());
                //wait for paused
                WaitUtils
                    .waitFor(() -> getCluster().clusterAvailabilityStatus().equalsIgnoreCase("paused"), 30, 30000,
                        "Waiting for Cluster " + getCluster().clusterIdentifier() + " to be paused");
            } catch (RedshiftException e) {
                throw new RuntimeException("Failed to stop redshift cluster, needs to be stopped manually");
            }
        }
    }

    private void checkSnapshots() {
        DescribeClusterSnapshotsResponse respRecent = redshiftClient.describeClusterSnapshots(builder -> builder
            .clusterIdentifier(account.clusterIdentifier())
            .startTime(Instant.now().minus(5, ChronoUnit.HOURS)) //any - manual/automated
        );

        if (respRecent.snapshots().size() == 0) {
            LOG.debug("Create a snapshot");
            //needs to be created new snapshot
            String id = "snapshot-tnb-" + new Date().getTime();
            redshiftClient.createClusterSnapshot(builder -> builder
                .clusterIdentifier(account.clusterIdentifier())
                .snapshotIdentifier(id)
                .manualSnapshotRetentionPeriod(1)); //1 day
            //wait for snapshot to be available
            WaitUtils.waitFor(() -> redshiftClient.describeClusterSnapshots(builder -> builder
                    .snapshotIdentifier(id)).snapshots().get(0).status().equals("available"), 30,
                10000, "Waiting for snapshot " + id + " to be available");

            //wait till cluster leaves "Modifying" state
            WaitUtils
                .waitFor(() -> !getCluster().clusterAvailabilityStatus().equalsIgnoreCase("modifying"), 30, 30000,
                    "Waiting for Cluster " + getCluster().clusterIdentifier() + " to process the snapshot");
        }
    }
}
