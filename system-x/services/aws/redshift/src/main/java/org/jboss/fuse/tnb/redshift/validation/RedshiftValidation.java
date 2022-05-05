package org.jboss.fuse.tnb.redshift.validation;

import org.jboss.fuse.tnb.common.service.Validation;

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
    //TODO(anyone): trial cluster expires 02/07/2022, currently uses default setup - cluster identifier ("redshift-cluster-1"), user ("awsuser")
    // and database ("dev")

    public RedshiftValidation(RedshiftClient redshiftClient, RedshiftDataClient redshiftDataClient) {
        this.redshiftClient = redshiftClient;
        this.redshiftDataClient = redshiftDataClient;
    }

    public String execSql(String sql) {
        Cluster cluster = redshiftClient.describeClusters().clusters().get(0);
        return redshiftDataClient.executeStatement(
            ExecuteStatementRequest.builder()
                .clusterIdentifier(cluster.clusterIdentifier())
                .database(cluster.dbName())
                .dbUser(cluster.masterUsername())
                .sql(sql).build()).id();
    }

    public List<List<Field>> records(String responseId) {
        GetStatementResultResponse
            response = redshiftDataClient.getStatementResult(GetStatementResultRequest.builder().id(responseId).build());
        return response.records();
    }

    public String describeStmtStatus(String responseId) {
        return redshiftDataClient.describeStatement(builder -> builder.id(responseId).build()).statusAsString();
    }
}
