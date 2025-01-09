package software.tnb.certmanager.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.validation.Validation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.fabric8.certmanager.api.model.v1.Certificate;
import io.fabric8.certmanager.api.model.v1.CertificateBuilder;
import io.fabric8.certmanager.client.CertManagerClient;
import io.fabric8.kubernetes.api.model.Duration;
import io.fabric8.kubernetes.api.model.GenericKubernetesResourceBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.rbac.PolicyRule;
import io.fabric8.kubernetes.api.model.rbac.Role;
import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.fabric8.kubernetes.api.model.rbac.RoleBindingBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleRef;
import io.fabric8.kubernetes.api.model.rbac.Subject;
import io.fabric8.kubernetes.client.dsl.RbacAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;

public class CertManagerValidation implements Validation {

    private final CertManagerClient client;

    private static final CustomResourceDefinitionContext ISSUER_CTX = new CustomResourceDefinitionContext
        .Builder()
        .withGroup("cert-manager.io")
        .withKind("Issuer")
        .withName("issuers.cert-manager.io")
        .withPlural("issuers")
        .withScope("Namespaced")
        .withVersion("v1")
        .build();

    private static final CustomResourceDefinitionContext CERTIFICATE_CTX = new CustomResourceDefinitionContext
        .Builder()
        .withGroup("cert-manager.io")
        .withKind("Certificate")
        .withName("certificates.cert-manager.io")
        .withPlural("certificates")
        .withScope("Namespaced")
        .withVersion("v1")
        .build();

    public CertManagerValidation(CertManagerClient client) {
        this.client = client;
    }

    /**
     * Creates self-signed issuer in the current namespace
     */
    public void createSelfSignedIssuer() {
        OpenshiftClient.get().genericKubernetesResources(ISSUER_CTX)
            .inNamespace(OpenshiftClient.get().getNamespace())
            .resource(new GenericKubernetesResourceBuilder()
                .withKind(ISSUER_CTX.getKind())
                .withNewMetadata()
                .withName("selfsigned-issuer")
                .endMetadata()
                .withAdditionalProperties(Map.of("spec", Map.of(
                    "selfSigned", Map.of()
                )))
                .build()
            ).serverSideApply();
    }

    /**
     * Creates self-signed certificate in the current namespace, it requires createSelfSignedIssuer to be called
     * @param name String, the name of the certificate CR
     * @param secretName String, the name of the secret that will contain the certificates
     * @param commonName String, the common name assigned to the certificate
     * @param usages List, the list of the usages according
     * to <a href="https://cert-manager.io/docs/reference/api-docs/#cert-manager.io/v1.KeyUsage">KeyUsage</a>
     * @param dnsNames List, the list of the associated names
     * @param passwordSecretName String, the name of the secret, containing the key `password` to use as password
     */
    public void createSelfSignedCertificate(String name, String secretName, String commonName, List<String> usages
        , List<String> dnsNames, String passwordSecretName) {

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
                    .withNewIssuerRef("cert-manager.io", "Issuer", "selfsigned-issuer")
                    .withNewKeystores()
                        .withNewJks()
                            .withCreate(true)
                            .withNewPasswordSecretRef("password", passwordSecretName)
                        .endJks()
                    .endKeystores()
                .endSpec()
            .build();
            // @formatter:on

            client.v1().certificates().inNamespace(OpenshiftClient.get().getNamespace())
                .resource(certificate).create();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a service account enabled to read secrets in the current namespace.
     * A new service account, role and role binding will be created
     * @param serviceAccount String, the name of the service account
     */
    public void createSecretViewer(String serviceAccount) {
        final String roleName = "secret-viewer";
        final String roleBingingName = "sa-secret-viewer";

        ServiceAccount sa = new ServiceAccountBuilder()
            .withNewMetadata().withName(serviceAccount).endMetadata()
            .withAutomountServiceAccountToken(false)
            .build();
        OpenshiftClient.get().serviceAccounts().inNamespace(OpenshiftClient.get().getNamespace()).resource(sa)
            .serverSideApply();

        List<PolicyRule> policyRuleList = new ArrayList<>();
        PolicyRule endpoints = new PolicyRule();
        endpoints.setApiGroups(List.of(""));
        endpoints.setResources(List.of("secrets"));
        endpoints.setVerbs(Arrays.asList("get", "list", "watch"));
        policyRuleList.add(endpoints);
        Role roleCreated = new RoleBuilder()
            .withNewMetadata().withName(roleName).withNamespace(OpenshiftClient.get().getNamespace()).endMetadata()
            .addAllToRules(policyRuleList)
            .build();
        final RbacAPIGroupDSL rbac = OpenshiftClient.get().rbac();
        rbac.roles().resource(roleCreated)
            .serverSideApply();

        List<Subject> subjects = new ArrayList<>();
        Subject subject = new Subject();
        subject.setKind("ServiceAccount");
        subject.setName(sa.getMetadata().getName());
        subject.setNamespace(OpenshiftClient.get().getNamespace());
        subjects.add(subject);
        RoleRef roleRef = new RoleRef();
        roleRef.setApiGroup("rbac.authorization.k8s.io");
        roleRef.setKind("Role");
        roleRef.setName(roleCreated.getMetadata().getName());
        RoleBinding roleBindingCreated = new RoleBindingBuilder()
            .withNewMetadata().withName(roleBingingName).withNamespace(OpenshiftClient.get().getNamespace()).endMetadata()
            .withRoleRef(roleRef)
            .addAllToSubjects(subjects)
            .build();
        rbac.roleBindings().resource(roleBindingCreated)
            .serverSideApply();
    }
}
