package software.tnb.db.mysql.service;

import software.tnb.db.common.account.SQLAccount;
import software.tnb.db.common.service.SQL;
import software.tnb.db.common.validation.SQLValidation;
import software.tnb.db.mysql.account.MySQLAccount;
import software.tnb.db.mysql.account.MySQLRootAccount;

import java.util.Map;

public abstract class MySQL extends SQL {
    protected static final int PORT = 3306;
    private SQLValidation rootValidation;

    @Override
    public String defaultImage() {
        return "registry.redhat.io/rhel8/mysql-80:latest";
    }

    @Override
    protected Class<? extends SQLAccount> accountClass() {
        return MySQLAccount.class;
    }

    @Override
    public String jdbcConnectionUrl() {
        return String.format("jdbc:mysql://%s:%d/%s", hostname(), port(), account().database());
    }

    @Override
    public Map<String, String> containerEnvironment() {
        return Map.of(
            "MYSQL_DATABASE", account().database(),
            "MYSQL_USER", account().username(),
            "MYSQL_PASSWORD", account().password(),
            "MYSQL_ROOT_PASSWORD", "my-secret-pw"
        );
    }

    public SQLValidation rootValidation() {
        if (rootValidation == null) {
            rootValidation = new SQLValidation(localConnectionUrl(), new MySQLRootAccount());
        }
        return rootValidation;
    }
}
