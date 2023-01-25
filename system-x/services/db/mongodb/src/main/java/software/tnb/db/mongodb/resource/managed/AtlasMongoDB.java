package software.tnb.db.mongodb.resource.managed;

import software.tnb.common.account.AccountFactory;
import software.tnb.db.mongodb.account.managed.AtlasAccount;
import software.tnb.db.mongodb.service.MongoDB;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.stream.Collectors;

public class AtlasMongoDB extends MongoDB {

    private AtlasAccount account;

    @Override
    public AtlasAccount account() {
        if (account == null) {
            account = AccountFactory.create(AtlasAccount.class);
        }
        return account;
    }

    @Override
    protected MongoClient client() {
        if (client == null) {
            this.client = MongoClients.create(String.format(
                "mongodb+srv://%s:%s@%s/%s",
                account().username(),
                account().password(),
                account().replicaSetUrl(),
                account().database()
            ));
        }
        return client;
    }

    @Override
    public String replicaSetUrl() {
        return account().replicaSetUrl();
    }

    @Override
    public String hostname() {
        // camel accepts a comma-separated list of host:port of all replicas so that's what we return here
        return client().getClusterDescription().getServerDescriptions().stream()
            .map(s -> s.getAddress().getHost() + ":" + s.getAddress().getPort())
            .collect(Collectors.joining(","));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // no-op
    }
}
