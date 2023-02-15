package software.tnb.google.cloud.bigquery.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;
import com.google.cloud.bigquery.TableResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BigQueryValidation {

    private static final Logger LOG = LoggerFactory.getLogger(BigQueryValidation.class);
    private final BigQuery bigQuery;
    private final String projectId;

    public BigQueryValidation(BigQuery bigQuery, String projectId) {
        this.bigQuery = bigQuery;
        this.projectId = projectId;
    }

    public void createTable(String dataSetId, String tableId, Schema schema) {
        LOG.debug("Creating table {}", tableId);
        TableId id = TableId.of(projectId, dataSetId, tableId);
        TableDefinition.Builder builder = StandardTableDefinition.newBuilder().setSchema(schema);
        TableInfo tableInfo = TableInfo.of(id, builder.build());
        bigQuery.create(tableInfo);
    }

    public void createTable(String dataSetId, String tableId, FieldList fieldList) {
        createTable(dataSetId, tableId, Schema.of(fieldList));
    }

    public void createTable(String dataSetId, String tableId, Map<String, StandardSQLTypeName> schema) {
        createTable(dataSetId, tableId, FieldList.of(schema.entrySet().stream()
            .map(entry -> Field.of(entry.getKey(), entry.getValue())).collect(Collectors.toList())));
    }

    public void deleteTable(String datasetName, String tableName) {
        LOG.debug("Deleting table {}", tableName);
        bigQuery.delete(TableId.of(projectId, datasetName, tableName));
    }

    public void createDataset(String datasetName) {
        LOG.debug("Creating dataset {}", datasetName);
        bigQuery.create(DatasetInfo.newBuilder(datasetName).build());
    }

    public void deleteDataset(String datasetName) {
        LOG.debug("Deleting dataset {}", datasetName);
        bigQuery.delete(DatasetId.of(projectId, datasetName), BigQuery.DatasetDeleteOption.deleteContents());
    }

    public List<FieldValueList> tableContent(String datasetName, String tableName) {
        return StreamSupport.stream(query("SELECT * FROM `" + projectId + "." + datasetName + "." + tableName + "`").iterateAll().spliterator(),
            false).collect(Collectors.toList());
    }

    private TableResult query(String query) {
        LOG.debug("Query: {}", query);
        QueryJobConfiguration queryJobConfiguration = QueryJobConfiguration.of(query);
        try {
            return bigQuery.query(queryJobConfiguration);
        } catch (InterruptedException e) {
            throw new RuntimeException("Unable to query BigQuery table", e);
        }
    }
}
