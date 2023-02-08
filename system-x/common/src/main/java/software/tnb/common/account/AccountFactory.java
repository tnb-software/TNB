package software.tnb.common.account;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.common.account.loader.CredentialsLoader;
import software.tnb.common.account.loader.VaultCredentialsLoader;
import software.tnb.common.account.loader.YamlCredentialsLoader;
import software.tnb.common.config.TestConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class AccountFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AccountFactory.class);

    private static CredentialsLoader loader;

    private AccountFactory() {
    }

    /**
     * Create the instance of given account class with values populated from the credentials.
     *
     * @param accountClass account class to create instance of
     * @param <T> return type
     * @return new instance of given class
     */
    public static <T extends Account> T create(Class<T> accountClass) {
        T instance = createInstance(accountClass);
        if (instance instanceof WithId) {
            LOG.debug("Loading {} account", accountClass.getSimpleName());
            if (loader == null) {
                try {
                    createLoader();
                } catch (Exception e) {
                    fail("Could not load credentials", e);
                }
            }
            return loader.get(getCredentialsIds(instance), accountClass);
        } else {
            LOG.debug("Initialization of {}. No credentials loading needed.", accountClass.getSimpleName());
            return instance;
        }
    }

    private static void createLoader() throws Exception {
        if (TestConfiguration.useVault()) {
            if (TestConfiguration.vaultToken() != null) {
                LOG.info("Logging into vault using github token");
                loader = new VaultCredentialsLoader(
                    TestConfiguration.vaultAddress(),
                    TestConfiguration.vaultPathPattern(),
                    TestConfiguration.vaultToken()
                );
            } else {
                LOG.info("Logging into vault using approle");
                loader = new VaultCredentialsLoader(
                    TestConfiguration.vaultAddress(),
                    TestConfiguration.vaultPathPattern(),
                    TestConfiguration.vaultRoleId(),
                    TestConfiguration.vaultSecretId()
                );
            }
        } else {
            if (TestConfiguration.credentials() != null) {
                LOG.info("loading credentials from property");
                loader = new YamlCredentialsLoader(TestConfiguration.credentials());
            } else {
                LOG.info("Loading credentials from file");
                loader = new YamlCredentialsLoader(new File(TestConfiguration.credentialsFile()));
            }
        }
    }

    private static <T extends Account> List<String> getCredentialsIds(T instance) {
        Function<WithId, String> withId = WithId::getId;

        List<String> ids = new ArrayList<>();

        Class<?> current = instance.getClass();
        while (current != null) {
            if (WithId.class.isAssignableFrom(current)) {
                ids.add(withId.apply((WithId) createInstance(current)));
            }
            current = current.getSuperclass();
        }
        Collections.reverse(ids);
        return ids;
    }

    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of " + clazz.getName() + " class: ", e);
        }
    }
}
