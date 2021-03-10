package org.jboss.fuse.tnb.common.account;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;

public class Accounts {
    private static Map<String, Map<String, String>> credentials;
    private static ObjectMapper mapper;

    private static void load() {
        try (FileInputStream fs = new FileInputStream(Paths.get(TestConfiguration.credentialsFile()).toAbsolutePath().toString())) {
            credentials = (Map) ((Map) new Yaml().load(fs)).get("services");
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        } catch (IOException e) {
            fail("Unable to load credentials file: " + e);
        }
    }

    /**
     * Create the instance of given account class with values populated from the credentials.
     * @param accountClass account class to create instance of
     * @param <T> return type
     * @return new instance of given class
     */
    public static <T extends Account> T get(Class<T> accountClass) {
        if (credentials == null) {
            load();
        }
        try {
            Function<Account, String> getId = Account::credentialsId;
            String credentialsId = getId.apply(accountClass.getDeclaredConstructor().newInstance());
            if (!credentials.containsKey(credentialsId)) {
                fail("Credentials with id " + credentialsId + " not found in credentials.yaml file");
            }
            return accountClass.cast(mapper.convertValue(credentials.get(credentialsId), accountClass));
        } catch (Exception e) {
            fail("Unable to create instance of " + accountClass.getName() + " class: ", e);
        }
        return null;
    }
}
