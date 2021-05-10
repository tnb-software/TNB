package org.jboss.fuse.tnb.common.account;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.common.config.TestConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class Accounts {
    private static final Logger LOG = LoggerFactory.getLogger(Accounts.class);

    private static Map<String, Map<String, Object>> credentials;
    private static ObjectMapper mapper;

    /**
     * Loads the credentials file into a map.
     */
    private static void load() {
        try (FileInputStream fs = new FileInputStream(Paths.get(TestConfiguration.credentialsFile()).toAbsolutePath().toString())) {
            LOG.info("Loading credentials file from {}", Paths.get(TestConfiguration.credentialsFile()).toAbsolutePath());
            credentials = (Map) ((Map) new Yaml().load(fs)).get("services");
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        } catch (IOException e) {
            fail("Unable to load credentials file: " + e);
        }
    }

    /**
     * Create the instance of given account class with values populated from the credentials.
     *
     * @param accountClass account class to create instance of
     * @param <T> return type
     * @return new instance of given class
     */
    public static <T extends Account> T get(Class<T> accountClass) {
        try {
            Function<WithId, String> getId = WithId::getId;
            T instance = accountClass.getDeclaredConstructor().newInstance();
            if (instance instanceof WithId) {
                LOG.debug("Loading {} account from credentials file", accountClass.getSimpleName());
                if (credentials == null) {
                    load();
                }
                String credentialsId = getId.apply((WithId) instance);
                if (!credentials.containsKey(credentialsId)) {
                    fail("Credentials with id " + credentialsId + " not found in credentials.yaml file");
                }
                return accountClass.cast(mapper.convertValue(credentials.get(credentialsId).get("credentials"), accountClass));
            } else {
                LOG.debug("Initialization of {}. No credentials loading needed.", accountClass.getSimpleName());
                return instance;
            }
        } catch (Exception e) {
            fail("Unable to create instance of " + accountClass.getName() + " class: ", e);
        }
        return null;
    }

    public static Properties getCredentialsOfService(String credentialsId) {
        if (credentials == null) {
            load();
        }
        if (!credentials.containsKey(credentialsId)) {
            fail("Credentials with id " + credentialsId + " not found in credentials.yaml file");
        }
        Properties credentialsIdProps = new Properties();
        Map<String, Object> credentialsIdMap = (Map<String, Object>) credentials.get(credentialsId).get("credentials");
        credentialsIdProps.putAll(credentialsIdMap);
        return credentialsIdProps;
    }
}
