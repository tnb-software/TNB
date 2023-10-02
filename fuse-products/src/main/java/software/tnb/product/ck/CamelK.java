package software.tnb.product.ck;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.PropertiesUtils;
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

import org.apache.camel.v1.IntegrationPlatform;
import org.apache.camel.v1.IntegrationPlatformSpec;
import org.apache.camel.v1.Kamelet;
import org.apache.camel.v1.Pipe;
import org.apache.camel.v1.integrationplatformspec.Build;
import org.apache.camel.v1.integrationplatformspec.build.maven.Settings;
import org.apache.camel.v1.integrationplatformspec.build.maven.settings.ConfigMapKeyRef;
import org.apache.camel.v1alpha1.KameletBinding;
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

import cz.xtf.core.openshift.PodShellOutput;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.utils.Serialization;

@AutoService(Product.class)
public class CamelK extends OpenshiftProduct implements KameletOps, BeforeEachCallback, AfterEachCallback {
    private static final Logger LOG = LoggerFactory.getLogger(CamelK.class);

    private Path operatorLogOutput;
    private LogWatch operatorLogWatch;
    private OutputStream logStream = null;

    private final List<String> kamelets = new ArrayList<>();

    // Count of all kamelets from camel-k operator
    private int operatorKameletCount = -1;

    private boolean useGlobalInstallation() {
        return CamelKConfiguration.getConfiguration().useGlobalInstallation();
    }

    private String operatorNamespace() {
        return useGlobalInstallation() ? "openshift-operators" : OpenshiftClient.get().getNamespace();
    }

    @Override
    public void setupProduct() {
        CamelKConfiguration config = CamelKConfiguration.getConfiguration();

        if (!isReady()) {
            if (!useGlobalInstallation()) {
                LOG.info("Deploying Camel-K");
                if (CamelKConfiguration.forceUpstream()) {
                    LOG.warn(
                        "You are going to deploy upstream version of Camel-K. "
                            + "Be aware that upstream Camel-K APIs does not have to be compatible with the PROD ones and this installation can "
                            + "break the cluster for other tests."
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
                    config.subscriptionName(), config.subscriptionSourceNamespace(), OpenshiftClient.get().getNamespace(), false);
                OpenshiftClient.get().waitForInstallPlanToComplete(config.subscriptionName());
            } else {
                LOG.info("Reusing global Camel-K installation.");
            }
        }

        ObjectMeta metadata = new ObjectMetaBuilder()
            .withName(config.integrationPlatformName())
            .withLabels(Map.of("app", "camel-k"))
            .build();

        IntegrationPlatformSpec spec = new IntegrationPlatformSpec();

        Build build = new Build();
        build.setTimeout(config.mavenBuildTimeout() + "m");
        spec.setBuild(build);
        if (config.baseImage() != null) {
            build.setBaseImage(config.baseImage());
        }

        org.apache.camel.v1.integrationplatformspec.build.Maven maven = new org.apache.camel.v1.integrationplatformspec.build.Maven();
        Settings settings = new Settings();
        ConfigMapKeyRef cm = new ConfigMapKeyRef();
        cm.setKey("settings.xml");
        cm.setName(config.mavenSettingsConfigMapName());
        cm.setOptional(false);
        settings.setConfigMapKeyRef(cm);
        maven.setSettings(settings);

        build.setMaven(maven);

        IntegrationPlatform ip = new IntegrationPlatform();
        ip.setMetadata(metadata);
        ip.setSpec(spec);

        if (TestConfiguration.mavenSettings() == null) {
            OpenshiftClient.get().createConfigMap(config.mavenSettingsConfigMapName(), Map.of("settings.xml", Maven.createSettingsXmlFile()));
        } else {
            OpenshiftClient.get().createConfigMap(config.mavenSettingsConfigMapName(),
                Map.of("settings.xml", IOUtils.readFile(Paths.get(TestConfiguration.mavenSettings()))));
        }

        OpenshiftClient.get().resources(IntegrationPlatform.class).delete();
        OpenshiftClient.get().resources(IntegrationPlatform.class).resource(ip).create();

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
        if (operatorKameletCount == -1) {
            final PodShellOutput shellOutput =
                OpenshiftClient.get().inNamespace(operatorNamespace(), c -> c.podShell(c.getLabeledPods("name", "camel-k-operator").get(0))
                    .executeWithBash("ls /kamelets/* | wc -l"));
            if (!shellOutput.getError().isEmpty()) {
                LOG.error("Unable to list all kamelets: {}", shellOutput.getError());
                return false;
            }
            if (shellOutput.getOutput().isEmpty()) {
                LOG.error("Unable to list all kamelets: empty response");
                return false;
            }

            operatorKameletCount = Integer.parseInt(shellOutput.getOutput().trim());
        }

        // https://github.com/fabric8io/kubernetes-client/issues/3852
        Serialization.jsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return OpenshiftClient.get().resources(Kamelet.class).list().getItems().size() >= operatorKameletCount;
    }

    private boolean kameletsReady() {
        return OpenshiftClient.get().resources(Kamelet.class).list().getItems().stream().allMatch(k -> {
            if (k.getStatus() == null) {
                return false;
            }
            return "Ready".equals(k.getStatus().getPhase());
        });
    }

    private boolean platformReady() {
        List<IntegrationPlatform> ip = OpenshiftClient.get().resources(IntegrationPlatform.class).list().getItems();
        return ip.size() == 1 && ip.get(0).getStatus() != null && ip.get(0).getStatus().getPhase().equals("Ready");
    }

    @Override
    public void teardownProduct() {
        if (useGlobalInstallation()) {
            LOG.debug("Skipping product teardown for global operator.");
            return;
        }
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

    public App createPipe(Pipe pipe) {
        return createPipes(new Pipe[] {pipe}).get(pipe.getMetadata().getName());
    }

    public Map<String, App> createPipes(Pipe... pipes) {
        return createIntegration((Object[]) pipes);
    }

    private App createApp(Object integrationSource) {
        App app;
        if (integrationSource instanceof AbstractIntegrationBuilder) {
            app = new CamelKApp((AbstractIntegrationBuilder<?>) integrationSource);
        } else if (integrationSource instanceof KameletBinding) {
            app = new CamelKApp((KameletBinding) integrationSource);
        } else if (integrationSource instanceof Pipe) {
            app = new CamelKApp((Pipe) integrationSource);
        } else {
            throw new IllegalArgumentException("Creating Camel-K integrations is possible only with IntegrationBuilders and KameletBindings (was "
                + integrationSource.getClass().getSimpleName() + ")");
        }
        integrations.put(app.getName(), app);
        return app;
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(
            OpenshiftClient.get().inNamespace(operatorNamespace(), c -> c.getLabeledPods("name", "camel-k-operator")))
            && platformReady() && kameletsDeployed() && kameletsReady();
    }

    @Override
    public void createKamelet(Kamelet kamelet) {
        if (kamelet == null) {
            throw new RuntimeException("Null kamelet");
        }
        LOG.info("Creating Kamelet " + kamelet.getMetadata().getName());
        OpenshiftClient.get().resources(Kamelet.class).resource(kamelet).create();
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
        return OpenshiftClient.get().resources(Kamelet.class).withName(name).get();
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
        OpenshiftClient.get().resources(Kamelet.class).withName(kameletName).delete();
        kamelets.remove(kameletName);
    }

    public void removeKamelets() {
        kamelets.forEach(kamelet -> OpenshiftClient.get().resources(Kamelet.class).withName(kamelet).delete());
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
        Pod operator = OpenshiftClient.get(operatorNamespace()).getLabeledPods("name", "camel-k-operator").get(0);
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
