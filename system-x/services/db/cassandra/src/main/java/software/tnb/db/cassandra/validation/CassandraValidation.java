package software.tnb.db.cassandra.validation;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CassandraValidation {

    private final CqlSession session;

    public CassandraValidation(CqlSession session) {
        this.session = session;
    }

    public void createKeyspace(String keyspace) {
        CreateKeyspace createKeyspace = SchemaBuilder.createKeyspace(keyspace)
            .ifNotExists()
            .withSimpleStrategy(1);

        session.execute(createKeyspace.build());
        session.execute("USE " + CqlIdentifier.fromCql(keyspace));
    }

    public ResultSet execute(String query) {
        return session.execute(query);
    }

    public Collection<Object> select(String keyspace, String table) {
        session.execute("USE " + keyspace);
        List<Object> result = new ArrayList<>();
        execute("select * from " + table).forEach(result::add);
        return result;
    }
}
