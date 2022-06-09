package software.tnb.db.dballocator.service;

import software.tnb.db.dballocator.validation.SqlValidation;
import software.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.jboss.qa.dballoc.api.allocator.DbAllocatorResource;
import org.jboss.qa.dballoc.api.allocator.entity.JaxbAllocation;
import org.jboss.qa.dballoc.client.RestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Optional;

@AutoService(DbAllocator.class)
public class DbAllocator implements Service {

    private static final String API = DbAllocatorConfiguration.getUrl();
    private static final Logger LOG = LoggerFactory.getLogger(DbAllocator.class);

    private DbAllocatorResource resource;
    private Optional<SqlValidation> validation;

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
        allocation.ifPresent(a -> {
            LOG.info("Database '{}' has been allocated {}", label, a.getUuid());
        });
        validation = allocation.map(i -> new SqlValidation(i, RestClientFactory.getSqlExecutorRestClient(API + "/sql-executor/api/")));
    }

    public SqlValidation validation() {
        return validation.orElse(null);
    }

    public Optional<JaxbAllocation> getAllocation() {
        return allocation;
    }
}
