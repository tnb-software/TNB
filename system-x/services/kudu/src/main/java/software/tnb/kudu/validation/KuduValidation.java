package software.tnb.kudu.validation;

import software.tnb.common.validation.Validation;
import software.tnb.kudu.resource.client.KuduClient;

import java.util.List;
import java.util.Map;

public class KuduValidation implements Validation {

    private final KuduClient client;

    public KuduValidation(KuduClient client) {
        this.client = client;
    }

    public String getLeaderMaster() {
        return client.findLeaderMasterServer();
    }

    public boolean tableExists(String tableName) {
        return client.tableExists(tableName);
    }

    /**
     * example of definition
     * <span>
     *  { "table_name": "test", "schema": { "columns": [ { "column_name": "id", "column_type": "INT32", "default_value": "1" }
     *  , { "column_name": "key", "column_type": "INT64", "is_nullable": false, "comment": "range partition column" }
     *  , { "column_name": "name", "column_type": "STRING", "is_nullable": false, "comment": "user name" } ]
     *  , "key_column_names": ["id", "key"] }, "partition": { "hash_partitions": [{"columns": ["id"], "num_buckets": 2, "seed": 8}]
     *  , "range_partition": { "columns": ["key"], "range_bounds": [ { "lower_bound": {"bound_type": "inclusive", "bound_values": ["2"]}
     *  , "upper_bound": {"bound_type": "exclusive", "bound_values": ["3"]} }
     *  , { "lower_bound": {"bound_type": "inclusive", "bound_values": ["3"]} } ] } }
     *  , "extra_configs": { "configs": { "kudu.table.history_max_age_sec": "3600" } }, "comment": "a test table", "num_replicas": 3 }
     * </span>
     *
     * @param tableDefinition {@link Map}
     */
    public void createTable(Map<String, Object> tableDefinition) {
        assert tableDefinition != null;
        assert tableDefinition.get("table_name") != null;
        assert tableDefinition.get("schema") != null;
        client.createTable(tableDefinition);
    }

    /**
     * Return the rows of a table
     * @param tableName
     * @param columns Optional
     * @param predicates Optional
     * @return List of String, the scanned rows in a raw format
     */
    public List<String> listRows(String tableName, List<String> columns, String predicates) {
        return client.listRows(tableName, columns, predicates);
    }
}
