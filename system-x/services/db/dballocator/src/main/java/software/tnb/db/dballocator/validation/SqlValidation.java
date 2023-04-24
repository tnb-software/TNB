package software.tnb.db.dballocator.validation;

import software.tnb.common.validation.Validation;

import org.jboss.qa.dballoc.api.allocator.entity.JaxbAllocation;
import org.jboss.qa.dballoc.api.executor.SqlExecutor;
import org.jboss.qa.dballoc.api.executor.SqlRequest;
import org.jboss.qa.dballoc.api.executor.SqlResponse;
import org.jboss.resteasy.client.ClientResponse;

import java.util.Arrays;
import java.util.List;

public class SqlValidation implements Validation {

    private JaxbAllocation allocation;
    private SqlExecutor executor;

    public SqlValidation(JaxbAllocation allocation, SqlExecutor executor) {
        this.allocation = allocation;
        this.executor = executor;
    }

    public SqlResponse executeSQL(boolean autocommit, List<String> sqlCommands) {

        final SqlRequest request = new SqlRequest(
            allocation.getAllocationProperties().getProperties().get("db.jdbc_url"),
            allocation.getAccount().getUsername(),
            allocation.getAccount().getPassword(),
            autocommit,
            sqlCommands
        );

        ClientResponse<SqlResponse> r = null;

        try {
            r = (ClientResponse) this.executor.execute(request, true, 60);

            if (r.getStatus() >= 400) {
                throw new AssertionError("Sql execution operation failed. Status code " + r.getStatus()
                    + ", reason: " + r.getEntity(SqlResponse.class).getError());
            }
            return r.getEntity(SqlResponse.class);
        } finally {
            if (r != null) {
                r.releaseConnection();
            }
        }
    }

    public SqlResponse executeSQL(boolean autocommit, String... sqlCommands) {
        return executeSQL(autocommit, Arrays.asList(sqlCommands));
    }

    public SqlResponse executeSQL(List<String> sqlCommands) {
        return executeSQL(true, sqlCommands);
    }

    public SqlResponse executeSQL(String... sqlCommands) {
        return executeSQL(true, Arrays.asList(sqlCommands));
    }
}
