package software.tnb.aws.redshift.service;

import software.tnb.aws.common.client.AWSClient;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.redshift.account.RedshiftAccount;
import software.tnb.aws.redshift.validation.RedshiftValidation;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
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
        return redshiftClient.describeClusters().clusters().stream()
            .filter(cluster -> cluster.clusterIdentifier().equals(account.clusterIdentifier())).findFirst().get();
    }

    private void resumeCluster() {
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
}
