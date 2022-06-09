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
 * A single acceptor configuration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "amqpMinLargeMessageSize",
    "port",
    "verifyHost",
    "wantClientAuth",
    "expose",
    "enabledCipherSuites",
    "needClientAuth",
    "multicastPrefix",
    "name",
    "connectionsAllowed",
    "sslEnabled",
    "sniHost",
    "enabledProtocols",
    "protocols",
    "sslSecret",
    "sslProvider",
    "anycastPrefix"
})
@Generated("jsonschema2pojo")
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class Acceptor implements KubernetesResource {

    /**
     * The default value is 102400 (100KBytes). Setting it to -1 will disable large message support.
     */
    @JsonProperty("amqpMinLargeMessageSize")
    @JsonPropertyDescription("The default value is 102400 (100KBytes). Setting it to -1 will disable large message support.")
    private Integer amqpMinLargeMessageSize;
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
     * Whether or not to expose this acceptor
     */
    @JsonProperty("expose")
    @JsonPropertyDescription("Whether or not to expose this acceptor")
    private Boolean expose;
    /**
     * Comma separated list of cipher suites used for SSL communication.
     */
    @JsonProperty("enabledCipherSuites")
    @JsonPropertyDescription("Comma separated list of cipher suites used for SSL communication.")
    private String enabledCipherSuites;
    /**
     * Tells a client connecting to this acceptor that 2-way SSL is required. This property takes precedence over wantClientAuth.
     */
    @JsonProperty("needClientAuth")
    @JsonPropertyDescription("Tells a client connecting to this acceptor that 2-way SSL is required. This property takes precedence over "
        + "wantClientAuth.")
    private Boolean needClientAuth;
    /**
     * To indicate which kind of routing type to use.
     */
    @JsonProperty("multicastPrefix")
    @JsonPropertyDescription("To indicate which kind of routing type to use.")
    private String multicastPrefix;
    /**
     * The name of the acceptor
     */
    @JsonProperty("name")
    @JsonPropertyDescription("The name of the acceptor")
    private String name;
    /**
     * Limits the number of connections which the acceptor will allow. When this limit is reached a DEBUG level message is issued to the log, and
     * the connection is refused.
     */
    @JsonProperty("connectionsAllowed")
    @JsonPropertyDescription("Limits the number of connections which the acceptor will allow. When this limit is reached a DEBUG level message is "
        + "issued to the log, and the connection is refused.")
    private Integer connectionsAllowed;
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
     * The protocols to enable for this acceptor
     */
    @JsonProperty("protocols")
    @JsonPropertyDescription("The protocols to enable for this acceptor")
    private String protocols;
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
    /**
     * To indicate which kind of routing type to use.
     */
    @JsonProperty("anycastPrefix")
    @JsonPropertyDescription("To indicate which kind of routing type to use.")
    private String anycastPrefix;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * The default value is 102400 (100KBytes). Setting it to -1 will disable large message support.
     */
    @JsonProperty("amqpMinLargeMessageSize")
    public Integer getAmqpMinLargeMessageSize() {
        return amqpMinLargeMessageSize;
    }

    /**
     * The default value is 102400 (100KBytes). Setting it to -1 will disable large message support.
     */
    @JsonProperty("amqpMinLargeMessageSize")
    public void setAmqpMinLargeMessageSize(Integer amqpMinLargeMessageSize) {
        this.amqpMinLargeMessageSize = amqpMinLargeMessageSize;
    }

    public Acceptor withAmqpMinLargeMessageSize(Integer amqpMinLargeMessageSize) {
        this.amqpMinLargeMessageSize = amqpMinLargeMessageSize;
        return this;
    }

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

    public Acceptor withPort(Integer port) {
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

    public Acceptor withVerifyHost(Boolean verifyHost) {
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

    public Acceptor withWantClientAuth(Boolean wantClientAuth) {
        this.wantClientAuth = wantClientAuth;
        return this;
    }

    /**
     * Whether or not to expose this acceptor
     */
    @JsonProperty("expose")
    public Boolean getExpose() {
        return expose;
    }

    /**
     * Whether or not to expose this acceptor
     */
    @JsonProperty("expose")
    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

    public Acceptor withExpose(Boolean expose) {
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

    public Acceptor withEnabledCipherSuites(String enabledCipherSuites) {
        this.enabledCipherSuites = enabledCipherSuites;
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

    public Acceptor withNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
        return this;
    }

    /**
     * To indicate which kind of routing type to use.
     */
    @JsonProperty("multicastPrefix")
    public String getMulticastPrefix() {
        return multicastPrefix;
    }

    /**
     * To indicate which kind of routing type to use.
     */
    @JsonProperty("multicastPrefix")
    public void setMulticastPrefix(String multicastPrefix) {
        this.multicastPrefix = multicastPrefix;
    }

    public Acceptor withMulticastPrefix(String multicastPrefix) {
        this.multicastPrefix = multicastPrefix;
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

    public Acceptor withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Limits the number of connections which the acceptor will allow. When this limit is reached a DEBUG level message is issued to the log, and
     * the connection is refused.
     */
    @JsonProperty("connectionsAllowed")
    public Integer getConnectionsAllowed() {
        return connectionsAllowed;
    }

    /**
     * Limits the number of connections which the acceptor will allow. When this limit is reached a DEBUG level message is issued to the log, and
     * the connection is refused.
     */
    @JsonProperty("connectionsAllowed")
    public void setConnectionsAllowed(Integer connectionsAllowed) {
        this.connectionsAllowed = connectionsAllowed;
    }

    public Acceptor withConnectionsAllowed(Integer connectionsAllowed) {
        this.connectionsAllowed = connectionsAllowed;
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

    public Acceptor withSslEnabled(Boolean sslEnabled) {
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

    public Acceptor withSniHost(String sniHost) {
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

    public Acceptor withEnabledProtocols(String enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
        return this;
    }

    /**
     * The protocols to enable for this acceptor
     */
    @JsonProperty("protocols")
    public String getProtocols() {
        return protocols;
    }

    /**
     * The protocols to enable for this acceptor
     */
    @JsonProperty("protocols")
    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }

    public Acceptor withProtocols(String protocols) {
        this.protocols = protocols;
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

    public Acceptor withSslSecret(String sslSecret) {
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

    public Acceptor withSslProvider(String sslProvider) {
        this.sslProvider = sslProvider;
        return this;
    }

    /**
     * To indicate which kind of routing type to use.
     */
    @JsonProperty("anycastPrefix")
    public String getAnycastPrefix() {
        return anycastPrefix;
    }

    /**
     * To indicate which kind of routing type to use.
     */
    @JsonProperty("anycastPrefix")
    public void setAnycastPrefix(String anycastPrefix) {
        this.anycastPrefix = anycastPrefix;
    }

    public Acceptor withAnycastPrefix(String anycastPrefix) {
        this.anycastPrefix = anycastPrefix;
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

    public Acceptor withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Acceptor.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("amqpMinLargeMessageSize");
        sb.append('=');
        sb.append(((this.amqpMinLargeMessageSize == null) ? "<null>" : this.amqpMinLargeMessageSize));
        sb.append(',');
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
        sb.append("needClientAuth");
        sb.append('=');
        sb.append(((this.needClientAuth == null) ? "<null>" : this.needClientAuth));
        sb.append(',');
        sb.append("multicastPrefix");
        sb.append('=');
        sb.append(((this.multicastPrefix == null) ? "<null>" : this.multicastPrefix));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("connectionsAllowed");
        sb.append('=');
        sb.append(((this.connectionsAllowed == null) ? "<null>" : this.connectionsAllowed));
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
        sb.append("protocols");
        sb.append('=');
        sb.append(((this.protocols == null) ? "<null>" : this.protocols));
        sb.append(',');
        sb.append("sslSecret");
        sb.append('=');
        sb.append(((this.sslSecret == null) ? "<null>" : this.sslSecret));
        sb.append(',');
        sb.append("sslProvider");
        sb.append('=');
        sb.append(((this.sslProvider == null) ? "<null>" : this.sslProvider));
        sb.append(',');
        sb.append("anycastPrefix");
        sb.append('=');
        sb.append(((this.anycastPrefix == null) ? "<null>" : this.anycastPrefix));
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
        result = ((result * 31) + ((this.anycastPrefix == null) ? 0 : this.anycastPrefix.hashCode()));
        result = ((result * 31) + ((this.verifyHost == null) ? 0 : this.verifyHost.hashCode()));
        result = ((result * 31) + ((this.sslProvider == null) ? 0 : this.sslProvider.hashCode()));
        result = ((result * 31) + ((this.expose == null) ? 0 : this.expose.hashCode()));
        result = ((result * 31) + ((this.multicastPrefix == null) ? 0 : this.multicastPrefix.hashCode()));
        result = ((result * 31) + ((this.wantClientAuth == null) ? 0 : this.wantClientAuth.hashCode()));
        result = ((result * 31) + ((this.enabledCipherSuites == null) ? 0 : this.enabledCipherSuites.hashCode()));
        result = ((result * 31) + ((this.amqpMinLargeMessageSize == null) ? 0 : this.amqpMinLargeMessageSize.hashCode()));
        result = ((result * 31) + ((this.port == null) ? 0 : this.port.hashCode()));
        result = ((result * 31) + ((this.sslEnabled == null) ? 0 : this.sslEnabled.hashCode()));
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.sslSecret == null) ? 0 : this.sslSecret.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.protocols == null) ? 0 : this.protocols.hashCode()));
        result = ((result * 31) + ((this.sniHost == null) ? 0 : this.sniHost.hashCode()));
        result = ((result * 31) + ((this.connectionsAllowed == null) ? 0 : this.connectionsAllowed.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Acceptor) == false) {
            return false;
        }
        Acceptor rhs = ((Acceptor) other);
        return ((((((((((((((((
            (((this.needClientAuth == rhs.needClientAuth) || ((this.needClientAuth != null) && this.needClientAuth.equals(rhs.needClientAuth))) && (
                (this.enabledProtocols == rhs.enabledProtocols) || ((this.enabledProtocols != null) && this.enabledProtocols
                    .equals(rhs.enabledProtocols)))) && ((this.anycastPrefix == rhs.anycastPrefix) || ((this.anycastPrefix != null)
                && this.anycastPrefix.equals(rhs.anycastPrefix)))) && ((this.verifyHost == rhs.verifyHost) || ((this.verifyHost != null)
            && this.verifyHost.equals(rhs.verifyHost)))) && ((this.sslProvider == rhs.sslProvider) || ((this.sslProvider != null) && this.sslProvider
            .equals(rhs.sslProvider)))) && ((this.expose == rhs.expose) || ((this.expose != null) && this.expose.equals(rhs.expose)))) && (
            (this.multicastPrefix == rhs.multicastPrefix) || ((this.multicastPrefix != null) && this.multicastPrefix.equals(rhs.multicastPrefix))))
            && ((this.wantClientAuth == rhs.wantClientAuth) || ((this.wantClientAuth != null) && this.wantClientAuth.equals(rhs.wantClientAuth))))
            && ((this.enabledCipherSuites == rhs.enabledCipherSuites) || ((this.enabledCipherSuites != null) && this.enabledCipherSuites
            .equals(rhs.enabledCipherSuites)))) && ((this.amqpMinLargeMessageSize == rhs.amqpMinLargeMessageSize) || (
            (this.amqpMinLargeMessageSize != null) && this.amqpMinLargeMessageSize.equals(rhs.amqpMinLargeMessageSize)))) && ((this.port == rhs.port)
            || ((this.port != null) && this.port.equals(rhs.port)))) && ((this.sslEnabled == rhs.sslEnabled) || ((this.sslEnabled != null)
            && this.sslEnabled.equals(rhs.sslEnabled)))) && ((this.name == rhs.name) || ((this.name != null) && this.name.equals(rhs.name)))) && (
            (this.sslSecret == rhs.sslSecret) || ((this.sslSecret != null) && this.sslSecret.equals(rhs.sslSecret)))) && (
            (this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
                .equals(rhs.additionalProperties)))) && ((this.protocols == rhs.protocols) || ((this.protocols != null) && this.protocols
            .equals(rhs.protocols)))) && ((this.sniHost == rhs.sniHost) || ((this.sniHost != null) && this.sniHost.equals(rhs.sniHost)))) && (
            (this.connectionsAllowed == rhs.connectionsAllowed) || ((this.connectionsAllowed != null) && this.connectionsAllowed
                .equals(rhs.connectionsAllowed))));
    }
}
