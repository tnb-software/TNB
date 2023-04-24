package software.tnb.db.dballocator.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.db.dballocator.configuration.DbAllocatorConfiguration;
import software.tnb.db.dballocator.validation.SqlValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.jboss.qa.dballoc.api.allocator.DbAllocatorResource;
import org.jboss.qa.dballoc.api.allocator.entity.JaxbAllocation;
import org.jboss.qa.dballoc.client.RestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Optional;

@AutoService(DbAllocator.class)
public class DbAllocator extends Service<NoAccount, NoClient, SqlValidation> {

    private static final String API = DbAllocatorConfiguration.getUrl();
    private static final Logger LOG = LoggerFactory.getLogger(DbAllocator.class);

    private final DbAllocatorResource resource;

    private Optional<JaxbAllocation> allocation = Optional.empty();

    public DbAllocator() {
        resource = RestClientFactory.getDbAllocatorResourceRestClient(API + "/allocator-rest/api/");
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        allocation.ifPresent(a -> {
            LOG.info("Free database: {}", a.getUuid());
            resource.free(a.getUuid().toString());
        });
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        final String label = DbAllocatorConfiguration.getLabel();
        LOG.info("Allocate '{}' database", label);
        allocation = Optional.of(resource.allocate(label,
            DbAllocatorConfiguration.getRequestee(),
            DbAllocatorConfiguration.getExpire(),
            DbAllocatorConfiguration.getErase())
        );
        allocation.ifPresent(a -> LOG.info("Database '{}' has been allocated {}", label, a.getUuid()));
        validation = allocation.map(i -> new SqlValidation(i, RestClientFactory.getSqlExecutorRestClient(API + "/sql-executor/api/"))).get();
    }

    public Optional<JaxbAllocation> getAllocation() {
        return allocation;
    }
}
