package org.jboss.fuse.tnb.redshift.service;

import org.jboss.fuse.tnb.aws.client.AWSClient;
import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.redshift.account.RedshiftAWSAccount;
import org.jboss.fuse.tnb.redshift.validation.RedshiftValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.DescribeClusterSnapshotsResponse;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;

@AutoService(Redshift.class)
public class Redshift extends AWSService<RedshiftAWSAccount, RedshiftDataClient, RedshiftValidation> {
    private RedshiftClient redshiftClient;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new AWS Redshift validation");
        redshiftClient = AWSClient.createDefaultClient(account(), RedshiftClient.class);
        validation = new RedshiftValidation(redshiftClient, client(RedshiftDataClient.class));
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

    @Override
    public RedshiftAWSAccount account() {
        if (account == null) {
            account = Accounts.get(RedshiftAWSAccount.class);
        }
        return account;
    }

    public Cluster getCluster() {
        return redshiftClient.describeClusters().clusters().get(0);
    }

    private void resumeCluster() {
        //resume cluster
        if (!getCluster().clusterStatus().equals("available")) {
            redshiftClient
                .resumeCluster(builder -> builder.clusterIdentifier(getCluster().clusterIdentifier()).build());
            //wait for available
            WaitUtils.waitFor(() -> getCluster().clusterStatus().equals("available"), 30, 30000,
                "Waiting for Cluster " + getCluster().clusterIdentifier() + " to be available");
        }
    }

    private void pauseCluster() {
        if (!getCluster().clusterStatus().equals("paused")) {
            //check snapshots
            checkSnapshots();
            redshiftClient.pauseCluster(builder -> builder.clusterIdentifier(getCluster().clusterIdentifier()).build());
            //wait for paused
            WaitUtils
                .waitFor(() -> getCluster().clusterStatus().equals("paused"), 30, 30000,
                    "Waiting for Cluster " + getCluster().clusterIdentifier() + " to be paused");
        }
    }

    private void checkSnapshots() {
        DescribeClusterSnapshotsResponse respRecent = redshiftClient.describeClusterSnapshots(builder -> builder
            .clusterIdentifier(getCluster().clusterIdentifier())
            .startTime(Instant.now().minus(5, ChronoUnit.HOURS)) //any - manual/automated
        );

        if (respRecent.snapshots().size() == 0) {
            LOG.debug("Create a snapshot");
            //needs to be created new snapshot
            String id = "snapshot-tnb-" + new Date().getTime();
            redshiftClient.createClusterSnapshot(builder -> builder
                .clusterIdentifier(getCluster().clusterIdentifier())
                .snapshotIdentifier(id)
                .manualSnapshotRetentionPeriod(1)); //1 day
            //wait for snapshot to be available
            WaitUtils.waitFor(() -> redshiftClient.describeClusterSnapshots(builder -> builder
                    .clusterIdentifier(getCluster().clusterIdentifier()).snapshotIdentifier(id)).snapshots().get(0).status().equals("available"), 30,
                10000, "Waiting for snapshot " + id + " to be available");
        }
    }
}
