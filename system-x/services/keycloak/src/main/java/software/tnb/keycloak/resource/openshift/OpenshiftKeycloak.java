package software.tnb.keycloak.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.keycloak.resource.openshift.generated.Keycloak;
import software.tnb.keycloak.resource.openshift.generated.KeycloakSpec;
import software.tnb.keycloak.resource.openshift.generated.KeycloakStatus;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.Db;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.Hostname;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.Http;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.Unsupported;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.db.PasswordSecret;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.db.UsernameSecret;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.unsupported.PodTemplate;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.unsupported.podtemplate.Spec;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.unsupported.podtemplate.spec.Containers;
import software.tnb.keycloak.resource.openshift.generated.keycloakspec.unsupported.podtemplate.spec.containers.Env;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.certmanager.api.model.v1.Certificate;
import io.fabric8.certmanager.api.model.v1.CertificateBuilder;
import io.fabric8.certmanager.api.model.v1.Issuer;
import io.fabric8.certmanager.api.model.v1.IssuerBuilder;
import io.fabric8.certmanager.api.model.v1.SelfSignedIssuer;
import io.fabric8.certmanager.client.CertManagerClient;
import io.fabric8.kubernetes.api.model.Duration;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.openshift.api.model.Route;

@AutoService(software.tnb.keycloak.service.Keycloak.class)
public class OpenshiftKeycloak extends software.tnb.keycloak.service.Keycloak
    implements OpenshiftDeployable, WithInClusterHostname, WithExternalHostname, WithOperatorHub, WithName {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftKeycloak.class);
    private static final String postgresDBName = "postgres-db";
    private static final String serviceAccountName = "postgres-sa";
    private static final String sccName = "postgres-scc";

    @Override
    public String host() {
        return isLocalHostname() ? externalHostname() : inClusterHostname();
    }

    @Override
    public int port() {
        return isLocalHostname() ? 443 : 8443;
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().statefulSets().delete();
        OpenshiftClient.get().resources(Keycloak.class).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("app.kubernetes.io/managed-by", "keycloak-operator").isEmpty());
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void create() {
        LOG.debug("Creating postgres database");
        createPosgredDB();

        LOG.debug("Creating keycloak operator");
        OpenshiftClient.get().createNamespace(targetNamespace());
        // Create subscription for keycloak operator
        createSubscription();

        LOG.debug("Self signed issuer creation");
        createSelfSignedIssuer();

        LOG.debug("Certificate creation");
        createSelfSignedCertificate("keycloak", "keycloak-tls", getDNSList().get(0),
            List.of("server auth"), getDNSList());

        LOG.debug("Creating keycloak instance");
        OpenshiftClient.get().resources(Keycloak.class).resource(createKeycloakCR()).create();
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods("app", "keycloak");
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0))
            && OpenshiftClient.get().secrets()
                .inNamespace(targetNamespace()).list().getItems().stream()
                .anyMatch(secret -> "keycloak-tls".equals(secret.getMetadata().getName()));
    }

    private Keycloak createKeycloakCR() {
        Keycloak keycloak = new Keycloak();

        KeycloakSpec spec = new KeycloakSpec();
        keycloak.getMetadata().setName(name());
        keycloak.getMetadata().setNamespace(targetNamespace());
        keycloak.getMetadata().setLabels(Map.of("app", "keycloak", "app.kubernetes.io/instance", name()));
        spec.setImage(defaultImage());
        Unsupported unsupportedSpec = new Unsupported();
        PodTemplate podTemplate = new PodTemplate();
        Spec podTemplateSpec = new Spec();
        podTemplateSpec.setContainers(List.of(new Containers()));
        podTemplate.setSpec(podTemplateSpec);
        unsupportedSpec.setPodTemplate(podTemplate);
        unsupportedSpec.getPodTemplate().getSpec().getContainers().get(0).setEnv(envs());
        spec.setUnsupported(unsupportedSpec);
        spec.setStartOptimized(false);
        Http http = new Http();
        http.setTlsSecret("keycloak-tls");
        http.setHttpEnabled(true);
        spec.setHttp(http);
        Db database = getDb();
        Hostname hostname = new Hostname();
        hostname.setHostname(OpenshiftClient.get().generateHostname("keycloak"));
        hostname.setStrict(false);
        hostname.setStrictBackchannel(true);
        spec.setHostname(hostname);
        spec.setInstances(1L);
        spec.setDb(database);
        keycloak.setSpec(spec);
        KeycloakStatus status = new KeycloakStatus();
        status.setSelector("app=keycloak,app.kubernetes.io/managed-by=keycloak-operator,app.kubernetes.io/instance=" + name());
        keycloak.setStatus(status);
        return keycloak;
    }

    private static Db getDb() {
        Db database = new Db();
        database.setDatabase("keycloak");
        database.setHost("keycloak-db-postgres");
        UsernameSecret usernameSecret = new UsernameSecret();
        usernameSecret.setKey("username");
        usernameSecret.setName("keycloak-db-credentials");
        PasswordSecret passwordSecret = new PasswordSecret();
        passwordSecret.setKey("password");
        passwordSecret.setName("keycloak-db-credentials");
        database.setUsernameSecret(usernameSecret);
        database.setPasswordSecret(passwordSecret);
        database.setPort(Long.parseLong("5432"));
        return database;
    }

    private List<Env> envs() {
        Env envBoostrapAdminUsername = new Env();
        envBoostrapAdminUsername.setName("KC_BOOTSTRAP_ADMIN_USERNAME");
        envBoostrapAdminUsername.setValue("admin");
        Env envBoostrapAdminPassword = new Env();
        envBoostrapAdminPassword.setName("KC_BOOTSTRAP_ADMIN_PASSWORD");
        envBoostrapAdminPassword.setValue("admin");
        Env envHttpPort = new Env();
        envHttpPort.setName("KC_HTTP_PORT");
        envHttpPort.setValue("8080");
        Env envHttpsPort = new Env();
        envHttpsPort.setName("KC_HTTPS_PORT");
        envHttpsPort.setValue("8443");
        Env envHostnameStrictHttps = new Env();
        envHostnameStrictHttps.setName("KC_HOSTNAME_STRICT_HTTPS");
        envHostnameStrictHttps.setValue("false");
        Env envProxy = new Env();
        envProxy.setName("KC_PROXY");
        envProxy.setValue("passthrough");
        Env envHealthEnabled = new Env();
        envHealthEnabled.setName("KC_HEALTH_ENABLED");
        envHealthEnabled.setValue("true");
        Env envTlsVerifier = new Env();
        envTlsVerifier.setName("KC_TLS_HOSTNAME_VERIFIER");
        envTlsVerifier.setValue("ANY");
        return List.of(envBoostrapAdminUsername, envBoostrapAdminPassword, envHttpPort, envHttpsPort,
           envHostnameStrictHttps, envTlsVerifier , envProxy, envHealthEnabled);
    }

    private List<EnvVar> postgresEnvs() {
        EnvVar postgresUserEnv = new EnvVar("POSTGRES_USER", "admin", null);
        EnvVar postgresPasswordEnv = new EnvVar("POSTGRES_PASSWORD", "admin", null);
        EnvVar postgresDataEnv = new EnvVar("PG_DATA", "/data/pgdata", null);
        EnvVar postgresDBEnv = new EnvVar("POSTGRESDB", "keycloak", null);
        return List.of(postgresUserEnv, postgresPasswordEnv, postgresDataEnv, postgresDBEnv);
    }

    private void createPosgredDB() {
        OpenshiftClient.get().serviceAccounts().resource(new ServiceAccountBuilder()
                .withNewMetadata()
                    .withName(serviceAccountName)
                .endMetadata()
                .build()
            ).serverSideApply();

        OpenshiftClient.get().addUsersToSecurityContext(
                OpenshiftClient.get().createSecurityContext(sccName, "anyuid", "SYS_CHROOT"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName));

        OpenshiftClient.get().secrets().createOrReplace(new SecretBuilder()
            .editOrNewMetadata()
            .withName("keycloak-db-credentials")
            .endMetadata()
            .withData(Map.of("username", Base64.getEncoder().encodeToString("admin".getBytes()),
                "password", Base64.getEncoder().encodeToString("admin".getBytes())))
            .withType("Opaque")
        .build());

        LOG.debug("Creating Stateful Set {}", postgresDBName);
        OpenshiftClient.get().apps().statefulSets().resource(new StatefulSetBuilder()
            .editOrNewMetadata()
                .withName(postgresDBName)
            .endMetadata()
            .editOrNewSpec()
                .withServiceName(postgresDBName)
                .withReplicas(1)
                .withNewSelector()
                    .addToMatchLabels("app", postgresDBName)
                .endSelector()
                .editOrNewTemplate()
                    .editOrNewMetadata()
                        .addToLabels("app", postgresDBName)
                    .endMetadata()
                    .editOrNewSpec()
                        .addNewContainer()
                            .withName(postgresDBName)
                            .withImage("quay.io/fuse_qe/postgres:latest")
                            .addAllToEnv(postgresEnvs())
                        .endContainer()
                        .withServiceAccount(serviceAccountName)
                    .endSpec()
                .endTemplate()
            .endSpec()
            .build()
        ).serverSideApply();

        LOG.debug("Creating service {}", postgresDBName);
        OpenshiftClient.get().services().resource(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(postgresDBName)
            .endMetadata()
            .editOrNewSpec()
                .withSelector(Map.of("app" , postgresDBName))
                .withPorts(new ServicePort("TCP", "tcp" + postgresDBName, null, 5432, "TCP", new IntOrString(5432)))
            .endSpec()
            .build()).serverSideApply();
    }

    public void createSelfSignedIssuer() {
        Issuer issuer = new IssuerBuilder()
            .withNewMetadata()
            .withName("self-signer")
            .withLabels(Map.of("app.kubernetes.io/instance", "cert-issuer"))
            .endMetadata()
            .withNewSpec()
            .withSelfSigned(new SelfSignedIssuer())
            .endSpec()
            .build();

        CertManagerClient cert = OpenshiftClient.get().adapt(CertManagerClient.class);
        cert.v1().issuers().inNamespace(OpenshiftClient.get().getNamespace()).resource(issuer).create();
    }

    public void createSelfSignedCertificate(String name, String secretName, String commonName, List<String> usages
        , List<String> dnsNames) {

        final String passwordSecretName = "kc-keystore-secret";

        final Map<String, String> data = Map.of(
            "password", encode(account().truststorePassword().getBytes())
        );

        SecretBuilder sb = new SecretBuilder().editOrNewMetadata().withName(passwordSecretName).endMetadata().withData(data);
        OpenshiftClient.get().secrets().createOrReplace(sb.build());

        try {
            // @formatter:off
            Certificate certificate = new CertificateBuilder()
                .withNewMetadata()
                    .withName(name)
                .endMetadata()
                .withNewSpec()
                    .withSecretName(secretName)
                    .withDuration(Duration.parse("2160h"))
                    .withRenewBefore(Duration.parse("360h"))
                    .withNewSubject()
                        .withOrganizations(OpenshiftClient.get().getNamespace())
                    .endSubject()
                    .withCommonName(commonName)
                    .withIsCA(Boolean.FALSE)
                    .withNewPrivateKey("RSA", "PKCS1", null, 2048)
                    .withUsages(usages)
                    .withDnsNames(dnsNames)
                    .withNewIssuerRef("cert-manager.io", "Issuer", "self-signer")
                    .withNewKeystores()
                        .withNewJks()
                            .withCreate(true)
                            .withNewPasswordSecretRef("password", passwordSecretName)
                        .endJks()
                    .endKeystores()
                .endSpec().build();
            // @formatter:on
            CertManagerClient cert = OpenshiftClient.get().adapt(CertManagerClient.class);
            cert.v1().certificates().inNamespace(OpenshiftClient.get().getNamespace())
                .resource(certificate).create();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getDNSList() {
        return List.of(
            StringUtils.substringBefore(inClusterHostname(), "."),
            inClusterHostname(),
            OpenshiftClient.get().generateHostname("keycloak")
        );
    }

    private String encode(byte[] content) {
        return Base64.getEncoder().encodeToString(content);
    }

    @Override
    public String inClusterHostname() {
        return String.format("%s-service.%s.svc.cluster.local", name(),
                OpenshiftClient.get().getNamespace());
    }

    @Override
    public String externalHostname() {
        final List<Route> routes = OpenshiftClient.get().routes().withLabel("app", "keycloak").list().getItems();

        if (routes.size() != 1) {
            throw new RuntimeException("Expected single route to be present but was " + routes.size());
        }

        return routes.get(0).getSpec().getHost();
    }

    @Override
    public String name() {
        return "keycloak";
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> p.getMetadata().getLabels() != null && p.getMetadata().getLabels().containsKey("app.kubernetes.io/instance")
            && name().equals(p.getMetadata().getLabels().get("app.kubernetes.io/instance"));
    }

    @Override
    public String operatorChannel() {
        return "fast";
    }

    @Override
    public String operatorName() {
        return "keycloak-operator";
    }

    @Override
    public String operatorCatalog() {
        return "community-operators";
    }
}
