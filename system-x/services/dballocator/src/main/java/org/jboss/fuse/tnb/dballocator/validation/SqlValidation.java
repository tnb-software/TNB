package org.jboss.fuse.tnb.dballocator.validation;

import org.jboss.qa.dballoc.api.allocator.entity.JaxbAllocation;
import org.jboss.qa.dballoc.api.executor.SqlExecutor;
import org.jboss.qa.dballoc.api.executor.SqlRequest;

import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;

public class SqlValidation {

    private JaxbAllocation allocation;
    private SqlExecutor executor;

    public SqlValidation(JaxbAllocation allocation, SqlExecutor executor) {
        this.allocation = allocation;
        this.executor = executor;
    }

    public Response executeSQL(boolean autocommit, List<String> sqlCommands) {

        SqlRequest request = new SqlRequest(
            allocation.getAllocationProperties().getProperties().get("db.jdbc_url"),
            allocation.getAccount().getUsername(),
            allocation.getAccount().getPassword(),
            autocommit,
            sqlCommands
        );

        return executor.execute(request, true, 60);
    }

    public Response executeSQL(boolean autocommit, String... sqlCommands) {
        return executeSQL(autocommit, Arrays.asList(sqlCommands));
    }

    public Response executeSQL(List<String> sqlCommands) {
        return executeSQL(true, sqlCommands);
    }

    public Response executeSQL(String... sqlCommands) {
        return executeSQL(true, Arrays.asList(sqlCommands));
    }
}
