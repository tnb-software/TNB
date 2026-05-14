package software.tnb.aws.redshift.service;

import software.tnb.aws.common.client.AWSClient;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.redshift.account.RedshiftAccount;
import software.tnb.aws.redshift.validation.RedshiftValidation;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.DescribeClusterSnapshotsRequest;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;

@AutoService(Redshift.class)
public class Redshift extends AWSService<RedshiftAccount, RedshiftDataClient, RedshiftValidation> {
    private static final String SNAPSHOT_PREFIX = "tnb-snapshot-";
    private RedshiftClient redshiftClient;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new AWS Redshift validation");
        redshiftClient = AWSClient.createDefaultClient(account(), RedshiftClient.class,
            getConfiguration().isLocalstack() ? localStack.clientUrl() : null);
        validation = new RedshiftValidation(redshiftClient, client(), account());
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

    public Cluster cluster() {
        return redshiftClient.describeClusters(b -> b.clusterIdentifier(account().clusterIdentifier())).clusters().get(0);
    }

    private void resumeCluster() {
        //resume cluster
        if (!"available".equalsIgnoreCase(cluster().clusterAvailabilityStatus())) {
            redshiftClient
                .resumeCluster(builder -> builder.clusterIdentifier(account.clusterIdentifier()).build());
            //wait for available
            WaitUtils.waitFor(new Waiter(() -> "available".equalsIgnoreCase(cluster().clusterAvailabilityStatus()),
                "Waiting for Cluster " + cluster().clusterIdentifier() + " to be available").timeout(60, 30000));
        }
    }

    private void pauseCluster() {
        if (!cluster().clusterAvailabilityStatus().equalsIgnoreCase("paused")) {
            createSnapshot();
            try {
                redshiftClient.pauseCluster(builder -> builder.clusterIdentifier(account.clusterIdentifier()).build());
                //wait for paused
                WaitUtils.waitFor(new Waiter(() -> "paused".equalsIgnoreCase(cluster().clusterAvailabilityStatus()),
                    "Waiting for Cluster " + cluster().clusterIdentifier() + " to be paused").timeout(60, 30000));
            } catch (RedshiftException e) {
                throw new RuntimeException("Failed to stop redshift cluster, needs to be stopped manually", e);
            }
        }
    }

    /**
     * Occasionally the pause operation failed with: You can't pause cluster because no recently available backup was found.
     */
    private void createSnapshot() {
        // delete previous snapshots as we don't really need those
        redshiftClient.describeClusterSnapshots().snapshots().stream()
            .filter(s -> s.snapshotIdentifier().startsWith(SNAPSHOT_PREFIX))
            .forEach(s -> redshiftClient.deleteClusterSnapshot(r -> r.snapshotIdentifier(s.snapshotIdentifier())));

        // create new snapshot
        String snapshot = SNAPSHOT_PREFIX + System.currentTimeMillis();
        LOG.info("Creating cluster snapshot {}", snapshot);
        redshiftClient.createClusterSnapshot(r -> r.clusterIdentifier(account().clusterIdentifier()).snapshotIdentifier(snapshot));

        String status = null;

        while (!"available".equals(status)) {
            WaitUtils.sleep(30000L);

            status = redshiftClient.describeClusterSnapshots(
                    DescribeClusterSnapshotsRequest.builder()
                        .snapshotIdentifier(snapshot)
                        .build())
                .snapshots()
                .get(0)
                .status();
        }

        WaitUtils.waitFor(new Waiter(() -> "available".equalsIgnoreCase(cluster().clusterAvailabilityStatus()),
            "Waiting until the cluster is available after snapshot").timeout(30, 30000L));
    }
}
