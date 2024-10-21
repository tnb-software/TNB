package software.tnb.aws.redshift.validation;

import software.tnb.aws.redshift.account.RedshiftAccount;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.validation.Validation;

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
    private final RedshiftAccount redshiftAccount;

    //TODO(anyone): trial cluster expires 02/07/2022, currently uses default setup - cluster identifier ("redshift-cluster-1"), user ("awsuser")
    // and database ("dev")

    public RedshiftValidation(RedshiftClient redshiftClient, RedshiftDataClient redshiftDataClient, RedshiftAccount redshiftAccount) {
        this.redshiftClient = redshiftClient;
        this.redshiftDataClient = redshiftDataClient;
        this.redshiftAccount = redshiftAccount;
    }

    public String executeQuery(String query) {
        Cluster cluster = redshiftClient.describeClusters().clusters().stream()
            .filter(c -> c.clusterIdentifier().equals(redshiftAccount.clusterIdentifier())).findFirst().get();
        String responseId = redshiftDataClient.executeStatement(
            ExecuteStatementRequest.builder()
                .clusterIdentifier(redshiftAccount.clusterIdentifier())
                .database(cluster.dbName())
                .dbUser(cluster.masterUsername())
                .sql(query).build()).id();
        WaitUtils.waitFor(
            () -> switch (getStatementStatus(responseId).toUpperCase()) {
                case "FINISHED" -> true;
                case "FAILED" -> throw new RuntimeException(String.format("Failure executing sql statement: '%s'", query));
                default -> false;
            }, 20,
            5000, "Waiting until the statement " + query + " is finished");
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
