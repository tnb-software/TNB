package software.tnb.common.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.utils.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.ConfigMap;

/**
 * Provides a file with the CA certificates of the cluster, for the tools that connect to the cluster outside of the {@link OpenshiftClient}
 * and do not trust the (self-signed) certificates of the cluster, for example the JKube plugin that runs in a separate JVM.
 * <p>
 * The certificate validation is never disabled, the tools are only given the CAs to trust.
 * It is enabled only explicitly, by one of the properties {@link OpenshiftConfiguration#OPENSHIFT_CLUSTER_CA_FILE} or
 * {@link OpenshiftConfiguration#OPENSHIFT_CLUSTER_CA_AUTOSET}.
 */
final class ClusterCa {
    static final String API_CA_CONFIGMAP = "kube-root-ca.crt";
    static final String API_CA_KEY = "ca.crt";
    static final String INGRESS_CA_NAMESPACE = "openshift-config-managed";
    static final String INGRESS_CA_CONFIGMAP = "default-ingress-cert";
    static final String INGRESS_CA_KEY = "ca-bundle.crt";

    private static final Logger LOG = LoggerFactory.getLogger(ClusterCa.class);
    private static final Pattern CERTIFICATE = Pattern.compile("-----BEGIN CERTIFICATE-----[^-]+-----END CERTIFICATE-----");

    private static Path created;

    private ClusterCa() {
    }

    /**
     * Gets the file with the CA certificates of the cluster, based on the configuration.
     * <p>
     * The cluster is read and the file is written only once per JVM, all the callers reuse the same file.
     *
     * @param targetFile where to write the certificates read from the cluster
     * @return the file to use, empty if not configured
     */
    static synchronized Optional<Path> file(Path targetFile) {
        final Path configuredFile = OpenshiftConfiguration.openshiftClusterCaFile();
        if (configuredFile != null) {
            if (!Files.isRegularFile(configuredFile)) {
                throw new IllegalStateException(String.format("File %s set in %s does not exist", configuredFile,
                    OpenshiftConfiguration.OPENSHIFT_CLUSTER_CA_FILE));
            }
            LOG.debug("Using the file {} set in {}, the cluster is not read", configuredFile, OpenshiftConfiguration.OPENSHIFT_CLUSTER_CA_FILE);
            return Optional.of(configuredFile);
        }

        if (!OpenshiftConfiguration.openshiftClusterCaAutoset()) {
            LOG.debug("{} is not enabled, no cluster CA file", OpenshiftConfiguration.OPENSHIFT_CLUSTER_CA_AUTOSET);
            return Optional.empty();
        }

        if (created != null && Files.isRegularFile(created)) {
            LOG.debug("Reusing the cluster CA file {}", created);
            return Optional.of(created);
        }

        final List<String> certificates = uniqueCertificates(readFromCluster());
        if (certificates.isEmpty()) {
            LOG.warn("No CA certificate found in the cluster, no cluster CA file");
            return Optional.empty();
        }
        created = write(targetFile, certificates);
        return Optional.of(created);
    }

    /**
     * Splits the PEM sources to single certificates and removes the duplicates, the same CA can be present in multiple config maps.
     */
    private static List<String> uniqueCertificates(List<String> sources) {
        final Set<String> seen = new HashSet<>();
        final List<String> unique = new ArrayList<>();
        sources.forEach(source -> {
            final Matcher matcher = CERTIFICATE.matcher(source);
            while (matcher.find()) {
                // compare without the whitespaces, but keep the original text with the line breaks
                if (seen.add(matcher.group().replaceAll("\\s+", ""))) {
                    unique.add(matcher.group());
                }
            }
        });
        return unique;
    }

    private static List<String> readFromCluster() {
        final List<String> certificates = new ArrayList<>();
        readConfigMapKey(OpenshiftClient.get().getNamespace(), API_CA_CONFIGMAP, API_CA_KEY).ifPresent(certificates::add);
        readConfigMapKey(INGRESS_CA_NAMESPACE, INGRESS_CA_CONFIGMAP, INGRESS_CA_KEY).ifPresent(certificates::add);
        return certificates;
    }

    private static Optional<String> readConfigMapKey(String namespace, String name, String key) {
        try {
            LOG.debug("Reading key {} of the config map {} in the namespace {}", key, name, namespace);
            final ConfigMap configMap = OpenshiftClient.get().configMaps().inNamespace(namespace).withName(name).get();
            if (configMap == null || configMap.getData() == null || configMap.getData().get(key) == null) {
                LOG.warn("Key {} of the config map {} not found in the cluster", key, name);
                return Optional.empty();
            }
            final String value = configMap.getData().get(key);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found key {} of the config map {}: {}", key, name, describe(value));
            }
            return Optional.of(value);
        } catch (RuntimeException e) {
            LOG.warn("Unable to read the config map {} from the cluster: {}", name, e.getMessage());
            return Optional.empty();
        }
    }

    private static Path write(Path file, List<String> certificates) {
        final StringBuilder content = new StringBuilder();
        certificates.forEach(certificate -> content.append(certificate.trim()).append(System.lineSeparator()));
        IOUtils.writeFile(file, content.toString());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Written {}: {}", file.toAbsolutePath(), describe(content.toString()));
        }
        return file;
    }

    /**
     * Describes the PEM content for the debug log: the subjects and the expiration of the certificates, not the certificates themselves.
     */
    private static String describe(String pem) {
        try {
            final Collection<? extends Certificate> certs = CertificateFactory.getInstance("X.509")
                .generateCertificates(new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8)));
            return certs.size() + " certificate(s) " + certs.stream()
                .map(c -> c instanceof X509Certificate x509
                    ? "[" + x509.getSubjectX500Principal().getName() + ", valid until " + x509.getNotAfter().toInstant() + "]"
                    : c.getType())
                .collect(Collectors.joining(", "));
        } catch (CertificateException e) {
            return "unparsable content (" + e.getMessage() + ")";
        }
    }
}
