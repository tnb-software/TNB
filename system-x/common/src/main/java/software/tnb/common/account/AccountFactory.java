package software.tnb.common.account;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.common.account.loader.CredentialsLoader;
import software.tnb.common.account.loader.DelegatingCredentialsLoader;
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
                    loader = defaultLoader();
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

    public static CredentialsLoader defaultLoader() throws Exception {
        List<CredentialsLoader> availableLoaders = new ArrayList<>();
        if (TestConfiguration.vaultToken() != null) {
            availableLoaders.add(new VaultCredentialsLoader(
                TestConfiguration.vaultAddress(),
                TestConfiguration.vaultPathPattern(),
                TestConfiguration.vaultToken()
            ));
        }
        if (TestConfiguration.vaultRoleId() != null && TestConfiguration.vaultSecretId() != null) {
            availableLoaders.add(new VaultCredentialsLoader(
                TestConfiguration.vaultAddress(),
                TestConfiguration.vaultPathPattern(),
                TestConfiguration.vaultRoleId(),
                TestConfiguration.vaultSecretId()
            ));
        }
        if (TestConfiguration.credentials() != null) {
            availableLoaders.add(new YamlCredentialsLoader(TestConfiguration.credentials()));
        }
        if (TestConfiguration.credentialsFile() != null) {
            availableLoaders.add(new YamlCredentialsLoader(new File(TestConfiguration.credentialsFile())));
        }
        return new DelegatingCredentialsLoader(availableLoaders);
    }

    public static void setCredentialsLoader(CredentialsLoader l) {
        loader = l;
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
