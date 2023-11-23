package software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.addresssettings;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"addressFullPolicy","autoCreateAddresses","autoCreateDeadLetterResources","autoCreateExpiryResources","autoCreateJmsQueues","autoCreateJmsTopics","autoCreateQueues","autoDeleteAddresses","autoDeleteAddressesDelay","autoDeleteCreatedQueues","autoDeleteJmsQueues","autoDeleteJmsTopics","autoDeleteQueues","autoDeleteQueuesDelay","autoDeleteQueuesMessageCount","configDeleteAddresses","configDeleteDiverts","configDeleteQueues","deadLetterAddress","deadLetterQueuePrefix","deadLetterQueueSuffix","defaultAddressRoutingType","defaultConsumerWindowSize","defaultConsumersBeforeDispatch","defaultDelayBeforeDispatch","defaultExclusiveQueue","defaultGroupBuckets","defaultGroupFirstKey","defaultGroupRebalance","defaultGroupRebalancePauseDispatch","defaultLastValueKey","defaultLastValueQueue","defaultMaxConsumers","defaultNonDestructive","defaultPurgeOnNoConsumers","defaultQueueRoutingType","defaultRingSize","enableIngressTimestamp","enableMetrics","expiryAddress","expiryDelay","expiryQueuePrefix","expiryQueueSuffix","lastValueQueue","managementBrowsePageSize","managementMessageAttributeSizeLimit","match","maxDeliveryAttempts","maxExpiryDelay","maxRedeliveryDelay","maxSizeBytes","maxSizeBytesRejectThreshold","maxSizeMessages","messageCounterHistoryDayLimit","minExpiryDelay","pageMaxCacheSize","pageSizeBytes","redeliveryDelay","redistributionDelay","retroactiveMessageCount","sendToDlaOnNoRoute","slowConsumerCheckPeriod","slowConsumerPolicy","slowConsumerThreshold","slowConsumerThresholdMeasurementUnit"})
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.fasterxml.jackson.databind.JsonDeserializer.None.class)
@javax.annotation.processing.Generated("io.fabric8.java.generator.CRGeneratorRunner")
public class AddressSetting implements io.fabric8.kubernetes.api.model.KubernetesResource {

    /**
     * what happens when an address where maxSizeBytes is specified becomes full
     */
    @com.fasterxml.jackson.annotation.JsonProperty("addressFullPolicy")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("what happens when an address where maxSizeBytes is specified becomes full")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String addressFullPolicy;

    public String getAddressFullPolicy() {
        return addressFullPolicy;
    }

    public void setAddressFullPolicy(String addressFullPolicy) {
        this.addressFullPolicy = addressFullPolicy;
    }

    /**
     * whether or not to automatically create addresses when a client sends a message to or attempts to consume a message from a queue mapped to an address that doesnt exist
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoCreateAddresses")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to automatically create addresses when a client sends a message to or attempts to consume a message from a queue mapped to an address that doesnt exist")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoCreateAddresses;

    public Boolean getAutoCreateAddresses() {
        return autoCreateAddresses;
    }

    public void setAutoCreateAddresses(Boolean autoCreateAddresses) {
        this.autoCreateAddresses = autoCreateAddresses;
    }

    /**
     * whether or not to automatically create the dead-letter-address and/or a corresponding queue on that address when a message found to be undeliverable
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoCreateDeadLetterResources")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to automatically create the dead-letter-address and/or a corresponding queue on that address when a message found to be undeliverable")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoCreateDeadLetterResources;

    public Boolean getAutoCreateDeadLetterResources() {
        return autoCreateDeadLetterResources;
    }

    public void setAutoCreateDeadLetterResources(Boolean autoCreateDeadLetterResources) {
        this.autoCreateDeadLetterResources = autoCreateDeadLetterResources;
    }

    /**
     * whether or not to automatically create the expiry-address and/or a corresponding queue on that address when a message is sent to a matching queue
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoCreateExpiryResources")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to automatically create the expiry-address and/or a corresponding queue on that address when a message is sent to a matching queue")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoCreateExpiryResources;

    public Boolean getAutoCreateExpiryResources() {
        return autoCreateExpiryResources;
    }

    public void setAutoCreateExpiryResources(Boolean autoCreateExpiryResources) {
        this.autoCreateExpiryResources = autoCreateExpiryResources;
    }

    /**
     * DEPRECATED. whether or not to automatically create JMS queues when a producer sends or a consumer connects to a queue
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoCreateJmsQueues")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("DEPRECATED. whether or not to automatically create JMS queues when a producer sends or a consumer connects to a queue")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoCreateJmsQueues;

    public Boolean getAutoCreateJmsQueues() {
        return autoCreateJmsQueues;
    }

    public void setAutoCreateJmsQueues(Boolean autoCreateJmsQueues) {
        this.autoCreateJmsQueues = autoCreateJmsQueues;
    }

    /**
     * DEPRECATED. whether or not to automatically create JMS topics when a producer sends or a consumer subscribes to a topic
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoCreateJmsTopics")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("DEPRECATED. whether or not to automatically create JMS topics when a producer sends or a consumer subscribes to a topic")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoCreateJmsTopics;

    public Boolean getAutoCreateJmsTopics() {
        return autoCreateJmsTopics;
    }

    public void setAutoCreateJmsTopics(Boolean autoCreateJmsTopics) {
        this.autoCreateJmsTopics = autoCreateJmsTopics;
    }

    /**
     * whether or not to automatically create a queue when a client sends a message to or attempts to consume a message from a queue
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoCreateQueues")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to automatically create a queue when a client sends a message to or attempts to consume a message from a queue")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoCreateQueues;

    public Boolean getAutoCreateQueues() {
        return autoCreateQueues;
    }

    public void setAutoCreateQueues(Boolean autoCreateQueues) {
        this.autoCreateQueues = autoCreateQueues;
    }

    /**
     * whether or not to delete auto-created addresses when it no longer has any queues
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteAddresses")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to delete auto-created addresses when it no longer has any queues")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoDeleteAddresses;

    public Boolean getAutoDeleteAddresses() {
        return autoDeleteAddresses;
    }

    public void setAutoDeleteAddresses(Boolean autoDeleteAddresses) {
        this.autoDeleteAddresses = autoDeleteAddresses;
    }

    /**
     * how long to wait (in milliseconds) before deleting auto-created addresses after they no longer have any queues
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteAddressesDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("how long to wait (in milliseconds) before deleting auto-created addresses after they no longer have any queues")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer autoDeleteAddressesDelay;

    public Integer getAutoDeleteAddressesDelay() {
        return autoDeleteAddressesDelay;
    }

    public void setAutoDeleteAddressesDelay(Integer autoDeleteAddressesDelay) {
        this.autoDeleteAddressesDelay = autoDeleteAddressesDelay;
    }

    /**
     * whether or not to delete created queues when the queue has 0 consumers and 0 messages
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteCreatedQueues")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to delete created queues when the queue has 0 consumers and 0 messages")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoDeleteCreatedQueues;

    public Boolean getAutoDeleteCreatedQueues() {
        return autoDeleteCreatedQueues;
    }

    public void setAutoDeleteCreatedQueues(Boolean autoDeleteCreatedQueues) {
        this.autoDeleteCreatedQueues = autoDeleteCreatedQueues;
    }

    /**
     * DEPRECATED. whether or not to delete auto-created JMS queues when the queue has 0 consumers and 0 messages
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteJmsQueues")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("DEPRECATED. whether or not to delete auto-created JMS queues when the queue has 0 consumers and 0 messages")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoDeleteJmsQueues;

    public Boolean getAutoDeleteJmsQueues() {
        return autoDeleteJmsQueues;
    }

    public void setAutoDeleteJmsQueues(Boolean autoDeleteJmsQueues) {
        this.autoDeleteJmsQueues = autoDeleteJmsQueues;
    }

    /**
     * DEPRECATED. whether or not to delete auto-created JMS topics when the last subscription is closed
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteJmsTopics")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("DEPRECATED. whether or not to delete auto-created JMS topics when the last subscription is closed")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoDeleteJmsTopics;

    public Boolean getAutoDeleteJmsTopics() {
        return autoDeleteJmsTopics;
    }

    public void setAutoDeleteJmsTopics(Boolean autoDeleteJmsTopics) {
        this.autoDeleteJmsTopics = autoDeleteJmsTopics;
    }

    /**
     * whether or not to delete auto-created queues when the queue has 0 consumers and 0 messages
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteQueues")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to delete auto-created queues when the queue has 0 consumers and 0 messages")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean autoDeleteQueues;

    public Boolean getAutoDeleteQueues() {
        return autoDeleteQueues;
    }

    public void setAutoDeleteQueues(Boolean autoDeleteQueues) {
        this.autoDeleteQueues = autoDeleteQueues;
    }

    /**
     * how long to wait (in milliseconds) before deleting auto-created queues after the queue has 0 consumers.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteQueuesDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("how long to wait (in milliseconds) before deleting auto-created queues after the queue has 0 consumers.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer autoDeleteQueuesDelay;

    public Integer getAutoDeleteQueuesDelay() {
        return autoDeleteQueuesDelay;
    }

    public void setAutoDeleteQueuesDelay(Integer autoDeleteQueuesDelay) {
        this.autoDeleteQueuesDelay = autoDeleteQueuesDelay;
    }

    /**
     * the message count the queue must be at or below before it can be evaluated to be auto deleted, 0 waits until empty queue (default) and -1 disables this check.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("autoDeleteQueuesMessageCount")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the message count the queue must be at or below before it can be evaluated to be auto deleted, 0 waits until empty queue (default) and -1 disables this check.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer autoDeleteQueuesMessageCount;

    public Integer getAutoDeleteQueuesMessageCount() {
        return autoDeleteQueuesMessageCount;
    }

    public void setAutoDeleteQueuesMessageCount(Integer autoDeleteQueuesMessageCount) {
        this.autoDeleteQueuesMessageCount = autoDeleteQueuesMessageCount;
    }

    /**
     * What to do when an address is no longer in broker.xml.  OFF = will do nothing addresses will remain, FORCE = delete address and its queues even if messages remaining.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("configDeleteAddresses")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("What to do when an address is no longer in broker.xml.  OFF = will do nothing addresses will remain, FORCE = delete address and its queues even if messages remaining.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String configDeleteAddresses;

    public String getConfigDeleteAddresses() {
        return configDeleteAddresses;
    }

    public void setConfigDeleteAddresses(String configDeleteAddresses) {
        this.configDeleteAddresses = configDeleteAddresses;
    }

    /**
     * What to do when a divert is no longer in broker.xml.  OFF = will do nothing and divert will remain(default), FORCE = delete divert
     */
    @com.fasterxml.jackson.annotation.JsonProperty("configDeleteDiverts")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("What to do when a divert is no longer in broker.xml.  OFF = will do nothing and divert will remain(default), FORCE = delete divert")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String configDeleteDiverts;

    public String getConfigDeleteDiverts() {
        return configDeleteDiverts;
    }

    public void setConfigDeleteDiverts(String configDeleteDiverts) {
        this.configDeleteDiverts = configDeleteDiverts;
    }

    /**
     * What to do when a queue is no longer in broker.xml.  OFF = will do nothing queues will remain, FORCE = delete queues even if messages remaining.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("configDeleteQueues")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("What to do when a queue is no longer in broker.xml.  OFF = will do nothing queues will remain, FORCE = delete queues even if messages remaining.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String configDeleteQueues;

    public String getConfigDeleteQueues() {
        return configDeleteQueues;
    }

    public void setConfigDeleteQueues(String configDeleteQueues) {
        this.configDeleteQueues = configDeleteQueues;
    }

    /**
     * the address to send dead messages to
     */
    @com.fasterxml.jackson.annotation.JsonProperty("deadLetterAddress")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the address to send dead messages to")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String deadLetterAddress;

    public String getDeadLetterAddress() {
        return deadLetterAddress;
    }

    public void setDeadLetterAddress(String deadLetterAddress) {
        this.deadLetterAddress = deadLetterAddress;
    }

    /**
     * the prefix to use for auto-created dead letter queues
     */
    @com.fasterxml.jackson.annotation.JsonProperty("deadLetterQueuePrefix")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the prefix to use for auto-created dead letter queues")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String deadLetterQueuePrefix;

    public String getDeadLetterQueuePrefix() {
        return deadLetterQueuePrefix;
    }

    public void setDeadLetterQueuePrefix(String deadLetterQueuePrefix) {
        this.deadLetterQueuePrefix = deadLetterQueuePrefix;
    }

    /**
     * the suffix to use for auto-created dead letter queues
     */
    @com.fasterxml.jackson.annotation.JsonProperty("deadLetterQueueSuffix")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the suffix to use for auto-created dead letter queues")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String deadLetterQueueSuffix;

    public String getDeadLetterQueueSuffix() {
        return deadLetterQueueSuffix;
    }

    public void setDeadLetterQueueSuffix(String deadLetterQueueSuffix) {
        this.deadLetterQueueSuffix = deadLetterQueueSuffix;
    }

    /**
     * the routing-type used on auto-created addresses
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultAddressRoutingType")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the routing-type used on auto-created addresses")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String defaultAddressRoutingType;

    public String getDefaultAddressRoutingType() {
        return defaultAddressRoutingType;
    }

    public void setDefaultAddressRoutingType(String defaultAddressRoutingType) {
        this.defaultAddressRoutingType = defaultAddressRoutingType;
    }

    /**
     * the default window size for a consumer
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultConsumerWindowSize")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the default window size for a consumer")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer defaultConsumerWindowSize;

    public Integer getDefaultConsumerWindowSize() {
        return defaultConsumerWindowSize;
    }

    public void setDefaultConsumerWindowSize(Integer defaultConsumerWindowSize) {
        this.defaultConsumerWindowSize = defaultConsumerWindowSize;
    }

    /**
     * the default number of consumers needed before dispatch can start for queues under the address.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultConsumersBeforeDispatch")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the default number of consumers needed before dispatch can start for queues under the address.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer defaultConsumersBeforeDispatch;

    public Integer getDefaultConsumersBeforeDispatch() {
        return defaultConsumersBeforeDispatch;
    }

    public void setDefaultConsumersBeforeDispatch(Integer defaultConsumersBeforeDispatch) {
        this.defaultConsumersBeforeDispatch = defaultConsumersBeforeDispatch;
    }

    /**
     * the default delay (in milliseconds) to wait before dispatching if number of consumers before dispatch is not met for queues under the address.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultDelayBeforeDispatch")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the default delay (in milliseconds) to wait before dispatching if number of consumers before dispatch is not met for queues under the address.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer defaultDelayBeforeDispatch;

    public Integer getDefaultDelayBeforeDispatch() {
        return defaultDelayBeforeDispatch;
    }

    public void setDefaultDelayBeforeDispatch(Integer defaultDelayBeforeDispatch) {
        this.defaultDelayBeforeDispatch = defaultDelayBeforeDispatch;
    }

    /**
     * whether to treat the queues under the address as exclusive queues by default
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultExclusiveQueue")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether to treat the queues under the address as exclusive queues by default")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean defaultExclusiveQueue;

    public Boolean getDefaultExclusiveQueue() {
        return defaultExclusiveQueue;
    }

    public void setDefaultExclusiveQueue(Boolean defaultExclusiveQueue) {
        this.defaultExclusiveQueue = defaultExclusiveQueue;
    }

    /**
     * number of buckets to use for grouping, -1 (default) is unlimited and uses the raw group, 0 disables message groups.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultGroupBuckets")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("number of buckets to use for grouping, -1 (default) is unlimited and uses the raw group, 0 disables message groups.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer defaultGroupBuckets;

    public Integer getDefaultGroupBuckets() {
        return defaultGroupBuckets;
    }

    public void setDefaultGroupBuckets(Integer defaultGroupBuckets) {
        this.defaultGroupBuckets = defaultGroupBuckets;
    }

    /**
     * key used to mark a message is first in a group for a consumer
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultGroupFirstKey")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("key used to mark a message is first in a group for a consumer")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String defaultGroupFirstKey;

    public String getDefaultGroupFirstKey() {
        return defaultGroupFirstKey;
    }

    public void setDefaultGroupFirstKey(String defaultGroupFirstKey) {
        this.defaultGroupFirstKey = defaultGroupFirstKey;
    }

    /**
     * whether to rebalance groups when a consumer is added
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultGroupRebalance")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether to rebalance groups when a consumer is added")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean defaultGroupRebalance;

    public Boolean getDefaultGroupRebalance() {
        return defaultGroupRebalance;
    }

    public void setDefaultGroupRebalance(Boolean defaultGroupRebalance) {
        this.defaultGroupRebalance = defaultGroupRebalance;
    }

    /**
     * whether to pause dispatch when rebalancing groups
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultGroupRebalancePauseDispatch")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether to pause dispatch when rebalancing groups")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean defaultGroupRebalancePauseDispatch;

    public Boolean getDefaultGroupRebalancePauseDispatch() {
        return defaultGroupRebalancePauseDispatch;
    }

    public void setDefaultGroupRebalancePauseDispatch(Boolean defaultGroupRebalancePauseDispatch) {
        this.defaultGroupRebalancePauseDispatch = defaultGroupRebalancePauseDispatch;
    }

    /**
     * the property to use as the key for a last value queue by default
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultLastValueKey")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the property to use as the key for a last value queue by default")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String defaultLastValueKey;

    public String getDefaultLastValueKey() {
        return defaultLastValueKey;
    }

    public void setDefaultLastValueKey(String defaultLastValueKey) {
        this.defaultLastValueKey = defaultLastValueKey;
    }

    /**
     * whether to treat the queues under the address as a last value queues by default
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultLastValueQueue")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether to treat the queues under the address as a last value queues by default")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean defaultLastValueQueue;

    public Boolean getDefaultLastValueQueue() {
        return defaultLastValueQueue;
    }

    public void setDefaultLastValueQueue(Boolean defaultLastValueQueue) {
        this.defaultLastValueQueue = defaultLastValueQueue;
    }

    /**
     * the maximum number of consumers allowed on this queue at any one time
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultMaxConsumers")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the maximum number of consumers allowed on this queue at any one time")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer defaultMaxConsumers;

    public Integer getDefaultMaxConsumers() {
        return defaultMaxConsumers;
    }

    public void setDefaultMaxConsumers(Integer defaultMaxConsumers) {
        this.defaultMaxConsumers = defaultMaxConsumers;
    }

    /**
     * whether the queue should be non-destructive by default
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultNonDestructive")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether the queue should be non-destructive by default")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean defaultNonDestructive;

    public Boolean getDefaultNonDestructive() {
        return defaultNonDestructive;
    }

    public void setDefaultNonDestructive(Boolean defaultNonDestructive) {
        this.defaultNonDestructive = defaultNonDestructive;
    }

    /**
     * purge the contents of the queue once there are no consumers
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultPurgeOnNoConsumers")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("purge the contents of the queue once there are no consumers")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean defaultPurgeOnNoConsumers;

    public Boolean getDefaultPurgeOnNoConsumers() {
        return defaultPurgeOnNoConsumers;
    }

    public void setDefaultPurgeOnNoConsumers(Boolean defaultPurgeOnNoConsumers) {
        this.defaultPurgeOnNoConsumers = defaultPurgeOnNoConsumers;
    }

    /**
     * the routing-type used on auto-created queues
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultQueueRoutingType")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the routing-type used on auto-created queues")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String defaultQueueRoutingType;

    public String getDefaultQueueRoutingType() {
        return defaultQueueRoutingType;
    }

    public void setDefaultQueueRoutingType(String defaultQueueRoutingType) {
        this.defaultQueueRoutingType = defaultQueueRoutingType;
    }

    /**
     * the default ring-size value for any matching queue which doesnt have ring-size explicitly defined
     */
    @com.fasterxml.jackson.annotation.JsonProperty("defaultRingSize")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the default ring-size value for any matching queue which doesnt have ring-size explicitly defined")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer defaultRingSize;

    public Integer getDefaultRingSize() {
        return defaultRingSize;
    }

    public void setDefaultRingSize(Integer defaultRingSize) {
        this.defaultRingSize = defaultRingSize;
    }

    /**
     * Whether or not set the timestamp of arrival on messages. default false
     */
    @com.fasterxml.jackson.annotation.JsonProperty("enableIngressTimestamp")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Whether or not set the timestamp of arrival on messages. default false")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean enableIngressTimestamp;

    public Boolean getEnableIngressTimestamp() {
        return enableIngressTimestamp;
    }

    public void setEnableIngressTimestamp(Boolean enableIngressTimestamp) {
        this.enableIngressTimestamp = enableIngressTimestamp;
    }

    /**
     * whether or not to enable metrics for metrics plugins on the matching address
     */
    @com.fasterxml.jackson.annotation.JsonProperty("enableMetrics")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("whether or not to enable metrics for metrics plugins on the matching address")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean enableMetrics;

    public Boolean getEnableMetrics() {
        return enableMetrics;
    }

    public void setEnableMetrics(Boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
    }

    /**
     * the address to send expired messages to
     */
    @com.fasterxml.jackson.annotation.JsonProperty("expiryAddress")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the address to send expired messages to")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String expiryAddress;

    public String getExpiryAddress() {
        return expiryAddress;
    }

    public void setExpiryAddress(String expiryAddress) {
        this.expiryAddress = expiryAddress;
    }

    /**
     * Overrides the expiration time for messages using the default value for expiration time. "-1" disables this setting.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("expiryDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Overrides the expiration time for messages using the default value for expiration time. \"-1\" disables this setting.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer expiryDelay;

    public Integer getExpiryDelay() {
        return expiryDelay;
    }

    public void setExpiryDelay(Integer expiryDelay) {
        this.expiryDelay = expiryDelay;
    }

    /**
     * the prefix to use for auto-created expiry queues
     */
    @com.fasterxml.jackson.annotation.JsonProperty("expiryQueuePrefix")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the prefix to use for auto-created expiry queues")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String expiryQueuePrefix;

    public String getExpiryQueuePrefix() {
        return expiryQueuePrefix;
    }

    public void setExpiryQueuePrefix(String expiryQueuePrefix) {
        this.expiryQueuePrefix = expiryQueuePrefix;
    }

    /**
     * the suffix to use for auto-created expiry queues
     */
    @com.fasterxml.jackson.annotation.JsonProperty("expiryQueueSuffix")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the suffix to use for auto-created expiry queues")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String expiryQueueSuffix;

    public String getExpiryQueueSuffix() {
        return expiryQueueSuffix;
    }

    public void setExpiryQueueSuffix(String expiryQueueSuffix) {
        this.expiryQueueSuffix = expiryQueueSuffix;
    }

    /**
     * This is deprecated please use default-last-value-queue instead.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("lastValueQueue")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("This is deprecated please use default-last-value-queue instead.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean lastValueQueue;

    public Boolean getLastValueQueue() {
        return lastValueQueue;
    }

    public void setLastValueQueue(Boolean lastValueQueue) {
        this.lastValueQueue = lastValueQueue;
    }

    /**
     * how many message a management resource can browse
     */
    @com.fasterxml.jackson.annotation.JsonProperty("managementBrowsePageSize")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("how many message a management resource can browse")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer managementBrowsePageSize;

    public Integer getManagementBrowsePageSize() {
        return managementBrowsePageSize;
    }

    public void setManagementBrowsePageSize(Integer managementBrowsePageSize) {
        this.managementBrowsePageSize = managementBrowsePageSize;
    }

    /**
     * max size of the message returned from management API, default 256
     */
    @com.fasterxml.jackson.annotation.JsonProperty("managementMessageAttributeSizeLimit")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("max size of the message returned from management API, default 256")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer managementMessageAttributeSizeLimit;

    public Integer getManagementMessageAttributeSizeLimit() {
        return managementMessageAttributeSizeLimit;
    }

    public void setManagementMessageAttributeSizeLimit(Integer managementMessageAttributeSizeLimit) {
        this.managementMessageAttributeSizeLimit = managementMessageAttributeSizeLimit;
    }

    /**
     * pattern for matching settings against addresses; can use wildards
     */
    @com.fasterxml.jackson.annotation.JsonProperty("match")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("pattern for matching settings against addresses; can use wildards")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String match;

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    /**
     * how many times to attempt to deliver a message before sending to dead letter address
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxDeliveryAttempts")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("how many times to attempt to deliver a message before sending to dead letter address")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer maxDeliveryAttempts;

    public Integer getMaxDeliveryAttempts() {
        return maxDeliveryAttempts;
    }

    public void setMaxDeliveryAttempts(Integer maxDeliveryAttempts) {
        this.maxDeliveryAttempts = maxDeliveryAttempts;
    }

    /**
     * Overrides the expiration time for messages using a higher value. "-1" disables this setting.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxExpiryDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Overrides the expiration time for messages using a higher value. \"-1\" disables this setting.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer maxExpiryDelay;

    public Integer getMaxExpiryDelay() {
        return maxExpiryDelay;
    }

    public void setMaxExpiryDelay(Integer maxExpiryDelay) {
        this.maxExpiryDelay = maxExpiryDelay;
    }

    /**
     * Maximum value for the redelivery-delay
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxRedeliveryDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Maximum value for the redelivery-delay")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer maxRedeliveryDelay;

    public Integer getMaxRedeliveryDelay() {
        return maxRedeliveryDelay;
    }

    public void setMaxRedeliveryDelay(Integer maxRedeliveryDelay) {
        this.maxRedeliveryDelay = maxRedeliveryDelay;
    }

    /**
     * the maximum size in bytes for an address. -1 means no limits. This is used in PAGING, BLOCK and FAIL policies. Supports byte notation like K, Mb, GB, etc.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxSizeBytes")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the maximum size in bytes for an address. -1 means no limits. This is used in PAGING, BLOCK and FAIL policies. Supports byte notation like K, Mb, GB, etc.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String maxSizeBytes;

    public String getMaxSizeBytes() {
        return maxSizeBytes;
    }

    public void setMaxSizeBytes(String maxSizeBytes) {
        this.maxSizeBytes = maxSizeBytes;
    }

    /**
     * used with the address full BLOCK policy, the maximum size in bytes an address can reach before messages start getting rejected. Works in combination with max-size-bytes for AMQP protocol only.  Default = -1 (no limit).
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxSizeBytesRejectThreshold")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("used with the address full BLOCK policy, the maximum size in bytes an address can reach before messages start getting rejected. Works in combination with max-size-bytes for AMQP protocol only.  Default = -1 (no limit).")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer maxSizeBytesRejectThreshold;

    public Integer getMaxSizeBytesRejectThreshold() {
        return maxSizeBytesRejectThreshold;
    }

    public void setMaxSizeBytesRejectThreshold(Integer maxSizeBytesRejectThreshold) {
        this.maxSizeBytesRejectThreshold = maxSizeBytesRejectThreshold;
    }

    /**
     * the maximum number of messages allowed on the address (default -1).  This is used in PAGING, BLOCK and FAIL policies. It does not support notations and it is a simple number of messages allowed.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("maxSizeMessages")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the maximum number of messages allowed on the address (default -1).  This is used in PAGING, BLOCK and FAIL policies. It does not support notations and it is a simple number of messages allowed.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Long maxSizeMessages;

    public Long getMaxSizeMessages() {
        return maxSizeMessages;
    }

    public void setMaxSizeMessages(Long maxSizeMessages) {
        this.maxSizeMessages = maxSizeMessages;
    }

    /**
     * how many days to keep message counter history for this address
     */
    @com.fasterxml.jackson.annotation.JsonProperty("messageCounterHistoryDayLimit")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("how many days to keep message counter history for this address")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer messageCounterHistoryDayLimit;

    public Integer getMessageCounterHistoryDayLimit() {
        return messageCounterHistoryDayLimit;
    }

    public void setMessageCounterHistoryDayLimit(Integer messageCounterHistoryDayLimit) {
        this.messageCounterHistoryDayLimit = messageCounterHistoryDayLimit;
    }

    /**
     * Overrides the expiration time for messages using a lower value. "-1" disables this setting.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("minExpiryDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Overrides the expiration time for messages using a lower value. \"-1\" disables this setting.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer minExpiryDelay;

    public Integer getMinExpiryDelay() {
        return minExpiryDelay;
    }

    public void setMinExpiryDelay(Integer minExpiryDelay) {
        this.minExpiryDelay = minExpiryDelay;
    }

    /**
     * Number of paging files to cache in memory to avoid IO during paging navigation
     */
    @com.fasterxml.jackson.annotation.JsonProperty("pageMaxCacheSize")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Number of paging files to cache in memory to avoid IO during paging navigation")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer pageMaxCacheSize;

    public Integer getPageMaxCacheSize() {
        return pageMaxCacheSize;
    }

    public void setPageMaxCacheSize(Integer pageMaxCacheSize) {
        this.pageMaxCacheSize = pageMaxCacheSize;
    }

    /**
     * The page size in bytes to use for an address. Supports byte notation like K, Mb, GB, etc.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("pageSizeBytes")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The page size in bytes to use for an address. Supports byte notation like K, Mb, GB, etc.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String pageSizeBytes;

    public String getPageSizeBytes() {
        return pageSizeBytes;
    }

    public void setPageSizeBytes(String pageSizeBytes) {
        this.pageSizeBytes = pageSizeBytes;
    }

    /**
     * the time (in ms) to wait before redelivering a cancelled message.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("redeliveryDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the time (in ms) to wait before redelivering a cancelled message.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer redeliveryDelay;

    public Integer getRedeliveryDelay() {
        return redeliveryDelay;
    }

    public void setRedeliveryDelay(Integer redeliveryDelay) {
        this.redeliveryDelay = redeliveryDelay;
    }

    /**
     * how long (in ms) to wait after the last consumer is closed on a queue before redistributing messages.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("redistributionDelay")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("how long (in ms) to wait after the last consumer is closed on a queue before redistributing messages.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer redistributionDelay;

    public Integer getRedistributionDelay() {
        return redistributionDelay;
    }

    public void setRedistributionDelay(Integer redistributionDelay) {
        this.redistributionDelay = redistributionDelay;
    }

    /**
     * the number of messages to preserve for future queues created on the matching address
     */
    @com.fasterxml.jackson.annotation.JsonProperty("retroactiveMessageCount")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("the number of messages to preserve for future queues created on the matching address")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer retroactiveMessageCount;

    public Integer getRetroactiveMessageCount() {
        return retroactiveMessageCount;
    }

    public void setRetroactiveMessageCount(Integer retroactiveMessageCount) {
        this.retroactiveMessageCount = retroactiveMessageCount;
    }

    /**
     * if there are no queues matching this address, whether to forward message to DLA (if it exists for this address)
     */
    @com.fasterxml.jackson.annotation.JsonProperty("sendToDlaOnNoRoute")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("if there are no queues matching this address, whether to forward message to DLA (if it exists for this address)")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Boolean sendToDlaOnNoRoute;

    public Boolean getSendToDlaOnNoRoute() {
        return sendToDlaOnNoRoute;
    }

    public void setSendToDlaOnNoRoute(Boolean sendToDlaOnNoRoute) {
        this.sendToDlaOnNoRoute = sendToDlaOnNoRoute;
    }

    /**
     * How often to check for slow consumers on a particular queue. Measured in seconds.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("slowConsumerCheckPeriod")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("How often to check for slow consumers on a particular queue. Measured in seconds.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer slowConsumerCheckPeriod;

    public Integer getSlowConsumerCheckPeriod() {
        return slowConsumerCheckPeriod;
    }

    public void setSlowConsumerCheckPeriod(Integer slowConsumerCheckPeriod) {
        this.slowConsumerCheckPeriod = slowConsumerCheckPeriod;
    }

    /**
     * what happens when a slow consumer is identified
     */
    @com.fasterxml.jackson.annotation.JsonProperty("slowConsumerPolicy")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("what happens when a slow consumer is identified")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String slowConsumerPolicy;

    public String getSlowConsumerPolicy() {
        return slowConsumerPolicy;
    }

    public void setSlowConsumerPolicy(String slowConsumerPolicy) {
        this.slowConsumerPolicy = slowConsumerPolicy;
    }

    /**
     * The minimum rate of message consumption allowed before a consumer is considered "slow." Measured in messages-per-second.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("slowConsumerThreshold")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("The minimum rate of message consumption allowed before a consumer is considered \"slow.\" Measured in messages-per-second.")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private Integer slowConsumerThreshold;

    public Integer getSlowConsumerThreshold() {
        return slowConsumerThreshold;
    }

    public void setSlowConsumerThreshold(Integer slowConsumerThreshold) {
        this.slowConsumerThreshold = slowConsumerThreshold;
    }

    /**
     * Unit used in specifying slow consumer threshold, default is MESSAGE_PER_SECOND
     */
    @com.fasterxml.jackson.annotation.JsonProperty("slowConsumerThresholdMeasurementUnit")
    @com.fasterxml.jackson.annotation.JsonPropertyDescription("Unit used in specifying slow consumer threshold, default is MESSAGE_PER_SECOND")
    @com.fasterxml.jackson.annotation.JsonSetter(nulls = com.fasterxml.jackson.annotation.Nulls.SKIP)
    private String slowConsumerThresholdMeasurementUnit;

    public String getSlowConsumerThresholdMeasurementUnit() {
        return slowConsumerThresholdMeasurementUnit;
    }

    public void setSlowConsumerThresholdMeasurementUnit(String slowConsumerThresholdMeasurementUnit) {
        this.slowConsumerThresholdMeasurementUnit = slowConsumerThresholdMeasurementUnit;
    }
}

