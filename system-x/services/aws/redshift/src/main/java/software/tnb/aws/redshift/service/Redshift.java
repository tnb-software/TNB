package software.tnb.aws.redshift.service;

import software.tnb.aws.common.client.AWSClient;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.redshift.account.RedshiftAccount;
import software.tnb.aws.redshift.validation.RedshiftValidation;
import software.tnb.aws.s3.service.S3;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.auto.service.AutoService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.BooleanSupplier;

import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshift.model.DescribeClusterSnapshotsResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;

@AutoService(Redshift.class)
public class Redshift extends AWSService<RedshiftAccount, RedshiftDataClient, RedshiftValidation> {
    private static final String S3_BUCKET = "tnb-redshift-lock";
    private static final String S3_FILE = "tnb-redshift-lock";

    private S3 s3 = ServiceFactory.create(S3.class);
    private RedshiftClient redshiftClient;
    private String content = "This file is created by TNB Redshift service and serves as a global lock for the redshift cluster so that only one "
        + "instance of RedShift operates on the cluster at time. If there was an error and this file was not deleted, this bucket should be "
        + "configured to automatically delete all files after 1 day.\nLock id: " + RandomStringUtils.randomAlphabetic(10);

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        s3.beforeAll(extensionContext);
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
        releaseS3Lock();
        s3.afterAll(extensionContext);
        if (redshiftClient != null) {
            redshiftClient.close();
        }
    }

    public Cluster cluster() {
        return redshiftClient.describeClusters().clusters().stream()
            .filter(cluster -> cluster.clusterIdentifier().equals(account.clusterIdentifier())).findFirst().get();
    }

    private void resumeCluster() {
        waitForS3Lock();
        //resume cluster
        if (!cluster().clusterAvailabilityStatus().equalsIgnoreCase("available")) {
            redshiftClient
                .resumeCluster(builder -> builder.clusterIdentifier(account.clusterIdentifier()).build());
            //wait for available
            WaitUtils.waitFor(() -> cluster().clusterAvailabilityStatus().equalsIgnoreCase("available"), 60, 30000,
                "Waiting for Cluster " + cluster().clusterIdentifier() + " to be available");
        }
    }

    private void pauseCluster() {
        if (!cluster().clusterAvailabilityStatus().equalsIgnoreCase("paused")) {
            checkSnapshots();
            try {
                redshiftClient.pauseCluster(builder -> builder.clusterIdentifier(account.clusterIdentifier()).build());
                //wait for paused
                WaitUtils
                    .waitFor(() -> cluster().clusterAvailabilityStatus().equalsIgnoreCase("paused"), 60, 30000,
                        "Waiting for Cluster " + cluster().clusterIdentifier() + " to be paused");
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

        if (respRecent.snapshots().isEmpty()) {
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
                .waitFor(() -> !cluster().clusterAvailabilityStatus().equalsIgnoreCase("modifying"), 60, 30000,
                    "Waiting for Cluster " + cluster().clusterIdentifier() + " to process the snapshot");
        }
    }

    private void waitForS3Lock() {
        content += "-" + TestConfiguration.user();
        if (System.getenv("BUILD_URL") != null) {
            content += "-" + System.getenv("BUILD_URL");
        }

        if (!s3.validation().bucketExists(S3_BUCKET)) {
            s3.validation().createS3Bucket(S3_BUCKET);
            s3.validation().setContentExpiration(S3_BUCKET, 1);
        }

        BooleanSupplier check = () -> {
            final List<String> files = s3.validation().listKeysInBucket(S3_BUCKET);
            if (!files.contains(S3_FILE)) {
                s3.validation().createFile(S3_BUCKET, S3_FILE, content);
                // Wait for a while and check if we really have the lock and the file was not overwritten by some other instance
                WaitUtils.sleep(30000L);
                return content.equals(s3.validation().readFileFromBucket(S3_BUCKET, S3_FILE));
            }
            return false;
        };

        WaitUtils.waitFor(check, 360, 60000L, "Trying to acquire RedShift S3 lock");
        LOG.info("Lock acquired");
    }

    private void releaseS3Lock() {
        s3.validation().deleteS3BucketContent(S3_BUCKET);
    }
}
