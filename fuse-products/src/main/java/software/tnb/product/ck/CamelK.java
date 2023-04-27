package software.tnb.product.ck;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.PropertiesUtils;
import software.tnb.common.utils.ResourceFunctions;
import software.tnb.common.utils.WaitUtils;
import software.tnb.product.OpenshiftProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.ck.application.CamelKApp;
import software.tnb.product.ck.configuration.CamelKConfiguration;
import software.tnb.product.ck.log.CamelKOperatorLogFilter;
import software.tnb.product.ck.utils.CamelKSupport;
import software.tnb.product.ck.utils.OwnerReferenceSetter;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.interfaces.KameletOps;
import software.tnb.product.rp.Attachments;
import software.tnb.product.util.executor.Executor;
import software.tnb.product.util.maven.Maven;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.auto.service.AutoService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.fabric8.camelk.client.CamelKClient;
import io.fabric8.camelk.v1.IntegrationPlatform;
import io.fabric8.camelk.v1.IntegrationPlatformBuilder;
import io.fabric8.camelk.v1.IntegrationPlatformSpecBuilder;
import io.fabric8.camelk.v1alpha1.Kamelet;
import io.fabric8.camelk.v1alpha1.KameletBinding;
import io.fabric8.kubernetes.api.model.ConfigMapKeySelector;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.base.PatchContext;
import io.fabric8.kubernetes.client.dsl.base.PatchType;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.openshift.api.model.operatorhub.v1alpha1.SubscriptionConfigBuilder;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct implements KameletOps, BeforeEachCallback, AfterEachCallback {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);

    private Path operatorLogOutput;
    private LogWatch operatorLogWatch;
    private OutputStream logStream = null;

    private final List<String> kamelets = new ArrayList<>();

    // Count of all kamelets from camel-k operator
    private int operatorKameletCount = -1;

    protected CamelKClient camelKClient;

    @Override
    public void setupProduct() {
        // Avoid creating static clients in case the camel-k should not be running (all product instances are created in ProductFactory.create()
        // and then the desired one is returned
        camelKClient = OpenshiftClient.get().adapt(CamelKClient.class);

        CamelKConfiguration config = CamelKConfiguration.getConfiguration();

        if (!isReady()) {
            LOG.info("Deploying Camel-K");
            if (CamelKConfiguration.forceUpstream()) {
                LOG.warn(
                    "You are going to deploy upstream version of Camel-K. "
                        + "Be aware that upstream Camel-K APIs does not have to be compatible with the PROD ones and this installation can break the "
                        + "cluster for other tests."
                );
            }
            if (OpenshiftClient.get().operatorHub().catalogSources().inNamespace(config.subscriptionSourceNamespace())
                .withName(config.subscriptionSource()).get() == null) {
                LOG.error("Operator Hub catalog source {} not found! Set {} property to an existing catalog source or create a new catalog source"
                        + " with {} name in {} namespace. Be careful, as if someone else uses the same cluster with a different version,"
                        + " the deployments may fail due changes in CRDs between versions", config.subscriptionSource(),
                    CamelKConfiguration.SUBSCRIPTION_SOURCE, config.subscriptionSource(), config.subscriptionSourceNamespace());
                throw new RuntimeException("Operator Hub catalog source " + config.subscriptionSource() + " not found!");
            }

            OpenshiftClient.get().createSubscription(config.subscriptionChannel(), config.subscriptionOperatorName(), config.subscriptionSource(),
                config.subscriptionName(), config.subscriptionSourceNamespace(), OpenshiftClient.get().getNamespace(), false, null,
                new SubscriptionConfigBuilder().withNewResources().withLimits(Map.of("memory",
                    // TODO(jbouska): Temporary workaround for native builds (it needs investigation why the memory consumption is so high)
                    Quantity.parse("7Gi"))).endResources().build());
            OpenshiftClient.get().waitForInstallPlanToComplete(config.subscriptionName());
        }

        // @formatter:off
        IntegrationPlatform ip = new IntegrationPlatformBuilder()
            .withNewMetadata()
                .withLabels(Map.of("app", "camel-k"))
                .withName(config.integrationPlatformName())
            .endMetadata()
            .build();

        IntegrationPlatformSpecBuilder specBuilder = new IntegrationPlatformSpecBuilder()
            .withNewBuild()
                .withTimeout(config.mavenBuildTimeout())
            .endBuild();

        if (config.baseImage() != null) {
            specBuilder.editBuild()
                .withBaseImage(config.baseImage())
            .endBuild();
        }

        if (TestConfiguration.mavenSettings() == null) {
            OpenshiftClient.get().createConfigMap(config.mavenSettingsConfigMapName(), Map.of("settings.xml", Maven.createSettingsXmlFile()));
        } else {
            OpenshiftClient.get().createConfigMap(config.mavenSettingsConfigMapName(),
                Map.of("settings.xml", IOUtils.readFile(Paths.get(TestConfiguration.mavenSettings()))));
        }

        specBuilder
            .editBuild()
                .withNewMaven()
                    .withNewSettings()
                        .withConfigMapKeyRef(new ConfigMapKeySelector("settings.xml", config.mavenSettingsConfigMapName(), false))
                    .endSettings()
                .endMaven()
            .endBuild()
            .build();
        // @formatter:on

        ip.setSpec(specBuilder.build());

        camelKClient.v1().integrationPlatforms().delete();
        camelKClient.v1().integrationPlatforms().create(ip);

        // TODO(anyone): Remove this workaround after migration of camel-k-client to =< v 6.0.0
        camelKClient.v1().integrationPlatforms().withName(ip.getMetadata().getName())
            .patch(PatchContext.of(PatchType.JSON_MERGE), "{\"spec\":{\"build\":{\"maven\":{\"cliOptions\":[\"-Dquarkus.native"
                + ".native-image-xmx=6g\"]}}}}");

        if (TestConfiguration.streamLogs()) {
            setupLogger();
        }
    }

    /**
     * To be able to display only the related portion of the camel-k operator logs, there is a special filter and this piece of code
     * adds the filter to the LogStream appender
     */
    private void setupLogger() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        ConsoleAppender appender = config.getAppender("LogStream");
        appender.addFilter(CamelKOperatorLogFilter.createFilter(Filter.Result.ACCEPT, Filter.Result.DENY));
        appender.start();

        ctx.updateLoggers();
    }

    private boolean kameletsDeployed() {
        // FIXME verify
        if (operatorKameletCount == -1) {
            var pod = OpenshiftClient.get().pods().withName(
                OpenshiftClient.get().getLabeledPods("name", "camel-k-operator").get(0).getMetadata().getName());

            // FIXME: maybe it would be worth it refactoring into a separate class
            StringWriter writer = new StringWriter();
            CountDownLatch latch = new CountDownLatch(1);
            try (ExecWatch exec = pod
                .writingOutput(new WriterOutputStream(writer, Charset.defaultCharset()))
                .writingError(System.err)
                .usingListener((code, reason) -> latch.countDown())
                .exec("bash", "-c", "ls /kamelets/* | wc -l")) {
                latch.await(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                LOG.error("Unable to list all kamelets", e);
                return false;
            }

            String output = writer.toString();

            if (output.isEmpty()) {
                LOG.error("Unable to list all kamelets: empty response");
                return false;
            }

            operatorKameletCount = Integer.parseInt(output.trim());
        }

        // https://github.com/fabric8io/kubernetes-client/issues/3852
        Serialization.jsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return camelKClient.v1alpha1().kamelets().list().getItems().size() >= operatorKameletCount;
    }

    private boolean kameletsReady() {
        return camelKClient.v1alpha1().kamelets().list().getItems().stream().allMatch(k -> {
            if (k.getStatus() == null) {
                return false;
            }
            return "Ready".equals(k.getStatus().getPhase());
        });
    }

    @Override
    public void teardownProduct() {
        OpenshiftClient.get().deleteSubscription(CamelKConfiguration.getConfiguration().subscriptionName());
        removeKamelets();
    }

    @Override
    protected App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        return this.createIntegration(integrationBuilder);
    }

    @Override
    public App createIntegration(AbstractIntegrationBuilder<?> integrationBuilder) {
        return createIntegration(integrationBuilder, new AbstractIntegrationBuilder[] {}).get(integrationBuilder.getIntegrationName());
    }

    @Override
    public Map<String, App> createIntegration(AbstractIntegrationBuilder<?> integrationBuilder,
        AbstractIntegrationBuilder<?>... integrationBuilders) {
        List<Object> integrationSources = new ArrayList<>();
        integrationSources.add(integrationBuilder);
        integrationSources.addAll(Arrays.asList(integrationBuilders));
        return createIntegration(integrationSources.toArray());
    }

    public Map<String, App> createKameletBindings(KameletBinding... kameletBindings) {
        return createIntegration((Object[]) kameletBindings);
    }

    private Map<String, App> createIntegration(Object... integrationSources) {
        // Return only integrations created in this invocation, not all created integrations
        Map<String, App> apps = new HashMap<>();
        for (Object integrationSource : integrationSources) {
            App app = createApp(integrationSource);
            apps.put(app.getName(), app);
            app.start();
        }

        apps.values().forEach(App::waitUntilReady);
        integrations.putAll(apps);

        return apps;
    }

    public App createKameletBinding(KameletBinding kameletBinding) {
        return createKameletBindings(new KameletBinding[] {kameletBinding}).get(kameletBinding.getMetadata().getName());
    }

    private App createApp(Object integrationSource) {
        App app;
        if (integrationSource instanceof AbstractIntegrationBuilder) {
            app = new CamelKApp((AbstractIntegrationBuilder<?>) integrationSource);
        } else if (integrationSource instanceof KameletBinding) {
            app = new CamelKApp((KameletBinding) integrationSource);
        } else {
            throw new IllegalArgumentException("Creating Camel-K integrations is possible only with IntegrationBuilders and KameletBindings (was "
                + integrationSource.getClass().getSimpleName() + ")");
        }
        integrations.put(app.getName(), app);
        return app;
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("name", "camel-k-operator"))
            && kameletsDeployed() && kameletsReady();
    }

    @Override
    public void createKamelet(Kamelet kamelet) {
        if (kamelet == null) {
            throw new RuntimeException("Null kamelet");
        }
        LOG.info("Creating Kamelet " + kamelet.getMetadata().getName());
        camelKClient.v1alpha1().kamelets().createOrReplace(kamelet);
        kamelets.add(kamelet.getMetadata().getName());
        WaitUtils.waitFor(() -> isKameletReady(kamelet), "Waiting for Kamelet to be ready");
    }

    public boolean isKameletReady(Kamelet kamelet) {
        if (kamelet == null) {
            return false;
        }
        String kameletName = kamelet.getMetadata().getName();
        if (getKameletByName(kameletName) != null && getKameletByName(kameletName).getStatus() != null) {
            return "ready".equalsIgnoreCase(getKameletByName(kameletName).getStatus().getPhase());
        } else {
            return false;
        }
    }

    @Override
    public boolean isKameletReady(String name) {
        return isKameletReady(getKameletByName(name));
    }

    /**
     * Gets kamelet by its name.
     *
     * @param name of Kamelet
     * @return null if Kamelet wasn't found, otherwise Kamelet with given name
     */
    public Kamelet getKameletByName(String name) {
        return camelKClient.v1alpha1().kamelets().withName(name).get();
    }

    /**
     * Create and label secret from credentials to kamelet.
     *
     * @param kameletName name of kamelet
     * @param credentials credentials required by kamelet (keys may contain underscore)
     */
    @Override
    public void createApplicationPropertiesSecretForKamelet(String kameletName, Properties credentials) {
        String prefix = "camel.kamelet." + kameletName + "." + kameletName + ".";
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put(CamelKSupport.CAMELK_CRD_GROUP + "/kamelet", kameletName);
        labels.put(CamelKSupport.CAMELK_CRD_GROUP + "/kamelet.configuration", kameletName);
        Properties camelCaseCredentials = PropertiesUtils.toCamelCaseProperties(credentials);

        // Set the later created integration object as the owner of the secret, so that the secret is deleted together with the integration
        Secret integrationSecret =
            OpenshiftClient.get().createApplicationPropertiesSecret(kameletName + "." + kameletName, camelCaseCredentials, labels, prefix);
        Executor.get().submit(new OwnerReferenceSetter(integrationSecret, kameletName));
    }

    @Override
    public void deleteSecretForKamelet(String kameletName) {
        OpenshiftClient.get().deleteSecret(kameletName + "." + kameletName);
    }

    @Override
    public void removeKamelet(String kameletName) {
        LOG.info("Deleting Kamelet " + kameletName);
        camelKClient.v1alpha1().kamelets().withName(kameletName).delete();
        kamelets.remove(kameletName);
    }

    public void removeKamelets() {
        kamelets.forEach(kamelet -> camelKClient.v1alpha1().kamelets().withName(kamelet).delete());
        kamelets.clear();
    }

    @Override
    public void removeIntegrations() {
        CountDownLatch latch = new CountDownLatch(integrations.size());
        integrations.values().forEach(app -> Executor.get().submit(() -> {
            try {
                app.stop();
            } finally {
                latch.countDown();
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.warn("Latch await thread interrupted");
        }
        integrations.clear();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Pod operator = OpenshiftClient.get().getLabeledPods("name", "camel-k-operator").get(0);
        operatorLogOutput = TestConfiguration.appLocation()
            .resolve(String.format("camel-k-operator-%s.log", extensionContext.getParent().get().getDisplayName()));

        try {
            logStream = new FileOutputStream(operatorLogOutput.toFile());
            operatorLogWatch =
                OpenshiftClient.get().pods().inNamespace(operator.getMetadata().getNamespace()).withName(operator.getMetadata().getName())
                    .tailingLines(0)
                    .watchLog(logStream);
        } catch (FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        operatorLogWatch.close();
        try {
            logStream.close();
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
        Attachments.addAttachment(operatorLogOutput);
    }
}
