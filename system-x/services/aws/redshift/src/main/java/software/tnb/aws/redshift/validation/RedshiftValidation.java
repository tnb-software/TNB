package software.tnb.aws.redshift.validation;

import software.tnb.aws.redshift.account.RedshiftAWSAccount;
import software.tnb.common.service.Validation;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.Assertions;

import java.util.List;

import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.Cluster;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.redshiftdata.model.Field;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultRequest;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultResponse;

public class RedshiftValidation implements Validation {

    private final RedshiftClient redshiftClient;
    private final RedshiftDataClient redshiftDataClient;
    private final RedshiftAWSAccount redshiftAWSAccount;

    //TODO(anyone): trial cluster expires 02/07/2022, currently uses default setup - cluster identifier ("redshift-cluster-1"), user ("awsuser")
    // and database ("dev")

    public RedshiftValidation(RedshiftClient redshiftClient, RedshiftDataClient redshiftDataClient, RedshiftAWSAccount redshiftAWSAccount) {
        this.redshiftClient = redshiftClient;
        this.redshiftDataClient = redshiftDataClient;
        this.redshiftAWSAccount = redshiftAWSAccount;
    }

    public String execSql(String sql) {
        Cluster cluster = redshiftClient.describeClusters().clusters().stream()
            .filter(c -> c.clusterIdentifier().equals(redshiftAWSAccount.redshiftClusterIdentifier())).findFirst().get();
        String responseId = redshiftDataClient.executeStatement(
            ExecuteStatementRequest.builder()
                .clusterIdentifier(redshiftAWSAccount.redshiftClusterIdentifier())
                .database(cluster.dbName())
                .dbUser(cluster.masterUsername())
                .sql(sql).build()).id();
        WaitUtils.waitFor(
            () -> {
                switch (getStatementStatus(responseId).toUpperCase()) {
                    case "FINISHED":
                        return true;
                    case "FAILED":
                        Assertions.fail(String.format("Failure executing sql statement: '%s'", sql));
                    default:
                        return false;
                }
            }, 20,
            5000, "waiting until the statement " + sql + " is finished");
        return responseId;
    }

    public List<List<Field>> records(String responseId) {
        GetStatementResultResponse
            response = redshiftDataClient.getStatementResult(GetStatementResultRequest.builder().id(responseId).build());
        return response.records();
    }

    public String getStatementStatus(String responseId) {
        return redshiftDataClient.describeStatement(builder -> builder.id(responseId).build()).statusAsString();
    }
}
