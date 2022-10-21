package software.tnb.google.cloud.bigquery.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BigQueryValidation {

    private static final Logger LOG = LoggerFactory.getLogger(BigQueryValidation.class);
    private BigQuery bigQuery;
    private String projectId;

    public BigQueryValidation(BigQuery bigQuery, String projectId) {
        this.bigQuery = bigQuery;
        this.projectId = projectId;
    }

    public void createTableWithSampleSchema(String dataSetId, String tableId) {
        createTable(dataSetId, tableId, createSampleSchema());
    }

    public void createTable(String dataSetId, String tableId, Schema schema) {
        TableId id = TableId.of(projectId, dataSetId, tableId);
        TableDefinition.Builder builder = StandardTableDefinition.newBuilder().setSchema(schema);
        TableInfo tableInfo = TableInfo.of(id, builder.build());
        bigQuery.create(tableInfo);
    }

    public void deleteTable(String datasetName, String tableName) {
        LOG.debug("Deleting BQ table " + tableName);
        bigQuery.delete(TableId.of(projectId, datasetName, tableName));
    }

    public void createDataset(String datasetName) {
        DatasetInfo datasetInfo = DatasetInfo.newBuilder(datasetName).build();

        Dataset newDataset = bigQuery.create(datasetInfo);
        String newDatasetName = newDataset.getDatasetId().getDataset();
    }

    public void deleteDataset(String datasetName) {
        DatasetId datasetId = DatasetId.of(projectId, datasetName);
        bigQuery.delete(datasetId, BigQuery.DatasetDeleteOption.deleteContents());
    }

    public long tableRowsCount(String datasetName, String tableName) {
        String query =
            "SELECT * FROM `" + projectId + "." + datasetName + "." + tableName + "`";
        try {
            return bigQuery.query(QueryJobConfiguration.of(query)).getTotalRows();
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to query the BQ table", e);
        }
    }

    public boolean tableContainsRow(String datasetName, String tableName, Map<String, String> row) {
        String query = "SELECT * FROM `" + projectId + "." + datasetName + "." + tableName + "` WHERE "
            + row.entrySet().stream()
            .map(e -> e.getKey() + " = '" + e.getValue() + "'")
            .collect(Collectors.joining(" AND "));
        LOG.debug("Query: {}", query);
        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration.of(query);
        try {
            return bigQuery.query(queryJobConfiguration).getTotalRows() == 1;
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to query the BQ table", e);
        }
    }

    public Schema createSchema(Map<String, StandardSQLTypeName> schema) {
        FieldList fields = FieldList.of(
            schema.entrySet().stream().map(entry -> Field.of(entry.getKey(), entry.getValue())
            ).collect(Collectors.toList()));
        return Schema.of(fields);
    }

    public Schema createSampleSchema() {
        Map<String, StandardSQLTypeName> schema = new HashMap<>();
        schema.put("id", StandardSQLTypeName.STRING);
        schema.put("field", StandardSQLTypeName.STRING);
        return createSchema(schema);
    }
}
