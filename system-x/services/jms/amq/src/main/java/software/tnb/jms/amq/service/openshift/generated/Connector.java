package software.tnb.jms.amq.service.openshift.generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.processing.Generated;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

/**
 * A single connector configuration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "port",
    "verifyHost",
    "wantClientAuth",
    "expose",
    "enabledCipherSuites",
    "host",
    "needClientAuth",
    "name",
    "sslEnabled",
    "sniHost",
    "enabledProtocols",
    "type",
    "sslSecret",
    "sslProvider"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Connector implements KubernetesResource {

    /**
     * Port number
     */
    @JsonProperty("port")
    @JsonPropertyDescription("Port number")
    private Integer port;
    /**
     * The CN of the connecting client's SSL certificate will be compared to its hostname to verify they match. This is useful only for 2-way SSL.
     */
    @JsonProperty("verifyHost")
    @JsonPropertyDescription("The CN of the connecting client's SSL certificate will be compared to its hostname to verify they match. This is "
        + "useful only for 2-way SSL.")
    private Boolean verifyHost;
    /**
     * Tells a client connecting to this acceptor that 2-way SSL is requested but not required. Overridden by needClientAuth.
     */
    @JsonProperty("wantClientAuth")
    @JsonPropertyDescription("Tells a client connecting to this acceptor that 2-way SSL is requested but not required. Overridden by needClientAuth.")
    private Boolean wantClientAuth;
    /**
     * Whether or not to expose this connector
     */
    @JsonProperty("expose")
    @JsonPropertyDescription("Whether or not to expose this connector")
    private Boolean expose;
    /**
     * Comma separated list of cipher suites used for SSL communication.
     */
    @JsonProperty("enabledCipherSuites")
    @JsonPropertyDescription("Comma separated list of cipher suites used for SSL communication.")
    private String enabledCipherSuites;
    /**
     * Hostname or IP to connect to
     */
    @JsonProperty("host")
    @JsonPropertyDescription("Hostname or IP to connect to")
    private String host;
    /**
     * Tells a client connecting to this acceptor that 2-way SSL is required. This property takes precedence over wantClientAuth.
     */
    @JsonProperty("needClientAuth")
    @JsonPropertyDescription("Tells a client connecting to this acceptor that 2-way SSL is required. This property takes precedence over "
        + "wantClientAuth.")
    private Boolean needClientAuth;
    /**
     * The name of the acceptor
     */
    @JsonProperty("name")
    @JsonPropertyDescription("The name of the acceptor")
    private String name;
    /**
     * Whether or not to enable SSL on this port
     */
    @JsonProperty("sslEnabled")
    @JsonPropertyDescription("Whether or not to enable SSL on this port")
    private Boolean sslEnabled;
    /**
     * A regular expression used to match the server_name extension on incoming SSL connections. If the name doesn't match then the connection to
     * the acceptor will be rejected.
     */
    @JsonProperty("sniHost")
    @JsonPropertyDescription("A regular expression used to match the server_name extension on incoming SSL connections. If the name doesn't match "
        + "then the connection to the acceptor will be rejected.")
    private String sniHost;
    /**
     * Comma separated list of protocols used for SSL communication.
     */
    @JsonProperty("enabledProtocols")
    @JsonPropertyDescription("Comma separated list of protocols used for SSL communication.")
    private String enabledProtocols;
    /**
     * The type either tcp or vm
     */
    @JsonProperty("type")
    @JsonPropertyDescription("The type either tcp or vm")
    private String type;
    /**
     * Name of the secret to use for ssl information
     */
    @JsonProperty("sslSecret")
    @JsonPropertyDescription("Name of the secret to use for ssl information")
    private String sslSecret;
    /**
     * Used to change the SSL Provider between JDK and OPENSSL. The default is JDK.
     */
    @JsonProperty("sslProvider")
    @JsonPropertyDescription("Used to change the SSL Provider between JDK and OPENSSL. The default is JDK.")
    private String sslProvider;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Port number
     */
    @JsonProperty("port")
    public Integer getPort() {
        return port;
    }

    /**
     * Port number
     */
    @JsonProperty("port")
    public void setPort(Integer port) {
        this.port = port;
    }

    public Connector withPort(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * The CN of the connecting client's SSL certificate will be compared to its hostname to verify they match. This is useful only for 2-way SSL.
     */
    @JsonProperty("verifyHost")
    public Boolean getVerifyHost() {
        return verifyHost;
    }

    /**
     * The CN of the connecting client's SSL certificate will be compared to its hostname to verify they match. This is useful only for 2-way SSL.
     */
    @JsonProperty("verifyHost")
    public void setVerifyHost(Boolean verifyHost) {
        this.verifyHost = verifyHost;
    }

    public Connector withVerifyHost(Boolean verifyHost) {
        this.verifyHost = verifyHost;
        return this;
    }

    /**
     * Tells a client connecting to this acceptor that 2-way SSL is requested but not required. Overridden by needClientAuth.
     */
    @JsonProperty("wantClientAuth")
    public Boolean getWantClientAuth() {
        return wantClientAuth;
    }

    /**
     * Tells a client connecting to this acceptor that 2-way SSL is requested but not required. Overridden by needClientAuth.
     */
    @JsonProperty("wantClientAuth")
    public void setWantClientAuth(Boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
    }

    public Connector withWantClientAuth(Boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
        return this;
    }

    /**
     * Whether or not to expose this connector
     */
    @JsonProperty("expose")
    public Boolean getExpose() {
        return expose;
    }

    /**
     * Whether or not to expose this connector
     */
    @JsonProperty("expose")
    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

    public Connector withExpose(Boolean expose) {
        this.expose = expose;
        return this;
    }

    /**
     * Comma separated list of cipher suites used for SSL communication.
     */
    @JsonProperty("enabledCipherSuites")
    public String getEnabledCipherSuites() {
        return enabledCipherSuites;
    }

    /**
     * Comma separated list of cipher suites used for SSL communication.
     */
    @JsonProperty("enabledCipherSuites")
    public void setEnabledCipherSuites(String enabledCipherSuites) {
        this.enabledCipherSuites = enabledCipherSuites;
    }

    public Connector withEnabledCipherSuites(String enabledCipherSuites) {
        this.enabledCipherSuites = enabledCipherSuites;
        return this;
    }

    /**
     * Hostname or IP to connect to
     */
    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    /**
     * Hostname or IP to connect to
     */
    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    public Connector withHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Tells a client connecting to this acceptor that 2-way SSL is required. This property takes precedence over wantClientAuth.
     */
    @JsonProperty("needClientAuth")
    public Boolean getNeedClientAuth() {
        return needClientAuth;
    }

    /**
     * Tells a client connecting to this acceptor that 2-way SSL is required. This property takes precedence over wantClientAuth.
     */
    @JsonProperty("needClientAuth")
    public void setNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    public Connector withNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
        return this;
    }

    /**
     * The name of the acceptor
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * The name of the acceptor
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Connector withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Whether or not to enable SSL on this port
     */
    @JsonProperty("sslEnabled")
    public Boolean getSslEnabled() {
        return sslEnabled;
    }

    /**
     * Whether or not to enable SSL on this port
     */
    @JsonProperty("sslEnabled")
    public void setSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public Connector withSslEnabled(Boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
        return this;
    }

    /**
     * A regular expression used to match the server_name extension on incoming SSL connections. If the name doesn't match then the connection to
     * the acceptor will be rejected.
     */
    @JsonProperty("sniHost")
    public String getSniHost() {
        return sniHost;
    }

    /**
     * A regular expression used to match the server_name extension on incoming SSL connections. If the name doesn't match then the connection to
     * the acceptor will be rejected.
     */
    @JsonProperty("sniHost")
    public void setSniHost(String sniHost) {
        this.sniHost = sniHost;
    }

    public Connector withSniHost(String sniHost) {
        this.sniHost = sniHost;
        return this;
    }

    /**
     * Comma separated list of protocols used for SSL communication.
     */
    @JsonProperty("enabledProtocols")
    public String getEnabledProtocols() {
        return enabledProtocols;
    }

    /**
     * Comma separated list of protocols used for SSL communication.
     */
    @JsonProperty("enabledProtocols")
    public void setEnabledProtocols(String enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    public Connector withEnabledProtocols(String enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
        return this;
    }

    /**
     * The type either tcp or vm
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * The type either tcp or vm
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public Connector withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Name of the secret to use for ssl information
     */
    @JsonProperty("sslSecret")
    public String getSslSecret() {
        return sslSecret;
    }

    /**
     * Name of the secret to use for ssl information
     */
    @JsonProperty("sslSecret")
    public void setSslSecret(String sslSecret) {
        this.sslSecret = sslSecret;
    }

    public Connector withSslSecret(String sslSecret) {
        this.sslSecret = sslSecret;
        return this;
    }

    /**
     * Used to change the SSL Provider between JDK and OPENSSL. The default is JDK.
     */
    @JsonProperty("sslProvider")
    public String getSslProvider() {
        return sslProvider;
    }

    /**
     * Used to change the SSL Provider between JDK and OPENSSL. The default is JDK.
     */
    @JsonProperty("sslProvider")
    public void setSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
    }

    public Connector withSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Connector withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Connector.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("port");
        sb.append('=');
        sb.append(((this.port == null) ? "<null>" : this.port));
        sb.append(',');
        sb.append("verifyHost");
        sb.append('=');
        sb.append(((this.verifyHost == null) ? "<null>" : this.verifyHost));
        sb.append(',');
        sb.append("wantClientAuth");
        sb.append('=');
        sb.append(((this.wantClientAuth == null) ? "<null>" : this.wantClientAuth));
        sb.append(',');
        sb.append("expose");
        sb.append('=');
        sb.append(((this.expose == null) ? "<null>" : this.expose));
        sb.append(',');
        sb.append("enabledCipherSuites");
        sb.append('=');
        sb.append(((this.enabledCipherSuites == null) ? "<null>" : this.enabledCipherSuites));
        sb.append(',');
        sb.append("host");
        sb.append('=');
        sb.append(((this.host == null) ? "<null>" : this.host));
        sb.append(',');
        sb.append("needClientAuth");
        sb.append('=');
        sb.append(((this.needClientAuth == null) ? "<null>" : this.needClientAuth));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("sslEnabled");
        sb.append('=');
        sb.append(((this.sslEnabled == null) ? "<null>" : this.sslEnabled));
        sb.append(',');
        sb.append("sniHost");
        sb.append('=');
        sb.append(((this.sniHost == null) ? "<null>" : this.sniHost));
        sb.append(',');
        sb.append("enabledProtocols");
        sb.append('=');
        sb.append(((this.enabledProtocols == null) ? "<null>" : this.enabledProtocols));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        sb.append("sslSecret");
        sb.append('=');
        sb.append(((this.sslSecret == null) ? "<null>" : this.sslSecret));
        sb.append(',');
        sb.append("sslProvider");
        sb.append('=');
        sb.append(((this.sslProvider == null) ? "<null>" : this.sslProvider));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.needClientAuth == null) ? 0 : this.needClientAuth.hashCode()));
        result = ((result * 31) + ((this.enabledProtocols == null) ? 0 : this.enabledProtocols.hashCode()));
        result = ((result * 31) + ((this.verifyHost == null) ? 0 : this.verifyHost.hashCode()));
        result = ((result * 31) + ((this.sslProvider == null) ? 0 : this.sslProvider.hashCode()));
        result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
        result = ((result * 31) + ((this.expose == null) ? 0 : this.expose.hashCode()));
        result = ((result * 31) + ((this.wantClientAuth == null) ? 0 : this.wantClientAuth.hashCode()));
        result = ((result * 31) + ((this.enabledCipherSuites == null) ? 0 : this.enabledCipherSuites.hashCode()));
        result = ((result * 31) + ((this.port == null) ? 0 : this.port.hashCode()));
        result = ((result * 31) + ((this.sslEnabled == null) ? 0 : this.sslEnabled.hashCode()));
        result = ((result * 31) + ((this.host == null) ? 0 : this.host.hashCode()));
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.sslSecret == null) ? 0 : this.sslSecret.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.sniHost == null) ? 0 : this.sniHost.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Connector) == false) {
            return false;
        }
        Connector rhs = ((Connector) other);
        return (((((((((((
            (((((this.needClientAuth == rhs.needClientAuth) || ((this.needClientAuth != null) && this.needClientAuth.equals(rhs.needClientAuth))) && (
                (this.enabledProtocols == rhs.enabledProtocols) || ((this.enabledProtocols != null) && this.enabledProtocols
                    .equals(rhs.enabledProtocols)))) && ((this.verifyHost == rhs.verifyHost) || ((this.verifyHost != null) && this.verifyHost
                .equals(rhs.verifyHost)))) && ((this.sslProvider == rhs.sslProvider) || ((this.sslProvider != null) && this.sslProvider
                .equals(rhs.sslProvider)))) && ((this.type == rhs.type) || ((this.type != null) && this.type.equals(rhs.type)))) && (
            (this.expose == rhs.expose) || ((this.expose != null) && this.expose.equals(rhs.expose)))) && ((this.wantClientAuth == rhs.wantClientAuth)
            || ((this.wantClientAuth != null) && this.wantClientAuth.equals(rhs.wantClientAuth)))) && (
            (this.enabledCipherSuites == rhs.enabledCipherSuites) || ((this.enabledCipherSuites != null) && this.enabledCipherSuites
                .equals(rhs.enabledCipherSuites)))) && ((this.port == rhs.port) || ((this.port != null) && this.port.equals(rhs.port)))) && (
            (this.sslEnabled == rhs.sslEnabled) || ((this.sslEnabled != null) && this.sslEnabled.equals(rhs.sslEnabled)))) && ((this.host == rhs.host)
            || ((this.host != null) && this.host.equals(rhs.host)))) && ((this.name == rhs.name) || ((this.name != null) && this.name
            .equals(rhs.name)))) && ((this.sslSecret == rhs.sslSecret) || ((this.sslSecret != null) && this.sslSecret.equals(rhs.sslSecret)))) && (
            (this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
                .equals(rhs.additionalProperties)))) && ((this.sniHost == rhs.sniHost) || ((this.sniHost != null) && this.sniHost
            .equals(rhs.sniHost))));
    }
}
