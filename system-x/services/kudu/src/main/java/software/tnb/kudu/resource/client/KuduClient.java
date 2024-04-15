package software.tnb.kudu.resource.client;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface KuduClient extends Closeable {

    String findLeaderMasterServer();

    boolean tableExists(String tableName);

    void createTable(Map<String, Object> tableDefinition);

    List<String> listRows(String tableName, List<String> columns, String predicates);
}
