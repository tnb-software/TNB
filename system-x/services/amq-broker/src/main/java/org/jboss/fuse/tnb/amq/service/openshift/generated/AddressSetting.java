package org.jboss.fuse.tnb.amq.service.openshift.generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.processing.Generated;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "deadLetterAddress",
    "autoCreateDeadLetterResources",
    "deadLetterQueuePrefix",
    "deadLetterQueueSuffix",
    "expiryAddress",
    "autoCreateExpiryResources",
    "expiryQueuePrefix",
    "expiryQueueSuffix",
    "expiryDelay",
    "minExpiryDelay",
    "maxExpiryDelay",
    "redeliveryDelay",
    "redeliveryDelayMultiplier",
    "redeliveryCollisionAvoidanceFactor",
    "maxRedeliveryDelay",
    "maxDeliveryAttempts",
    "maxSizeBytes",
    "maxSizeBytesRejectThreshold",
    "pageSizeBytes",
    "pageMaxCacheSize",
    "addressFullPolicy",
    "messageCounterHistoryDayLimit",
    "lastValueQueue",
    "defaultLastValueQueue",
    "defaultLastValueKey",
    "defaultNonDestructive",
    "defaultExclusiveQueue",
    "defaultGroupRebalance",
    "defaultGroupRebalancePauseDispatch",
    "defaultGroupBuckets",
    "defaultGroupFirstKey",
    "defaultConsumersBeforeDispatch",
    "defaultDelayBeforeDispatch",
    "redistributionDelay",
    "sendToDlaOnNoRoute",
    "slowConsumerThreshold",
    "slowConsumerPolicy",
    "slowConsumerCheckPeriod",
    "autoCreateJmsQueues",
    "autoDeleteJmsQueues",
    "autoCreateJmsTopics",
    "autoDeleteJmsTopics",
    "autoCreateQueues",
    "autoDeleteQueues",
    "autoDeleteCreatedQueues",
    "autoDeleteQueuesDelay",
    "autoDeleteQueuesMessageCount",
    "configDeleteQueues",
    "autoCreateAddresses",
    "autoDeleteAddresses",
    "autoDeleteAddressesDelay",
    "configDeleteAddresses",
    "managementBrowsePageSize",
    "defaultPurgeOnNoConsumers",
    "defaultMaxConsumers",
    "defaultQueueRoutingType",
    "defaultAddressRoutingType",
    "defaultConsumerWindowSize",
    "defaultRingSize",
    "retroactiveMessageCount",
    "enableMetrics",
    "match"
})
@Generated("jsonschema2pojo")
public class AddressSetting implements KubernetesResource {

    /**
     * the address to send dead messages to
     */
    @JsonProperty("deadLetterAddress")
    @JsonPropertyDescription("the address to send dead messages to")
    private String deadLetterAddress;
    /**
     * whether or not to automatically create the dead-letter-address and/or a corresponding queue on that address when a message found to be
     * undeliverable
     */
    @JsonProperty("autoCreateDeadLetterResources")
    @JsonPropertyDescription("whether or not to automatically create the dead-letter-address and/or a corresponding queue on that address when a "
        + "message found to be undeliverable")
    private Boolean autoCreateDeadLetterResources;
    /**
     * the prefix to use for auto-created dead letter queues
     */
    @JsonProperty("deadLetterQueuePrefix")
    @JsonPropertyDescription("the prefix to use for auto-created dead letter queues")
    private String deadLetterQueuePrefix;
    /**
     * the suffix to use for auto-created dead letter queues
     */
    @JsonProperty("deadLetterQueueSuffix")
    @JsonPropertyDescription("the suffix to use for auto-created dead letter queues")
    private String deadLetterQueueSuffix;
    /**
     * the address to send expired messages to
     */
    @JsonProperty("expiryAddress")
    @JsonPropertyDescription("the address to send expired messages to")
    private String expiryAddress;
    /**
     * whether or not to automatically create the expiry-address and/or a corresponding queue on that address when a message is sent to a matching
     * queue
     */
    @JsonProperty("autoCreateExpiryResources")
    @JsonPropertyDescription("whether or not to automatically create the expiry-address and/or a corresponding queue on that address when a message"
        + " is sent to a matching queue")
    private Boolean autoCreateExpiryResources;
    /**
     * the prefix to use for auto-created expiry queues
     */
    @JsonProperty("expiryQueuePrefix")
    @JsonPropertyDescription("the prefix to use for auto-created expiry queues")
    private String expiryQueuePrefix;
    /**
     * the suffix to use for auto-created expiry queues
     */
    @JsonProperty("expiryQueueSuffix")
    @JsonPropertyDescription("the suffix to use for auto-created expiry queues")
    private String expiryQueueSuffix;
    /**
     * Overrides the expiration time for messages using the default value for expiration time. "-1" disables this setting.
     */
    @JsonProperty("expiryDelay")
    @JsonPropertyDescription("Overrides the expiration time for messages using the default value for expiration time. \"-1\" disables this setting.")
    private Integer expiryDelay;
    /**
     * Overrides the expiration time for messages using a lower value. "-1" disables this setting.
     */
    @JsonProperty("minExpiryDelay")
    @JsonPropertyDescription("Overrides the expiration time for messages using a lower value. \"-1\" disables this setting.")
    private Integer minExpiryDelay;
    /**
     * Overrides the expiration time for messages using a higher value. "-1" disables this setting.
     */
    @JsonProperty("maxExpiryDelay")
    @JsonPropertyDescription("Overrides the expiration time for messages using a higher value. \"-1\" disables this setting.")
    private Integer maxExpiryDelay;
    /**
     * the time (in ms) to wait before redelivering a cancelled message.
     */
    @JsonProperty("redeliveryDelay")
    @JsonPropertyDescription("the time (in ms) to wait before redelivering a cancelled message.")
    private Integer redeliveryDelay;
    /**
     * multiplier to apply to the redelivery-delay
     */
    @JsonProperty("redeliveryDelayMultiplier")
    @JsonPropertyDescription("multiplier to apply to the redelivery-delay")
    private Double redeliveryDelayMultiplier;
    /**
     * factor by which to modify the redelivery delay slightly to avoid collisions
     */
    @JsonProperty("redeliveryCollisionAvoidanceFactor")
    @JsonPropertyDescription("factor by which to modify the redelivery delay slightly to avoid collisions")
    private Double redeliveryCollisionAvoidanceFactor;
    /**
     * Maximum value for the redelivery-delay
     */
    @JsonProperty("maxRedeliveryDelay")
    @JsonPropertyDescription("Maximum value for the redelivery-delay")
    private Integer maxRedeliveryDelay;
    /**
     * how many times to attempt to deliver a message before sending to dead letter address
     */
    @JsonProperty("maxDeliveryAttempts")
    @JsonPropertyDescription("how many times to attempt to deliver a message before sending to dead letter address")
    private Integer maxDeliveryAttempts;
    /**
     * the maximum size in bytes for an address. -1 means no limits. This is used in PAGING, BLOCK and FAIL policies. Supports byte notation like
     * K, Mb, GB, etc.
     */
    @JsonProperty("maxSizeBytes")
    @JsonPropertyDescription("the maximum size in bytes for an address. -1 means no limits. This is used in PAGING, BLOCK and FAIL policies. "
        + "Supports byte notation like K, Mb, GB, etc.")
    private String maxSizeBytes;
    /**
     * used with the address full BLOCK policy, the maximum size in bytes an address can reach before messages start getting rejected. Works in
     * combination with max-size-bytes for AMQP protocol only. Default = -1 (no limit).
     */
    @JsonProperty("maxSizeBytesRejectThreshold")
    @JsonPropertyDescription("used with the address full BLOCK policy, the maximum size in bytes an address can reach before messages start getting"
        + " rejected. Works in combination with max-size-bytes for AMQP protocol only. Default = -1 (no limit).")
    private Integer maxSizeBytesRejectThreshold;
    /**
     * The page size in bytes to use for an address. Supports byte notation like K, Mb, GB, etc.
     */
    @JsonProperty("pageSizeBytes")
    @JsonPropertyDescription("The page size in bytes to use for an address. Supports byte notation like K, Mb, GB, etc.")
    private String pageSizeBytes;
    /**
     * Number of paging files to cache in memory to avoid IO during paging navigation
     */
    @JsonProperty("pageMaxCacheSize")
    @JsonPropertyDescription("Number of paging files to cache in memory to avoid IO during paging navigation")
    private Integer pageMaxCacheSize;
    /**
     * what happens when an address where maxSizeBytes is specified becomes full
     */
    @JsonProperty("addressFullPolicy")
    @JsonPropertyDescription("what happens when an address where maxSizeBytes is specified becomes full")
    private AddressSetting.AddressFullPolicy addressFullPolicy;
    /**
     * how many days to keep message counter history for this address
     */
    @JsonProperty("messageCounterHistoryDayLimit")
    @JsonPropertyDescription("how many days to keep message counter history for this address")
    private Integer messageCounterHistoryDayLimit;
    /**
     * This is deprecated please use default-last-value-queue instead.
     */
    @JsonProperty("lastValueQueue")
    @JsonPropertyDescription("This is deprecated please use default-last-value-queue instead.")
    private Boolean lastValueQueue;
    /**
     * whether to treat the queues under the address as a last value queues by default
     */
    @JsonProperty("defaultLastValueQueue")
    @JsonPropertyDescription("whether to treat the queues under the address as a last value queues by default")
    private Boolean defaultLastValueQueue;
    /**
     * the property to use as the key for a last value queue by default
     */
    @JsonProperty("defaultLastValueKey")
    @JsonPropertyDescription("the property to use as the key for a last value queue by default")
    private String defaultLastValueKey;
    /**
     * whether the queue should be non-destructive by default
     */
    @JsonProperty("defaultNonDestructive")
    @JsonPropertyDescription("whether the queue should be non-destructive by default")
    private Boolean defaultNonDestructive;
    /**
     * whether to treat the queues under the address as exclusive queues by default
     */
    @JsonProperty("defaultExclusiveQueue")
    @JsonPropertyDescription("whether to treat the queues under the address as exclusive queues by default")
    private Boolean defaultExclusiveQueue;
    /**
     * whether to rebalance groups when a consumer is added
     */
    @JsonProperty("defaultGroupRebalance")
    @JsonPropertyDescription("whether to rebalance groups when a consumer is added")
    private Boolean defaultGroupRebalance;
    /**
     * whether to pause dispatch when rebalancing groups
     */
    @JsonProperty("defaultGroupRebalancePauseDispatch")
    @JsonPropertyDescription("whether to pause dispatch when rebalancing groups")
    private Boolean defaultGroupRebalancePauseDispatch;
    /**
     * number of buckets to use for grouping, -1 (default) is unlimited and uses the raw group, 0 disables message groups.
     */
    @JsonProperty("defaultGroupBuckets")
    @JsonPropertyDescription("number of buckets to use for grouping, -1 (default) is unlimited and uses the raw group, 0 disables message groups.")
    private Integer defaultGroupBuckets;
    /**
     * key used to mark a message is first in a group for a consumer
     */
    @JsonProperty("defaultGroupFirstKey")
    @JsonPropertyDescription("key used to mark a message is first in a group for a consumer")
    private String defaultGroupFirstKey;
    /**
     * the default number of consumers needed before dispatch can start for queues under the address.
     */
    @JsonProperty("defaultConsumersBeforeDispatch")
    @JsonPropertyDescription("the default number of consumers needed before dispatch can start for queues under the address.")
    private Integer defaultConsumersBeforeDispatch;
    /**
     * the default delay (in milliseconds) to wait before dispatching if number of consumers before dispatch is not met for queues under the address.
     */
    @JsonProperty("defaultDelayBeforeDispatch")
    @JsonPropertyDescription("the default delay (in milliseconds) to wait before dispatching if number of consumers before dispatch is not met for "
        + "queues under the address.")
    private Integer defaultDelayBeforeDispatch;
    /**
     * how long (in ms) to wait after the last consumer is closed on a queue before redistributing messages.
     */
    @JsonProperty("redistributionDelay")
    @JsonPropertyDescription("how long (in ms) to wait after the last consumer is closed on a queue before redistributing messages.")
    private Integer redistributionDelay;
    /**
     * if there are no queues matching this address, whether to forward message to DLA (if it exists for this address)
     */
    @JsonProperty("sendToDlaOnNoRoute")
    @JsonPropertyDescription("if there are no queues matching this address, whether to forward message to DLA (if it exists for this address)")
    private Boolean sendToDlaOnNoRoute;
    /**
     * The minimum rate of message consumption allowed before a consumer is considered "slow." Measured in messages-per-second.
     */
    @JsonProperty("slowConsumerThreshold")
    @JsonPropertyDescription("The minimum rate of message consumption allowed before a consumer is considered \"slow.\" Measured in "
        + "messages-per-second.")
    private Integer slowConsumerThreshold;
    /**
     * what happens when a slow consumer is identified
     */
    @JsonProperty("slowConsumerPolicy")
    @JsonPropertyDescription("what happens when a slow consumer is identified")
    private AddressSetting.SlowConsumerPolicy slowConsumerPolicy;
    /**
     * How often to check for slow consumers on a particular queue. Measured in seconds.
     */
    @JsonProperty("slowConsumerCheckPeriod")
    @JsonPropertyDescription("How often to check for slow consumers on a particular queue. Measured in seconds.")
    private Integer slowConsumerCheckPeriod;
    /**
     * DEPRECATED. whether or not to automatically create JMS queues when a producer sends or a consumer connects to a queue
     */
    @JsonProperty("autoCreateJmsQueues")
    @JsonPropertyDescription("DEPRECATED. whether or not to automatically create JMS queues when a producer sends or a consumer connects to a queue")
    private Boolean autoCreateJmsQueues;
    /**
     * DEPRECATED. whether or not to delete auto-created JMS queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteJmsQueues")
    @JsonPropertyDescription("DEPRECATED. whether or not to delete auto-created JMS queues when the queue has 0 consumers and 0 messages")
    private Boolean autoDeleteJmsQueues;
    /**
     * DEPRECATED. whether or not to automatically create JMS topics when a producer sends or a consumer subscribes to a topic
     */
    @JsonProperty("autoCreateJmsTopics")
    @JsonPropertyDescription("DEPRECATED. whether or not to automatically create JMS topics when a producer sends or a consumer subscribes to a "
        + "topic")
    private Boolean autoCreateJmsTopics;
    /**
     * DEPRECATED. whether or not to delete auto-created JMS topics when the last subscription is closed
     */
    @JsonProperty("autoDeleteJmsTopics")
    @JsonPropertyDescription("DEPRECATED. whether or not to delete auto-created JMS topics when the last subscription is closed")
    private Boolean autoDeleteJmsTopics;
    /**
     * whether or not to automatically create a queue when a client sends a message to or attempts to consume a message from a queue
     */
    @JsonProperty("autoCreateQueues")
    @JsonPropertyDescription("whether or not to automatically create a queue when a client sends a message to or attempts to consume a message from"
        + " a queue")
    private Boolean autoCreateQueues;
    /**
     * whether or not to delete auto-created queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteQueues")
    @JsonPropertyDescription("whether or not to delete auto-created queues when the queue has 0 consumers and 0 messages")
    private Boolean autoDeleteQueues;
    /**
     * whether or not to delete created queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteCreatedQueues")
    @JsonPropertyDescription("whether or not to delete created queues when the queue has 0 consumers and 0 messages")
    private Boolean autoDeleteCreatedQueues;
    /**
     * how long to wait (in milliseconds) before deleting auto-created queues after the queue has 0 consumers.
     */
    @JsonProperty("autoDeleteQueuesDelay")
    @JsonPropertyDescription("how long to wait (in milliseconds) before deleting auto-created queues after the queue has 0 consumers.")
    private Integer autoDeleteQueuesDelay;
    /**
     * the message count the queue must be at or below before it can be evaluated  to be auto deleted, 0 waits until empty queue (default) and -1
     * disables this check.
     */
    @JsonProperty("autoDeleteQueuesMessageCount")
    @JsonPropertyDescription("the message count the queue must be at or below before it can be evaluated  to be auto deleted, 0 waits until empty "
        + "queue (default) and -1 disables this check.")
    private Integer autoDeleteQueuesMessageCount;
    /**
     * What to do when a queue is no longer in broker.xml. OFF = will do nothing queues will remain, FORCE = delete queues even if messages remaining.
     */
    @JsonProperty("configDeleteQueues")
    @JsonPropertyDescription("What to do when a queue is no longer in broker.xml. OFF = will do nothing queues will remain, FORCE = delete queues "
        + "even if messages remaining.")
    private AddressSetting.ConfigDeleteQueues configDeleteQueues;
    /**
     * whether or not to automatically create addresses when a client sends a message to or attempts to consume a message from a queue mapped to an
     * address that doesnt exist
     */
    @JsonProperty("autoCreateAddresses")
    @JsonPropertyDescription("whether or not to automatically create addresses when a client sends a message to or attempts to consume a message "
        + "from a queue mapped to an address that doesnt exist")
    private Boolean autoCreateAddresses;
    /**
     * whether or not to delete auto-created addresses when it no longer has any queues
     */
    @JsonProperty("autoDeleteAddresses")
    @JsonPropertyDescription("whether or not to delete auto-created addresses when it no longer has any queues")
    private Boolean autoDeleteAddresses;
    /**
     * how long to wait (in milliseconds) before deleting auto-created addresses after they no longer have any queues
     */
    @JsonProperty("autoDeleteAddressesDelay")
    @JsonPropertyDescription("how long to wait (in milliseconds) before deleting auto-created addresses after they no longer have any queues")
    private Integer autoDeleteAddressesDelay;
    /**
     * What to do when an address is no longer in broker.xml. OFF = will do nothing addresses will remain, FORCE = delete address and its queues
     * even if messages remaining.
     */
    @JsonProperty("configDeleteAddresses")
    @JsonPropertyDescription("What to do when an address is no longer in broker.xml. OFF = will do nothing addresses will remain, FORCE = delete "
        + "address and its queues even if messages remaining.")
    private AddressSetting.ConfigDeleteAddresses configDeleteAddresses;
    /**
     * how many message a management resource can browse
     */
    @JsonProperty("managementBrowsePageSize")
    @JsonPropertyDescription("how many message a management resource can browse")
    private Integer managementBrowsePageSize;
    /**
     * purge the contents of the queue once there are no consumers
     */
    @JsonProperty("defaultPurgeOnNoConsumers")
    @JsonPropertyDescription("purge the contents of the queue once there are no consumers")
    private Boolean defaultPurgeOnNoConsumers;
    /**
     * the maximum number of consumers allowed on this queue at any one time
     */
    @JsonProperty("defaultMaxConsumers")
    @JsonPropertyDescription("the maximum number of consumers allowed on this queue at any one time")
    private Integer defaultMaxConsumers;
    /**
     * the routing-type used on auto-created queues
     */
    @JsonProperty("defaultQueueRoutingType")
    @JsonPropertyDescription("the routing-type used on auto-created queues")
    private AddressSetting.DefaultQueueRoutingType defaultQueueRoutingType;
    /**
     * the routing-type used on auto-created addresses
     */
    @JsonProperty("defaultAddressRoutingType")
    @JsonPropertyDescription("the routing-type used on auto-created addresses")
    private AddressSetting.DefaultAddressRoutingType defaultAddressRoutingType;
    /**
     * the default window size for a consumer
     */
    @JsonProperty("defaultConsumerWindowSize")
    @JsonPropertyDescription("the default window size for a consumer")
    private Integer defaultConsumerWindowSize;
    /**
     * the default ring-size value for any matching queue which doesnt have ring-size explicitly defined
     */
    @JsonProperty("defaultRingSize")
    @JsonPropertyDescription("the default ring-size value for any matching queue which doesnt have ring-size explicitly defined")
    private Integer defaultRingSize;
    /**
     * the number of messages to preserve for future queues created on the matching address
     */
    @JsonProperty("retroactiveMessageCount")
    @JsonPropertyDescription("the number of messages to preserve for future queues created on the matching address")
    private Integer retroactiveMessageCount;
    /**
     * whether or not to enable metrics for metrics plugins on the matching address
     */
    @JsonProperty("enableMetrics")
    @JsonPropertyDescription("whether or not to enable metrics for metrics plugins on the matching address")
    private Boolean enableMetrics;
    /**
     * pattern for matching settings against addresses; can use wildards
     */
    @JsonProperty("match")
    @JsonPropertyDescription("pattern for matching settings against addresses; can use wildards")
    private String match;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * the address to send dead messages to
     */
    @JsonProperty("deadLetterAddress")
    public String getDeadLetterAddress() {
        return deadLetterAddress;
    }

    /**
     * the address to send dead messages to
     */
    @JsonProperty("deadLetterAddress")
    public void setDeadLetterAddress(String deadLetterAddress) {
        this.deadLetterAddress = deadLetterAddress;
    }

    public AddressSetting withDeadLetterAddress(String deadLetterAddress) {
        this.deadLetterAddress = deadLetterAddress;
        return this;
    }

    /**
     * whether or not to automatically create the dead-letter-address and/or a corresponding queue on that address when a message found to be
     * undeliverable
     */
    @JsonProperty("autoCreateDeadLetterResources")
    public Boolean getAutoCreateDeadLetterResources() {
        return autoCreateDeadLetterResources;
    }

    /**
     * whether or not to automatically create the dead-letter-address and/or a corresponding queue on that address when a message found to be
     * undeliverable
     */
    @JsonProperty("autoCreateDeadLetterResources")
    public void setAutoCreateDeadLetterResources(Boolean autoCreateDeadLetterResources) {
        this.autoCreateDeadLetterResources = autoCreateDeadLetterResources;
    }

    public AddressSetting withAutoCreateDeadLetterResources(Boolean autoCreateDeadLetterResources) {
        this.autoCreateDeadLetterResources = autoCreateDeadLetterResources;
        return this;
    }

    /**
     * the prefix to use for auto-created dead letter queues
     */
    @JsonProperty("deadLetterQueuePrefix")
    public String getDeadLetterQueuePrefix() {
        return deadLetterQueuePrefix;
    }

    /**
     * the prefix to use for auto-created dead letter queues
     */
    @JsonProperty("deadLetterQueuePrefix")
    public void setDeadLetterQueuePrefix(String deadLetterQueuePrefix) {
        this.deadLetterQueuePrefix = deadLetterQueuePrefix;
    }

    public AddressSetting withDeadLetterQueuePrefix(String deadLetterQueuePrefix) {
        this.deadLetterQueuePrefix = deadLetterQueuePrefix;
        return this;
    }

    /**
     * the suffix to use for auto-created dead letter queues
     */
    @JsonProperty("deadLetterQueueSuffix")
    public String getDeadLetterQueueSuffix() {
        return deadLetterQueueSuffix;
    }

    /**
     * the suffix to use for auto-created dead letter queues
     */
    @JsonProperty("deadLetterQueueSuffix")
    public void setDeadLetterQueueSuffix(String deadLetterQueueSuffix) {
        this.deadLetterQueueSuffix = deadLetterQueueSuffix;
    }

    public AddressSetting withDeadLetterQueueSuffix(String deadLetterQueueSuffix) {
        this.deadLetterQueueSuffix = deadLetterQueueSuffix;
        return this;
    }

    /**
     * the address to send expired messages to
     */
    @JsonProperty("expiryAddress")
    public String getExpiryAddress() {
        return expiryAddress;
    }

    /**
     * the address to send expired messages to
     */
    @JsonProperty("expiryAddress")
    public void setExpiryAddress(String expiryAddress) {
        this.expiryAddress = expiryAddress;
    }

    public AddressSetting withExpiryAddress(String expiryAddress) {
        this.expiryAddress = expiryAddress;
        return this;
    }

    /**
     * whether or not to automatically create the expiry-address and/or a corresponding queue on that address when a message is sent to a matching
     * queue
     */
    @JsonProperty("autoCreateExpiryResources")
    public Boolean getAutoCreateExpiryResources() {
        return autoCreateExpiryResources;
    }

    /**
     * whether or not to automatically create the expiry-address and/or a corresponding queue on that address when a message is sent to a matching
     * queue
     */
    @JsonProperty("autoCreateExpiryResources")
    public void setAutoCreateExpiryResources(Boolean autoCreateExpiryResources) {
        this.autoCreateExpiryResources = autoCreateExpiryResources;
    }

    public AddressSetting withAutoCreateExpiryResources(Boolean autoCreateExpiryResources) {
        this.autoCreateExpiryResources = autoCreateExpiryResources;
        return this;
    }

    /**
     * the prefix to use for auto-created expiry queues
     */
    @JsonProperty("expiryQueuePrefix")
    public String getExpiryQueuePrefix() {
        return expiryQueuePrefix;
    }

    /**
     * the prefix to use for auto-created expiry queues
     */
    @JsonProperty("expiryQueuePrefix")
    public void setExpiryQueuePrefix(String expiryQueuePrefix) {
        this.expiryQueuePrefix = expiryQueuePrefix;
    }

    public AddressSetting withExpiryQueuePrefix(String expiryQueuePrefix) {
        this.expiryQueuePrefix = expiryQueuePrefix;
        return this;
    }

    /**
     * the suffix to use for auto-created expiry queues
     */
    @JsonProperty("expiryQueueSuffix")
    public String getExpiryQueueSuffix() {
        return expiryQueueSuffix;
    }

    /**
     * the suffix to use for auto-created expiry queues
     */
    @JsonProperty("expiryQueueSuffix")
    public void setExpiryQueueSuffix(String expiryQueueSuffix) {
        this.expiryQueueSuffix = expiryQueueSuffix;
    }

    public AddressSetting withExpiryQueueSuffix(String expiryQueueSuffix) {
        this.expiryQueueSuffix = expiryQueueSuffix;
        return this;
    }

    /**
     * Overrides the expiration time for messages using the default value for expiration time. "-1" disables this setting.
     */
    @JsonProperty("expiryDelay")
    public Integer getExpiryDelay() {
        return expiryDelay;
    }

    /**
     * Overrides the expiration time for messages using the default value for expiration time. "-1" disables this setting.
     */
    @JsonProperty("expiryDelay")
    public void setExpiryDelay(Integer expiryDelay) {
        this.expiryDelay = expiryDelay;
    }

    public AddressSetting withExpiryDelay(Integer expiryDelay) {
        this.expiryDelay = expiryDelay;
        return this;
    }

    /**
     * Overrides the expiration time for messages using a lower value. "-1" disables this setting.
     */
    @JsonProperty("minExpiryDelay")
    public Integer getMinExpiryDelay() {
        return minExpiryDelay;
    }

    /**
     * Overrides the expiration time for messages using a lower value. "-1" disables this setting.
     */
    @JsonProperty("minExpiryDelay")
    public void setMinExpiryDelay(Integer minExpiryDelay) {
        this.minExpiryDelay = minExpiryDelay;
    }

    public AddressSetting withMinExpiryDelay(Integer minExpiryDelay) {
        this.minExpiryDelay = minExpiryDelay;
        return this;
    }

    /**
     * Overrides the expiration time for messages using a higher value. "-1" disables this setting.
     */
    @JsonProperty("maxExpiryDelay")
    public Integer getMaxExpiryDelay() {
        return maxExpiryDelay;
    }

    /**
     * Overrides the expiration time for messages using a higher value. "-1" disables this setting.
     */
    @JsonProperty("maxExpiryDelay")
    public void setMaxExpiryDelay(Integer maxExpiryDelay) {
        this.maxExpiryDelay = maxExpiryDelay;
    }

    public AddressSetting withMaxExpiryDelay(Integer maxExpiryDelay) {
        this.maxExpiryDelay = maxExpiryDelay;
        return this;
    }

    /**
     * the time (in ms) to wait before redelivering a cancelled message.
     */
    @JsonProperty("redeliveryDelay")
    public Integer getRedeliveryDelay() {
        return redeliveryDelay;
    }

    /**
     * the time (in ms) to wait before redelivering a cancelled message.
     */
    @JsonProperty("redeliveryDelay")
    public void setRedeliveryDelay(Integer redeliveryDelay) {
        this.redeliveryDelay = redeliveryDelay;
    }

    public AddressSetting withRedeliveryDelay(Integer redeliveryDelay) {
        this.redeliveryDelay = redeliveryDelay;
        return this;
    }

    /**
     * multiplier to apply to the redelivery-delay
     */
    @JsonProperty("redeliveryDelayMultiplier")
    public Double getRedeliveryDelayMultiplier() {
        return redeliveryDelayMultiplier;
    }

    /**
     * multiplier to apply to the redelivery-delay
     */
    @JsonProperty("redeliveryDelayMultiplier")
    public void setRedeliveryDelayMultiplier(Double redeliveryDelayMultiplier) {
        this.redeliveryDelayMultiplier = redeliveryDelayMultiplier;
    }

    public AddressSetting withRedeliveryDelayMultiplier(Double redeliveryDelayMultiplier) {
        this.redeliveryDelayMultiplier = redeliveryDelayMultiplier;
        return this;
    }

    /**
     * factor by which to modify the redelivery delay slightly to avoid collisions
     */
    @JsonProperty("redeliveryCollisionAvoidanceFactor")
    public Double getRedeliveryCollisionAvoidanceFactor() {
        return redeliveryCollisionAvoidanceFactor;
    }

    /**
     * factor by which to modify the redelivery delay slightly to avoid collisions
     */
    @JsonProperty("redeliveryCollisionAvoidanceFactor")
    public void setRedeliveryCollisionAvoidanceFactor(Double redeliveryCollisionAvoidanceFactor) {
        this.redeliveryCollisionAvoidanceFactor = redeliveryCollisionAvoidanceFactor;
    }

    public AddressSetting withRedeliveryCollisionAvoidanceFactor(Double redeliveryCollisionAvoidanceFactor) {
        this.redeliveryCollisionAvoidanceFactor = redeliveryCollisionAvoidanceFactor;
        return this;
    }

    /**
     * Maximum value for the redelivery-delay
     */
    @JsonProperty("maxRedeliveryDelay")
    public Integer getMaxRedeliveryDelay() {
        return maxRedeliveryDelay;
    }

    /**
     * Maximum value for the redelivery-delay
     */
    @JsonProperty("maxRedeliveryDelay")
    public void setMaxRedeliveryDelay(Integer maxRedeliveryDelay) {
        this.maxRedeliveryDelay = maxRedeliveryDelay;
    }

    public AddressSetting withMaxRedeliveryDelay(Integer maxRedeliveryDelay) {
        this.maxRedeliveryDelay = maxRedeliveryDelay;
        return this;
    }

    /**
     * how many times to attempt to deliver a message before sending to dead letter address
     */
    @JsonProperty("maxDeliveryAttempts")
    public Integer getMaxDeliveryAttempts() {
        return maxDeliveryAttempts;
    }

    /**
     * how many times to attempt to deliver a message before sending to dead letter address
     */
    @JsonProperty("maxDeliveryAttempts")
    public void setMaxDeliveryAttempts(Integer maxDeliveryAttempts) {
        this.maxDeliveryAttempts = maxDeliveryAttempts;
    }

    public AddressSetting withMaxDeliveryAttempts(Integer maxDeliveryAttempts) {
        this.maxDeliveryAttempts = maxDeliveryAttempts;
        return this;
    }

    /**
     * the maximum size in bytes for an address. -1 means no limits. This is used in PAGING, BLOCK and FAIL policies. Supports byte notation like
     * K, Mb, GB, etc.
     */
    @JsonProperty("maxSizeBytes")
    public String getMaxSizeBytes() {
        return maxSizeBytes;
    }

    /**
     * the maximum size in bytes for an address. -1 means no limits. This is used in PAGING, BLOCK and FAIL policies. Supports byte notation like
     * K, Mb, GB, etc.
     */
    @JsonProperty("maxSizeBytes")
    public void setMaxSizeBytes(String maxSizeBytes) {
        this.maxSizeBytes = maxSizeBytes;
    }

    public AddressSetting withMaxSizeBytes(String maxSizeBytes) {
        this.maxSizeBytes = maxSizeBytes;
        return this;
    }

    /**
     * used with the address full BLOCK policy, the maximum size in bytes an address can reach before messages start getting rejected. Works in
     * combination with max-size-bytes for AMQP protocol only. Default = -1 (no limit).
     */
    @JsonProperty("maxSizeBytesRejectThreshold")
    public Integer getMaxSizeBytesRejectThreshold() {
        return maxSizeBytesRejectThreshold;
    }

    /**
     * used with the address full BLOCK policy, the maximum size in bytes an address can reach before messages start getting rejected. Works in
     * combination with max-size-bytes for AMQP protocol only. Default = -1 (no limit).
     */
    @JsonProperty("maxSizeBytesRejectThreshold")
    public void setMaxSizeBytesRejectThreshold(Integer maxSizeBytesRejectThreshold) {
        this.maxSizeBytesRejectThreshold = maxSizeBytesRejectThreshold;
    }

    public AddressSetting withMaxSizeBytesRejectThreshold(Integer maxSizeBytesRejectThreshold) {
        this.maxSizeBytesRejectThreshold = maxSizeBytesRejectThreshold;
        return this;
    }

    /**
     * The page size in bytes to use for an address. Supports byte notation like K, Mb, GB, etc.
     */
    @JsonProperty("pageSizeBytes")
    public String getPageSizeBytes() {
        return pageSizeBytes;
    }

    /**
     * The page size in bytes to use for an address. Supports byte notation like K, Mb, GB, etc.
     */
    @JsonProperty("pageSizeBytes")
    public void setPageSizeBytes(String pageSizeBytes) {
        this.pageSizeBytes = pageSizeBytes;
    }

    public AddressSetting withPageSizeBytes(String pageSizeBytes) {
        this.pageSizeBytes = pageSizeBytes;
        return this;
    }

    /**
     * Number of paging files to cache in memory to avoid IO during paging navigation
     */
    @JsonProperty("pageMaxCacheSize")
    public Integer getPageMaxCacheSize() {
        return pageMaxCacheSize;
    }

    /**
     * Number of paging files to cache in memory to avoid IO during paging navigation
     */
    @JsonProperty("pageMaxCacheSize")
    public void setPageMaxCacheSize(Integer pageMaxCacheSize) {
        this.pageMaxCacheSize = pageMaxCacheSize;
    }

    public AddressSetting withPageMaxCacheSize(Integer pageMaxCacheSize) {
        this.pageMaxCacheSize = pageMaxCacheSize;
        return this;
    }

    /**
     * what happens when an address where maxSizeBytes is specified becomes full
     */
    @JsonProperty("addressFullPolicy")
    public AddressSetting.AddressFullPolicy getAddressFullPolicy() {
        return addressFullPolicy;
    }

    /**
     * what happens when an address where maxSizeBytes is specified becomes full
     */
    @JsonProperty("addressFullPolicy")
    public void setAddressFullPolicy(AddressSetting.AddressFullPolicy addressFullPolicy) {
        this.addressFullPolicy = addressFullPolicy;
    }

    public AddressSetting withAddressFullPolicy(AddressSetting.AddressFullPolicy addressFullPolicy) {
        this.addressFullPolicy = addressFullPolicy;
        return this;
    }

    /**
     * how many days to keep message counter history for this address
     */
    @JsonProperty("messageCounterHistoryDayLimit")
    public Integer getMessageCounterHistoryDayLimit() {
        return messageCounterHistoryDayLimit;
    }

    /**
     * how many days to keep message counter history for this address
     */
    @JsonProperty("messageCounterHistoryDayLimit")
    public void setMessageCounterHistoryDayLimit(Integer messageCounterHistoryDayLimit) {
        this.messageCounterHistoryDayLimit = messageCounterHistoryDayLimit;
    }

    public AddressSetting withMessageCounterHistoryDayLimit(Integer messageCounterHistoryDayLimit) {
        this.messageCounterHistoryDayLimit = messageCounterHistoryDayLimit;
        return this;
    }

    /**
     * This is deprecated please use default-last-value-queue instead.
     */
    @JsonProperty("lastValueQueue")
    public Boolean getLastValueQueue() {
        return lastValueQueue;
    }

    /**
     * This is deprecated please use default-last-value-queue instead.
     */
    @JsonProperty("lastValueQueue")
    public void setLastValueQueue(Boolean lastValueQueue) {
        this.lastValueQueue = lastValueQueue;
    }

    public AddressSetting withLastValueQueue(Boolean lastValueQueue) {
        this.lastValueQueue = lastValueQueue;
        return this;
    }

    /**
     * whether to treat the queues under the address as a last value queues by default
     */
    @JsonProperty("defaultLastValueQueue")
    public Boolean getDefaultLastValueQueue() {
        return defaultLastValueQueue;
    }

    /**
     * whether to treat the queues under the address as a last value queues by default
     */
    @JsonProperty("defaultLastValueQueue")
    public void setDefaultLastValueQueue(Boolean defaultLastValueQueue) {
        this.defaultLastValueQueue = defaultLastValueQueue;
    }

    public AddressSetting withDefaultLastValueQueue(Boolean defaultLastValueQueue) {
        this.defaultLastValueQueue = defaultLastValueQueue;
        return this;
    }

    /**
     * the property to use as the key for a last value queue by default
     */
    @JsonProperty("defaultLastValueKey")
    public String getDefaultLastValueKey() {
        return defaultLastValueKey;
    }

    /**
     * the property to use as the key for a last value queue by default
     */
    @JsonProperty("defaultLastValueKey")
    public void setDefaultLastValueKey(String defaultLastValueKey) {
        this.defaultLastValueKey = defaultLastValueKey;
    }

    public AddressSetting withDefaultLastValueKey(String defaultLastValueKey) {
        this.defaultLastValueKey = defaultLastValueKey;
        return this;
    }

    /**
     * whether the queue should be non-destructive by default
     */
    @JsonProperty("defaultNonDestructive")
    public Boolean getDefaultNonDestructive() {
        return defaultNonDestructive;
    }

    /**
     * whether the queue should be non-destructive by default
     */
    @JsonProperty("defaultNonDestructive")
    public void setDefaultNonDestructive(Boolean defaultNonDestructive) {
        this.defaultNonDestructive = defaultNonDestructive;
    }

    public AddressSetting withDefaultNonDestructive(Boolean defaultNonDestructive) {
        this.defaultNonDestructive = defaultNonDestructive;
        return this;
    }

    /**
     * whether to treat the queues under the address as exclusive queues by default
     */
    @JsonProperty("defaultExclusiveQueue")
    public Boolean getDefaultExclusiveQueue() {
        return defaultExclusiveQueue;
    }

    /**
     * whether to treat the queues under the address as exclusive queues by default
     */
    @JsonProperty("defaultExclusiveQueue")
    public void setDefaultExclusiveQueue(Boolean defaultExclusiveQueue) {
        this.defaultExclusiveQueue = defaultExclusiveQueue;
    }

    public AddressSetting withDefaultExclusiveQueue(Boolean defaultExclusiveQueue) {
        this.defaultExclusiveQueue = defaultExclusiveQueue;
        return this;
    }

    /**
     * whether to rebalance groups when a consumer is added
     */
    @JsonProperty("defaultGroupRebalance")
    public Boolean getDefaultGroupRebalance() {
        return defaultGroupRebalance;
    }

    /**
     * whether to rebalance groups when a consumer is added
     */
    @JsonProperty("defaultGroupRebalance")
    public void setDefaultGroupRebalance(Boolean defaultGroupRebalance) {
        this.defaultGroupRebalance = defaultGroupRebalance;
    }

    public AddressSetting withDefaultGroupRebalance(Boolean defaultGroupRebalance) {
        this.defaultGroupRebalance = defaultGroupRebalance;
        return this;
    }

    /**
     * whether to pause dispatch when rebalancing groups
     */
    @JsonProperty("defaultGroupRebalancePauseDispatch")
    public Boolean getDefaultGroupRebalancePauseDispatch() {
        return defaultGroupRebalancePauseDispatch;
    }

    /**
     * whether to pause dispatch when rebalancing groups
     */
    @JsonProperty("defaultGroupRebalancePauseDispatch")
    public void setDefaultGroupRebalancePauseDispatch(Boolean defaultGroupRebalancePauseDispatch) {
        this.defaultGroupRebalancePauseDispatch = defaultGroupRebalancePauseDispatch;
    }

    public AddressSetting withDefaultGroupRebalancePauseDispatch(Boolean defaultGroupRebalancePauseDispatch) {
        this.defaultGroupRebalancePauseDispatch = defaultGroupRebalancePauseDispatch;
        return this;
    }

    /**
     * number of buckets to use for grouping, -1 (default) is unlimited and uses the raw group, 0 disables message groups.
     */
    @JsonProperty("defaultGroupBuckets")
    public Integer getDefaultGroupBuckets() {
        return defaultGroupBuckets;
    }

    /**
     * number of buckets to use for grouping, -1 (default) is unlimited and uses the raw group, 0 disables message groups.
     */
    @JsonProperty("defaultGroupBuckets")
    public void setDefaultGroupBuckets(Integer defaultGroupBuckets) {
        this.defaultGroupBuckets = defaultGroupBuckets;
    }

    public AddressSetting withDefaultGroupBuckets(Integer defaultGroupBuckets) {
        this.defaultGroupBuckets = defaultGroupBuckets;
        return this;
    }

    /**
     * key used to mark a message is first in a group for a consumer
     */
    @JsonProperty("defaultGroupFirstKey")
    public String getDefaultGroupFirstKey() {
        return defaultGroupFirstKey;
    }

    /**
     * key used to mark a message is first in a group for a consumer
     */
    @JsonProperty("defaultGroupFirstKey")
    public void setDefaultGroupFirstKey(String defaultGroupFirstKey) {
        this.defaultGroupFirstKey = defaultGroupFirstKey;
    }

    public AddressSetting withDefaultGroupFirstKey(String defaultGroupFirstKey) {
        this.defaultGroupFirstKey = defaultGroupFirstKey;
        return this;
    }

    /**
     * the default number of consumers needed before dispatch can start for queues under the address.
     */
    @JsonProperty("defaultConsumersBeforeDispatch")
    public Integer getDefaultConsumersBeforeDispatch() {
        return defaultConsumersBeforeDispatch;
    }

    /**
     * the default number of consumers needed before dispatch can start for queues under the address.
     */
    @JsonProperty("defaultConsumersBeforeDispatch")
    public void setDefaultConsumersBeforeDispatch(Integer defaultConsumersBeforeDispatch) {
        this.defaultConsumersBeforeDispatch = defaultConsumersBeforeDispatch;
    }

    public AddressSetting withDefaultConsumersBeforeDispatch(Integer defaultConsumersBeforeDispatch) {
        this.defaultConsumersBeforeDispatch = defaultConsumersBeforeDispatch;
        return this;
    }

    /**
     * the default delay (in milliseconds) to wait before dispatching if number of consumers before dispatch is not met for queues under the address.
     */
    @JsonProperty("defaultDelayBeforeDispatch")
    public Integer getDefaultDelayBeforeDispatch() {
        return defaultDelayBeforeDispatch;
    }

    /**
     * the default delay (in milliseconds) to wait before dispatching if number of consumers before dispatch is not met for queues under the address.
     */
    @JsonProperty("defaultDelayBeforeDispatch")
    public void setDefaultDelayBeforeDispatch(Integer defaultDelayBeforeDispatch) {
        this.defaultDelayBeforeDispatch = defaultDelayBeforeDispatch;
    }

    public AddressSetting withDefaultDelayBeforeDispatch(Integer defaultDelayBeforeDispatch) {
        this.defaultDelayBeforeDispatch = defaultDelayBeforeDispatch;
        return this;
    }

    /**
     * how long (in ms) to wait after the last consumer is closed on a queue before redistributing messages.
     */
    @JsonProperty("redistributionDelay")
    public Integer getRedistributionDelay() {
        return redistributionDelay;
    }

    /**
     * how long (in ms) to wait after the last consumer is closed on a queue before redistributing messages.
     */
    @JsonProperty("redistributionDelay")
    public void setRedistributionDelay(Integer redistributionDelay) {
        this.redistributionDelay = redistributionDelay;
    }

    public AddressSetting withRedistributionDelay(Integer redistributionDelay) {
        this.redistributionDelay = redistributionDelay;
        return this;
    }

    /**
     * if there are no queues matching this address, whether to forward message to DLA (if it exists for this address)
     */
    @JsonProperty("sendToDlaOnNoRoute")
    public Boolean getSendToDlaOnNoRoute() {
        return sendToDlaOnNoRoute;
    }

    /**
     * if there are no queues matching this address, whether to forward message to DLA (if it exists for this address)
     */
    @JsonProperty("sendToDlaOnNoRoute")
    public void setSendToDlaOnNoRoute(Boolean sendToDlaOnNoRoute) {
        this.sendToDlaOnNoRoute = sendToDlaOnNoRoute;
    }

    public AddressSetting withSendToDlaOnNoRoute(Boolean sendToDlaOnNoRoute) {
        this.sendToDlaOnNoRoute = sendToDlaOnNoRoute;
        return this;
    }

    /**
     * The minimum rate of message consumption allowed before a consumer is considered "slow." Measured in messages-per-second.
     */
    @JsonProperty("slowConsumerThreshold")
    public Integer getSlowConsumerThreshold() {
        return slowConsumerThreshold;
    }

    /**
     * The minimum rate of message consumption allowed before a consumer is considered "slow." Measured in messages-per-second.
     */
    @JsonProperty("slowConsumerThreshold")
    public void setSlowConsumerThreshold(Integer slowConsumerThreshold) {
        this.slowConsumerThreshold = slowConsumerThreshold;
    }

    public AddressSetting withSlowConsumerThreshold(Integer slowConsumerThreshold) {
        this.slowConsumerThreshold = slowConsumerThreshold;
        return this;
    }

    /**
     * what happens when a slow consumer is identified
     */
    @JsonProperty("slowConsumerPolicy")
    public AddressSetting.SlowConsumerPolicy getSlowConsumerPolicy() {
        return slowConsumerPolicy;
    }

    /**
     * what happens when a slow consumer is identified
     */
    @JsonProperty("slowConsumerPolicy")
    public void setSlowConsumerPolicy(AddressSetting.SlowConsumerPolicy slowConsumerPolicy) {
        this.slowConsumerPolicy = slowConsumerPolicy;
    }

    public AddressSetting withSlowConsumerPolicy(AddressSetting.SlowConsumerPolicy slowConsumerPolicy) {
        this.slowConsumerPolicy = slowConsumerPolicy;
        return this;
    }

    /**
     * How often to check for slow consumers on a particular queue. Measured in seconds.
     */
    @JsonProperty("slowConsumerCheckPeriod")
    public Integer getSlowConsumerCheckPeriod() {
        return slowConsumerCheckPeriod;
    }

    /**
     * How often to check for slow consumers on a particular queue. Measured in seconds.
     */
    @JsonProperty("slowConsumerCheckPeriod")
    public void setSlowConsumerCheckPeriod(Integer slowConsumerCheckPeriod) {
        this.slowConsumerCheckPeriod = slowConsumerCheckPeriod;
    }

    public AddressSetting withSlowConsumerCheckPeriod(Integer slowConsumerCheckPeriod) {
        this.slowConsumerCheckPeriod = slowConsumerCheckPeriod;
        return this;
    }

    /**
     * DEPRECATED. whether or not to automatically create JMS queues when a producer sends or a consumer connects to a queue
     */
    @JsonProperty("autoCreateJmsQueues")
    public Boolean getAutoCreateJmsQueues() {
        return autoCreateJmsQueues;
    }

    /**
     * DEPRECATED. whether or not to automatically create JMS queues when a producer sends or a consumer connects to a queue
     */
    @JsonProperty("autoCreateJmsQueues")
    public void setAutoCreateJmsQueues(Boolean autoCreateJmsQueues) {
        this.autoCreateJmsQueues = autoCreateJmsQueues;
    }

    public AddressSetting withAutoCreateJmsQueues(Boolean autoCreateJmsQueues) {
        this.autoCreateJmsQueues = autoCreateJmsQueues;
        return this;
    }

    /**
     * DEPRECATED. whether or not to delete auto-created JMS queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteJmsQueues")
    public Boolean getAutoDeleteJmsQueues() {
        return autoDeleteJmsQueues;
    }

    /**
     * DEPRECATED. whether or not to delete auto-created JMS queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteJmsQueues")
    public void setAutoDeleteJmsQueues(Boolean autoDeleteJmsQueues) {
        this.autoDeleteJmsQueues = autoDeleteJmsQueues;
    }

    public AddressSetting withAutoDeleteJmsQueues(Boolean autoDeleteJmsQueues) {
        this.autoDeleteJmsQueues = autoDeleteJmsQueues;
        return this;
    }

    /**
     * DEPRECATED. whether or not to automatically create JMS topics when a producer sends or a consumer subscribes to a topic
     */
    @JsonProperty("autoCreateJmsTopics")
    public Boolean getAutoCreateJmsTopics() {
        return autoCreateJmsTopics;
    }

    /**
     * DEPRECATED. whether or not to automatically create JMS topics when a producer sends or a consumer subscribes to a topic
     */
    @JsonProperty("autoCreateJmsTopics")
    public void setAutoCreateJmsTopics(Boolean autoCreateJmsTopics) {
        this.autoCreateJmsTopics = autoCreateJmsTopics;
    }

    public AddressSetting withAutoCreateJmsTopics(Boolean autoCreateJmsTopics) {
        this.autoCreateJmsTopics = autoCreateJmsTopics;
        return this;
    }

    /**
     * DEPRECATED. whether or not to delete auto-created JMS topics when the last subscription is closed
     */
    @JsonProperty("autoDeleteJmsTopics")
    public Boolean getAutoDeleteJmsTopics() {
        return autoDeleteJmsTopics;
    }

    /**
     * DEPRECATED. whether or not to delete auto-created JMS topics when the last subscription is closed
     */
    @JsonProperty("autoDeleteJmsTopics")
    public void setAutoDeleteJmsTopics(Boolean autoDeleteJmsTopics) {
        this.autoDeleteJmsTopics = autoDeleteJmsTopics;
    }

    public AddressSetting withAutoDeleteJmsTopics(Boolean autoDeleteJmsTopics) {
        this.autoDeleteJmsTopics = autoDeleteJmsTopics;
        return this;
    }

    /**
     * whether or not to automatically create a queue when a client sends a message to or attempts to consume a message from a queue
     */
    @JsonProperty("autoCreateQueues")
    public Boolean getAutoCreateQueues() {
        return autoCreateQueues;
    }

    /**
     * whether or not to automatically create a queue when a client sends a message to or attempts to consume a message from a queue
     */
    @JsonProperty("autoCreateQueues")
    public void setAutoCreateQueues(Boolean autoCreateQueues) {
        this.autoCreateQueues = autoCreateQueues;
    }

    public AddressSetting withAutoCreateQueues(Boolean autoCreateQueues) {
        this.autoCreateQueues = autoCreateQueues;
        return this;
    }

    /**
     * whether or not to delete auto-created queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteQueues")
    public Boolean getAutoDeleteQueues() {
        return autoDeleteQueues;
    }

    /**
     * whether or not to delete auto-created queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteQueues")
    public void setAutoDeleteQueues(Boolean autoDeleteQueues) {
        this.autoDeleteQueues = autoDeleteQueues;
    }

    public AddressSetting withAutoDeleteQueues(Boolean autoDeleteQueues) {
        this.autoDeleteQueues = autoDeleteQueues;
        return this;
    }

    /**
     * whether or not to delete created queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteCreatedQueues")
    public Boolean getAutoDeleteCreatedQueues() {
        return autoDeleteCreatedQueues;
    }

    /**
     * whether or not to delete created queues when the queue has 0 consumers and 0 messages
     */
    @JsonProperty("autoDeleteCreatedQueues")
    public void setAutoDeleteCreatedQueues(Boolean autoDeleteCreatedQueues) {
        this.autoDeleteCreatedQueues = autoDeleteCreatedQueues;
    }

    public AddressSetting withAutoDeleteCreatedQueues(Boolean autoDeleteCreatedQueues) {
        this.autoDeleteCreatedQueues = autoDeleteCreatedQueues;
        return this;
    }

    /**
     * how long to wait (in milliseconds) before deleting auto-created queues after the queue has 0 consumers.
     */
    @JsonProperty("autoDeleteQueuesDelay")
    public Integer getAutoDeleteQueuesDelay() {
        return autoDeleteQueuesDelay;
    }

    /**
     * how long to wait (in milliseconds) before deleting auto-created queues after the queue has 0 consumers.
     */
    @JsonProperty("autoDeleteQueuesDelay")
    public void setAutoDeleteQueuesDelay(Integer autoDeleteQueuesDelay) {
        this.autoDeleteQueuesDelay = autoDeleteQueuesDelay;
    }

    public AddressSetting withAutoDeleteQueuesDelay(Integer autoDeleteQueuesDelay) {
        this.autoDeleteQueuesDelay = autoDeleteQueuesDelay;
        return this;
    }

    /**
     * the message count the queue must be at or below before it can be evaluated  to be auto deleted, 0 waits until empty queue (default) and -1
     * disables this check.
     */
    @JsonProperty("autoDeleteQueuesMessageCount")
    public Integer getAutoDeleteQueuesMessageCount() {
        return autoDeleteQueuesMessageCount;
    }

    /**
     * the message count the queue must be at or below before it can be evaluated  to be auto deleted, 0 waits until empty queue (default) and -1
     * disables this check.
     */
    @JsonProperty("autoDeleteQueuesMessageCount")
    public void setAutoDeleteQueuesMessageCount(Integer autoDeleteQueuesMessageCount) {
        this.autoDeleteQueuesMessageCount = autoDeleteQueuesMessageCount;
    }

    public AddressSetting withAutoDeleteQueuesMessageCount(Integer autoDeleteQueuesMessageCount) {
        this.autoDeleteQueuesMessageCount = autoDeleteQueuesMessageCount;
        return this;
    }

    /**
     * What to do when a queue is no longer in broker.xml. OFF = will do nothing queues will remain, FORCE = delete queues even if messages remaining.
     */
    @JsonProperty("configDeleteQueues")
    public AddressSetting.ConfigDeleteQueues getConfigDeleteQueues() {
        return configDeleteQueues;
    }

    /**
     * What to do when a queue is no longer in broker.xml. OFF = will do nothing queues will remain, FORCE = delete queues even if messages remaining.
     */
    @JsonProperty("configDeleteQueues")
    public void setConfigDeleteQueues(AddressSetting.ConfigDeleteQueues configDeleteQueues) {
        this.configDeleteQueues = configDeleteQueues;
    }

    public AddressSetting withConfigDeleteQueues(AddressSetting.ConfigDeleteQueues configDeleteQueues) {
        this.configDeleteQueues = configDeleteQueues;
        return this;
    }

    /**
     * whether or not to automatically create addresses when a client sends a message to or attempts to consume a message from a queue mapped to an
     * address that doesnt exist
     */
    @JsonProperty("autoCreateAddresses")
    public Boolean getAutoCreateAddresses() {
        return autoCreateAddresses;
    }

    /**
     * whether or not to automatically create addresses when a client sends a message to or attempts to consume a message from a queue mapped to an
     * address that doesnt exist
     */
    @JsonProperty("autoCreateAddresses")
    public void setAutoCreateAddresses(Boolean autoCreateAddresses) {
        this.autoCreateAddresses = autoCreateAddresses;
    }

    public AddressSetting withAutoCreateAddresses(Boolean autoCreateAddresses) {
        this.autoCreateAddresses = autoCreateAddresses;
        return this;
    }

    /**
     * whether or not to delete auto-created addresses when it no longer has any queues
     */
    @JsonProperty("autoDeleteAddresses")
    public Boolean getAutoDeleteAddresses() {
        return autoDeleteAddresses;
    }

    /**
     * whether or not to delete auto-created addresses when it no longer has any queues
     */
    @JsonProperty("autoDeleteAddresses")
    public void setAutoDeleteAddresses(Boolean autoDeleteAddresses) {
        this.autoDeleteAddresses = autoDeleteAddresses;
    }

    public AddressSetting withAutoDeleteAddresses(Boolean autoDeleteAddresses) {
        this.autoDeleteAddresses = autoDeleteAddresses;
        return this;
    }

    /**
     * how long to wait (in milliseconds) before deleting auto-created addresses after they no longer have any queues
     */
    @JsonProperty("autoDeleteAddressesDelay")
    public Integer getAutoDeleteAddressesDelay() {
        return autoDeleteAddressesDelay;
    }

    /**
     * how long to wait (in milliseconds) before deleting auto-created addresses after they no longer have any queues
     */
    @JsonProperty("autoDeleteAddressesDelay")
    public void setAutoDeleteAddressesDelay(Integer autoDeleteAddressesDelay) {
        this.autoDeleteAddressesDelay = autoDeleteAddressesDelay;
    }

    public AddressSetting withAutoDeleteAddressesDelay(Integer autoDeleteAddressesDelay) {
        this.autoDeleteAddressesDelay = autoDeleteAddressesDelay;
        return this;
    }

    /**
     * What to do when an address is no longer in broker.xml. OFF = will do nothing addresses will remain, FORCE = delete address and its queues
     * even if messages remaining.
     */
    @JsonProperty("configDeleteAddresses")
    public AddressSetting.ConfigDeleteAddresses getConfigDeleteAddresses() {
        return configDeleteAddresses;
    }

    /**
     * What to do when an address is no longer in broker.xml. OFF = will do nothing addresses will remain, FORCE = delete address and its queues
     * even if messages remaining.
     */
    @JsonProperty("configDeleteAddresses")
    public void setConfigDeleteAddresses(AddressSetting.ConfigDeleteAddresses configDeleteAddresses) {
        this.configDeleteAddresses = configDeleteAddresses;
    }

    public AddressSetting withConfigDeleteAddresses(AddressSetting.ConfigDeleteAddresses configDeleteAddresses) {
        this.configDeleteAddresses = configDeleteAddresses;
        return this;
    }

    /**
     * how many message a management resource can browse
     */
    @JsonProperty("managementBrowsePageSize")
    public Integer getManagementBrowsePageSize() {
        return managementBrowsePageSize;
    }

    /**
     * how many message a management resource can browse
     */
    @JsonProperty("managementBrowsePageSize")
    public void setManagementBrowsePageSize(Integer managementBrowsePageSize) {
        this.managementBrowsePageSize = managementBrowsePageSize;
    }

    public AddressSetting withManagementBrowsePageSize(Integer managementBrowsePageSize) {
        this.managementBrowsePageSize = managementBrowsePageSize;
        return this;
    }

    /**
     * purge the contents of the queue once there are no consumers
     */
    @JsonProperty("defaultPurgeOnNoConsumers")
    public Boolean getDefaultPurgeOnNoConsumers() {
        return defaultPurgeOnNoConsumers;
    }

    /**
     * purge the contents of the queue once there are no consumers
     */
    @JsonProperty("defaultPurgeOnNoConsumers")
    public void setDefaultPurgeOnNoConsumers(Boolean defaultPurgeOnNoConsumers) {
        this.defaultPurgeOnNoConsumers = defaultPurgeOnNoConsumers;
    }

    public AddressSetting withDefaultPurgeOnNoConsumers(Boolean defaultPurgeOnNoConsumers) {
        this.defaultPurgeOnNoConsumers = defaultPurgeOnNoConsumers;
        return this;
    }

    /**
     * the maximum number of consumers allowed on this queue at any one time
     */
    @JsonProperty("defaultMaxConsumers")
    public Integer getDefaultMaxConsumers() {
        return defaultMaxConsumers;
    }

    /**
     * the maximum number of consumers allowed on this queue at any one time
     */
    @JsonProperty("defaultMaxConsumers")
    public void setDefaultMaxConsumers(Integer defaultMaxConsumers) {
        this.defaultMaxConsumers = defaultMaxConsumers;
    }

    public AddressSetting withDefaultMaxConsumers(Integer defaultMaxConsumers) {
        this.defaultMaxConsumers = defaultMaxConsumers;
        return this;
    }

    /**
     * the routing-type used on auto-created queues
     */
    @JsonProperty("defaultQueueRoutingType")
    public AddressSetting.DefaultQueueRoutingType getDefaultQueueRoutingType() {
        return defaultQueueRoutingType;
    }

    /**
     * the routing-type used on auto-created queues
     */
    @JsonProperty("defaultQueueRoutingType")
    public void setDefaultQueueRoutingType(AddressSetting.DefaultQueueRoutingType defaultQueueRoutingType) {
        this.defaultQueueRoutingType = defaultQueueRoutingType;
    }

    public AddressSetting withDefaultQueueRoutingType(AddressSetting.DefaultQueueRoutingType defaultQueueRoutingType) {
        this.defaultQueueRoutingType = defaultQueueRoutingType;
        return this;
    }

    /**
     * the routing-type used on auto-created addresses
     */
    @JsonProperty("defaultAddressRoutingType")
    public AddressSetting.DefaultAddressRoutingType getDefaultAddressRoutingType() {
        return defaultAddressRoutingType;
    }

    /**
     * the routing-type used on auto-created addresses
     */
    @JsonProperty("defaultAddressRoutingType")
    public void setDefaultAddressRoutingType(AddressSetting.DefaultAddressRoutingType defaultAddressRoutingType) {
        this.defaultAddressRoutingType = defaultAddressRoutingType;
    }

    public AddressSetting withDefaultAddressRoutingType(AddressSetting.DefaultAddressRoutingType defaultAddressRoutingType) {
        this.defaultAddressRoutingType = defaultAddressRoutingType;
        return this;
    }

    /**
     * the default window size for a consumer
     */
    @JsonProperty("defaultConsumerWindowSize")
    public Integer getDefaultConsumerWindowSize() {
        return defaultConsumerWindowSize;
    }

    /**
     * the default window size for a consumer
     */
    @JsonProperty("defaultConsumerWindowSize")
    public void setDefaultConsumerWindowSize(Integer defaultConsumerWindowSize) {
        this.defaultConsumerWindowSize = defaultConsumerWindowSize;
    }

    public AddressSetting withDefaultConsumerWindowSize(Integer defaultConsumerWindowSize) {
        this.defaultConsumerWindowSize = defaultConsumerWindowSize;
        return this;
    }

    /**
     * the default ring-size value for any matching queue which doesnt have ring-size explicitly defined
     */
    @JsonProperty("defaultRingSize")
    public Integer getDefaultRingSize() {
        return defaultRingSize;
    }

    /**
     * the default ring-size value for any matching queue which doesnt have ring-size explicitly defined
     */
    @JsonProperty("defaultRingSize")
    public void setDefaultRingSize(Integer defaultRingSize) {
        this.defaultRingSize = defaultRingSize;
    }

    public AddressSetting withDefaultRingSize(Integer defaultRingSize) {
        this.defaultRingSize = defaultRingSize;
        return this;
    }

    /**
     * the number of messages to preserve for future queues created on the matching address
     */
    @JsonProperty("retroactiveMessageCount")
    public Integer getRetroactiveMessageCount() {
        return retroactiveMessageCount;
    }

    /**
     * the number of messages to preserve for future queues created on the matching address
     */
    @JsonProperty("retroactiveMessageCount")
    public void setRetroactiveMessageCount(Integer retroactiveMessageCount) {
        this.retroactiveMessageCount = retroactiveMessageCount;
    }

    public AddressSetting withRetroactiveMessageCount(Integer retroactiveMessageCount) {
        this.retroactiveMessageCount = retroactiveMessageCount;
        return this;
    }

    /**
     * whether or not to enable metrics for metrics plugins on the matching address
     */
    @JsonProperty("enableMetrics")
    public Boolean getEnableMetrics() {
        return enableMetrics;
    }

    /**
     * whether or not to enable metrics for metrics plugins on the matching address
     */
    @JsonProperty("enableMetrics")
    public void setEnableMetrics(Boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
    }

    public AddressSetting withEnableMetrics(Boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
        return this;
    }

    /**
     * pattern for matching settings against addresses; can use wildards
     */
    @JsonProperty("match")
    public String getMatch() {
        return match;
    }

    /**
     * pattern for matching settings against addresses; can use wildards
     */
    @JsonProperty("match")
    public void setMatch(String match) {
        this.match = match;
    }

    public AddressSetting withMatch(String match) {
        this.match = match;
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

    public AddressSetting withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AddressSetting.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("deadLetterAddress");
        sb.append('=');
        sb.append(((this.deadLetterAddress == null) ? "<null>" : this.deadLetterAddress));
        sb.append(',');
        sb.append("autoCreateDeadLetterResources");
        sb.append('=');
        sb.append(((this.autoCreateDeadLetterResources == null) ? "<null>" : this.autoCreateDeadLetterResources));
        sb.append(',');
        sb.append("deadLetterQueuePrefix");
        sb.append('=');
        sb.append(((this.deadLetterQueuePrefix == null) ? "<null>" : this.deadLetterQueuePrefix));
        sb.append(',');
        sb.append("deadLetterQueueSuffix");
        sb.append('=');
        sb.append(((this.deadLetterQueueSuffix == null) ? "<null>" : this.deadLetterQueueSuffix));
        sb.append(',');
        sb.append("expiryAddress");
        sb.append('=');
        sb.append(((this.expiryAddress == null) ? "<null>" : this.expiryAddress));
        sb.append(',');
        sb.append("autoCreateExpiryResources");
        sb.append('=');
        sb.append(((this.autoCreateExpiryResources == null) ? "<null>" : this.autoCreateExpiryResources));
        sb.append(',');
        sb.append("expiryQueuePrefix");
        sb.append('=');
        sb.append(((this.expiryQueuePrefix == null) ? "<null>" : this.expiryQueuePrefix));
        sb.append(',');
        sb.append("expiryQueueSuffix");
        sb.append('=');
        sb.append(((this.expiryQueueSuffix == null) ? "<null>" : this.expiryQueueSuffix));
        sb.append(',');
        sb.append("expiryDelay");
        sb.append('=');
        sb.append(((this.expiryDelay == null) ? "<null>" : this.expiryDelay));
        sb.append(',');
        sb.append("minExpiryDelay");
        sb.append('=');
        sb.append(((this.minExpiryDelay == null) ? "<null>" : this.minExpiryDelay));
        sb.append(',');
        sb.append("maxExpiryDelay");
        sb.append('=');
        sb.append(((this.maxExpiryDelay == null) ? "<null>" : this.maxExpiryDelay));
        sb.append(',');
        sb.append("redeliveryDelay");
        sb.append('=');
        sb.append(((this.redeliveryDelay == null) ? "<null>" : this.redeliveryDelay));
        sb.append(',');
        sb.append("redeliveryDelayMultiplier");
        sb.append('=');
        sb.append(((this.redeliveryDelayMultiplier == null) ? "<null>" : this.redeliveryDelayMultiplier));
        sb.append(',');
        sb.append("redeliveryCollisionAvoidanceFactor");
        sb.append('=');
        sb.append(((this.redeliveryCollisionAvoidanceFactor == null) ? "<null>" : this.redeliveryCollisionAvoidanceFactor));
        sb.append(',');
        sb.append("maxRedeliveryDelay");
        sb.append('=');
        sb.append(((this.maxRedeliveryDelay == null) ? "<null>" : this.maxRedeliveryDelay));
        sb.append(',');
        sb.append("maxDeliveryAttempts");
        sb.append('=');
        sb.append(((this.maxDeliveryAttempts == null) ? "<null>" : this.maxDeliveryAttempts));
        sb.append(',');
        sb.append("maxSizeBytes");
        sb.append('=');
        sb.append(((this.maxSizeBytes == null) ? "<null>" : this.maxSizeBytes));
        sb.append(',');
        sb.append("maxSizeBytesRejectThreshold");
        sb.append('=');
        sb.append(((this.maxSizeBytesRejectThreshold == null) ? "<null>" : this.maxSizeBytesRejectThreshold));
        sb.append(',');
        sb.append("pageSizeBytes");
        sb.append('=');
        sb.append(((this.pageSizeBytes == null) ? "<null>" : this.pageSizeBytes));
        sb.append(',');
        sb.append("pageMaxCacheSize");
        sb.append('=');
        sb.append(((this.pageMaxCacheSize == null) ? "<null>" : this.pageMaxCacheSize));
        sb.append(',');
        sb.append("addressFullPolicy");
        sb.append('=');
        sb.append(((this.addressFullPolicy == null) ? "<null>" : this.addressFullPolicy));
        sb.append(',');
        sb.append("messageCounterHistoryDayLimit");
        sb.append('=');
        sb.append(((this.messageCounterHistoryDayLimit == null) ? "<null>" : this.messageCounterHistoryDayLimit));
        sb.append(',');
        sb.append("lastValueQueue");
        sb.append('=');
        sb.append(((this.lastValueQueue == null) ? "<null>" : this.lastValueQueue));
        sb.append(',');
        sb.append("defaultLastValueQueue");
        sb.append('=');
        sb.append(((this.defaultLastValueQueue == null) ? "<null>" : this.defaultLastValueQueue));
        sb.append(',');
        sb.append("defaultLastValueKey");
        sb.append('=');
        sb.append(((this.defaultLastValueKey == null) ? "<null>" : this.defaultLastValueKey));
        sb.append(',');
        sb.append("defaultNonDestructive");
        sb.append('=');
        sb.append(((this.defaultNonDestructive == null) ? "<null>" : this.defaultNonDestructive));
        sb.append(',');
        sb.append("defaultExclusiveQueue");
        sb.append('=');
        sb.append(((this.defaultExclusiveQueue == null) ? "<null>" : this.defaultExclusiveQueue));
        sb.append(',');
        sb.append("defaultGroupRebalance");
        sb.append('=');
        sb.append(((this.defaultGroupRebalance == null) ? "<null>" : this.defaultGroupRebalance));
        sb.append(',');
        sb.append("defaultGroupRebalancePauseDispatch");
        sb.append('=');
        sb.append(((this.defaultGroupRebalancePauseDispatch == null) ? "<null>" : this.defaultGroupRebalancePauseDispatch));
        sb.append(',');
        sb.append("defaultGroupBuckets");
        sb.append('=');
        sb.append(((this.defaultGroupBuckets == null) ? "<null>" : this.defaultGroupBuckets));
        sb.append(',');
        sb.append("defaultGroupFirstKey");
        sb.append('=');
        sb.append(((this.defaultGroupFirstKey == null) ? "<null>" : this.defaultGroupFirstKey));
        sb.append(',');
        sb.append("defaultConsumersBeforeDispatch");
        sb.append('=');
        sb.append(((this.defaultConsumersBeforeDispatch == null) ? "<null>" : this.defaultConsumersBeforeDispatch));
        sb.append(',');
        sb.append("defaultDelayBeforeDispatch");
        sb.append('=');
        sb.append(((this.defaultDelayBeforeDispatch == null) ? "<null>" : this.defaultDelayBeforeDispatch));
        sb.append(',');
        sb.append("redistributionDelay");
        sb.append('=');
        sb.append(((this.redistributionDelay == null) ? "<null>" : this.redistributionDelay));
        sb.append(',');
        sb.append("sendToDlaOnNoRoute");
        sb.append('=');
        sb.append(((this.sendToDlaOnNoRoute == null) ? "<null>" : this.sendToDlaOnNoRoute));
        sb.append(',');
        sb.append("slowConsumerThreshold");
        sb.append('=');
        sb.append(((this.slowConsumerThreshold == null) ? "<null>" : this.slowConsumerThreshold));
        sb.append(',');
        sb.append("slowConsumerPolicy");
        sb.append('=');
        sb.append(((this.slowConsumerPolicy == null) ? "<null>" : this.slowConsumerPolicy));
        sb.append(',');
        sb.append("slowConsumerCheckPeriod");
        sb.append('=');
        sb.append(((this.slowConsumerCheckPeriod == null) ? "<null>" : this.slowConsumerCheckPeriod));
        sb.append(',');
        sb.append("autoCreateJmsQueues");
        sb.append('=');
        sb.append(((this.autoCreateJmsQueues == null) ? "<null>" : this.autoCreateJmsQueues));
        sb.append(',');
        sb.append("autoDeleteJmsQueues");
        sb.append('=');
        sb.append(((this.autoDeleteJmsQueues == null) ? "<null>" : this.autoDeleteJmsQueues));
        sb.append(',');
        sb.append("autoCreateJmsTopics");
        sb.append('=');
        sb.append(((this.autoCreateJmsTopics == null) ? "<null>" : this.autoCreateJmsTopics));
        sb.append(',');
        sb.append("autoDeleteJmsTopics");
        sb.append('=');
        sb.append(((this.autoDeleteJmsTopics == null) ? "<null>" : this.autoDeleteJmsTopics));
        sb.append(',');
        sb.append("autoCreateQueues");
        sb.append('=');
        sb.append(((this.autoCreateQueues == null) ? "<null>" : this.autoCreateQueues));
        sb.append(',');
        sb.append("autoDeleteQueues");
        sb.append('=');
        sb.append(((this.autoDeleteQueues == null) ? "<null>" : this.autoDeleteQueues));
        sb.append(',');
        sb.append("autoDeleteCreatedQueues");
        sb.append('=');
        sb.append(((this.autoDeleteCreatedQueues == null) ? "<null>" : this.autoDeleteCreatedQueues));
        sb.append(',');
        sb.append("autoDeleteQueuesDelay");
        sb.append('=');
        sb.append(((this.autoDeleteQueuesDelay == null) ? "<null>" : this.autoDeleteQueuesDelay));
        sb.append(',');
        sb.append("autoDeleteQueuesMessageCount");
        sb.append('=');
        sb.append(((this.autoDeleteQueuesMessageCount == null) ? "<null>" : this.autoDeleteQueuesMessageCount));
        sb.append(',');
        sb.append("configDeleteQueues");
        sb.append('=');
        sb.append(((this.configDeleteQueues == null) ? "<null>" : this.configDeleteQueues));
        sb.append(',');
        sb.append("autoCreateAddresses");
        sb.append('=');
        sb.append(((this.autoCreateAddresses == null) ? "<null>" : this.autoCreateAddresses));
        sb.append(',');
        sb.append("autoDeleteAddresses");
        sb.append('=');
        sb.append(((this.autoDeleteAddresses == null) ? "<null>" : this.autoDeleteAddresses));
        sb.append(',');
        sb.append("autoDeleteAddressesDelay");
        sb.append('=');
        sb.append(((this.autoDeleteAddressesDelay == null) ? "<null>" : this.autoDeleteAddressesDelay));
        sb.append(',');
        sb.append("configDeleteAddresses");
        sb.append('=');
        sb.append(((this.configDeleteAddresses == null) ? "<null>" : this.configDeleteAddresses));
        sb.append(',');
        sb.append("managementBrowsePageSize");
        sb.append('=');
        sb.append(((this.managementBrowsePageSize == null) ? "<null>" : this.managementBrowsePageSize));
        sb.append(',');
        sb.append("defaultPurgeOnNoConsumers");
        sb.append('=');
        sb.append(((this.defaultPurgeOnNoConsumers == null) ? "<null>" : this.defaultPurgeOnNoConsumers));
        sb.append(',');
        sb.append("defaultMaxConsumers");
        sb.append('=');
        sb.append(((this.defaultMaxConsumers == null) ? "<null>" : this.defaultMaxConsumers));
        sb.append(',');
        sb.append("defaultQueueRoutingType");
        sb.append('=');
        sb.append(((this.defaultQueueRoutingType == null) ? "<null>" : this.defaultQueueRoutingType));
        sb.append(',');
        sb.append("defaultAddressRoutingType");
        sb.append('=');
        sb.append(((this.defaultAddressRoutingType == null) ? "<null>" : this.defaultAddressRoutingType));
        sb.append(',');
        sb.append("defaultConsumerWindowSize");
        sb.append('=');
        sb.append(((this.defaultConsumerWindowSize == null) ? "<null>" : this.defaultConsumerWindowSize));
        sb.append(',');
        sb.append("defaultRingSize");
        sb.append('=');
        sb.append(((this.defaultRingSize == null) ? "<null>" : this.defaultRingSize));
        sb.append(',');
        sb.append("retroactiveMessageCount");
        sb.append('=');
        sb.append(((this.retroactiveMessageCount == null) ? "<null>" : this.retroactiveMessageCount));
        sb.append(',');
        sb.append("enableMetrics");
        sb.append('=');
        sb.append(((this.enableMetrics == null) ? "<null>" : this.enableMetrics));
        sb.append(',');
        sb.append("match");
        sb.append('=');
        sb.append(((this.match == null) ? "<null>" : this.match));
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
        result = ((result * 31) + ((this.expiryQueuePrefix == null) ? 0 : this.expiryQueuePrefix.hashCode()));
        result = ((result * 31) + ((this.defaultConsumerWindowSize == null) ? 0 : this.defaultConsumerWindowSize.hashCode()));
        result = ((result * 31) + ((this.deadLetterQueuePrefix == null) ? 0 : this.deadLetterQueuePrefix.hashCode()));
        result = ((result * 31) + ((this.defaultGroupRebalancePauseDispatch == null) ? 0 : this.defaultGroupRebalancePauseDispatch.hashCode()));
        result = ((result * 31) + ((this.autoCreateAddresses == null) ? 0 : this.autoCreateAddresses.hashCode()));
        result = ((result * 31) + ((this.defaultGroupBuckets == null) ? 0 : this.defaultGroupBuckets.hashCode()));
        result = ((result * 31) + ((this.slowConsumerThreshold == null) ? 0 : this.slowConsumerThreshold.hashCode()));
        result = ((result * 31) + ((this.autoCreateExpiryResources == null) ? 0 : this.autoCreateExpiryResources.hashCode()));
        result = ((result * 31) + ((this.pageSizeBytes == null) ? 0 : this.pageSizeBytes.hashCode()));
        result = ((result * 31) + ((this.minExpiryDelay == null) ? 0 : this.minExpiryDelay.hashCode()));
        result = ((result * 31) + ((this.expiryQueueSuffix == null) ? 0 : this.expiryQueueSuffix.hashCode()));
        result = ((result * 31) + ((this.pageMaxCacheSize == null) ? 0 : this.pageMaxCacheSize.hashCode()));
        result = ((result * 31) + ((this.defaultConsumersBeforeDispatch == null) ? 0 : this.defaultConsumersBeforeDispatch.hashCode()));
        result = ((result * 31) + ((this.configDeleteQueues == null) ? 0 : this.configDeleteQueues.hashCode()));
        result = ((result * 31) + ((this.expiryAddress == null) ? 0 : this.expiryAddress.hashCode()));
        result = ((result * 31) + ((this.autoDeleteCreatedQueues == null) ? 0 : this.autoDeleteCreatedQueues.hashCode()));
        result = ((result * 31) + ((this.managementBrowsePageSize == null) ? 0 : this.managementBrowsePageSize.hashCode()));
        result = ((result * 31) + ((this.autoDeleteQueues == null) ? 0 : this.autoDeleteQueues.hashCode()));
        result = ((result * 31) + ((this.retroactiveMessageCount == null) ? 0 : this.retroactiveMessageCount.hashCode()));
        result = ((result * 31) + ((this.maxExpiryDelay == null) ? 0 : this.maxExpiryDelay.hashCode()));
        result = ((result * 31) + ((this.lastValueQueue == null) ? 0 : this.lastValueQueue.hashCode()));
        result = ((result * 31) + ((this.maxDeliveryAttempts == null) ? 0 : this.maxDeliveryAttempts.hashCode()));
        result = ((result * 31) + ((this.defaultGroupFirstKey == null) ? 0 : this.defaultGroupFirstKey.hashCode()));
        result = ((result * 31) + ((this.autoDeleteJmsQueues == null) ? 0 : this.autoDeleteJmsQueues.hashCode()));
        result = ((result * 31) + ((this.slowConsumerCheckPeriod == null) ? 0 : this.slowConsumerCheckPeriod.hashCode()));
        result = ((result * 31) + ((this.defaultPurgeOnNoConsumers == null) ? 0 : this.defaultPurgeOnNoConsumers.hashCode()));
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.defaultLastValueKey == null) ? 0 : this.defaultLastValueKey.hashCode()));
        result = ((result * 31) + ((this.autoCreateQueues == null) ? 0 : this.autoCreateQueues.hashCode()));
        result = ((result * 31) + ((this.defaultExclusiveQueue == null) ? 0 : this.defaultExclusiveQueue.hashCode()));
        result = ((result * 31) + ((this.sendToDlaOnNoRoute == null) ? 0 : this.sendToDlaOnNoRoute.hashCode()));
        result = ((result * 31) + ((this.defaultMaxConsumers == null) ? 0 : this.defaultMaxConsumers.hashCode()));
        result = ((result * 31) + ((this.defaultQueueRoutingType == null) ? 0 : this.defaultQueueRoutingType.hashCode()));
        result = ((result * 31) + ((this.messageCounterHistoryDayLimit == null) ? 0 : this.messageCounterHistoryDayLimit.hashCode()));
        result = ((result * 31) + ((this.defaultGroupRebalance == null) ? 0 : this.defaultGroupRebalance.hashCode()));
        result = ((result * 31) + ((this.maxSizeBytesRejectThreshold == null) ? 0 : this.maxSizeBytesRejectThreshold.hashCode()));
        result = ((result * 31) + ((this.defaultAddressRoutingType == null) ? 0 : this.defaultAddressRoutingType.hashCode()));
        result = ((result * 31) + ((this.autoCreateDeadLetterResources == null) ? 0 : this.autoCreateDeadLetterResources.hashCode()));
        result = ((result * 31) + ((this.autoCreateJmsQueues == null) ? 0 : this.autoCreateJmsQueues.hashCode()));
        result = ((result * 31) + ((this.autoDeleteJmsTopics == null) ? 0 : this.autoDeleteJmsTopics.hashCode()));
        result = ((result * 31) + ((this.maxRedeliveryDelay == null) ? 0 : this.maxRedeliveryDelay.hashCode()));
        result = ((result * 31) + ((this.deadLetterAddress == null) ? 0 : this.deadLetterAddress.hashCode()));
        result = ((result * 31) + ((this.configDeleteAddresses == null) ? 0 : this.configDeleteAddresses.hashCode()));
        result = ((result * 31) + ((this.autoDeleteQueuesMessageCount == null) ? 0 : this.autoDeleteQueuesMessageCount.hashCode()));
        result = ((result * 31) + ((this.autoDeleteAddresses == null) ? 0 : this.autoDeleteAddresses.hashCode()));
        result = ((result * 31) + ((this.maxSizeBytes == null) ? 0 : this.maxSizeBytes.hashCode()));
        result = ((result * 31) + ((this.defaultDelayBeforeDispatch == null) ? 0 : this.defaultDelayBeforeDispatch.hashCode()));
        result = ((result * 31) + ((this.redistributionDelay == null) ? 0 : this.redistributionDelay.hashCode()));
        result = ((result * 31) + ((this.defaultRingSize == null) ? 0 : this.defaultRingSize.hashCode()));
        result = ((result * 31) + ((this.match == null) ? 0 : this.match.hashCode()));
        result = ((result * 31) + ((this.defaultLastValueQueue == null) ? 0 : this.defaultLastValueQueue.hashCode()));
        result = ((result * 31) + ((this.slowConsumerPolicy == null) ? 0 : this.slowConsumerPolicy.hashCode()));
        result = ((result * 31) + ((this.redeliveryCollisionAvoidanceFactor == null) ? 0 : this.redeliveryCollisionAvoidanceFactor.hashCode()));
        result = ((result * 31) + ((this.autoDeleteQueuesDelay == null) ? 0 : this.autoDeleteQueuesDelay.hashCode()));
        result = ((result * 31) + ((this.autoDeleteAddressesDelay == null) ? 0 : this.autoDeleteAddressesDelay.hashCode()));
        result = ((result * 31) + ((this.addressFullPolicy == null) ? 0 : this.addressFullPolicy.hashCode()));
        result = ((result * 31) + ((this.expiryDelay == null) ? 0 : this.expiryDelay.hashCode()));
        result = ((result * 31) + ((this.enableMetrics == null) ? 0 : this.enableMetrics.hashCode()));
        result = ((result * 31) + ((this.redeliveryDelayMultiplier == null) ? 0 : this.redeliveryDelayMultiplier.hashCode()));
        result = ((result * 31) + ((this.autoCreateJmsTopics == null) ? 0 : this.autoCreateJmsTopics.hashCode()));
        result = ((result * 31) + ((this.redeliveryDelay == null) ? 0 : this.redeliveryDelay.hashCode()));
        result = ((result * 31) + ((this.deadLetterQueueSuffix == null) ? 0 : this.deadLetterQueueSuffix.hashCode()));
        result = ((result * 31) + ((this.defaultNonDestructive == null) ? 0 : this.defaultNonDestructive.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AddressSetting) == false) {
            return false;
        }
        AddressSetting rhs = ((AddressSetting) other);
        return (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((
            (((this.expiryQueuePrefix == rhs.expiryQueuePrefix) || ((this.expiryQueuePrefix != null) && this.expiryQueuePrefix
                .equals(rhs.expiryQueuePrefix))) && ((this.defaultConsumerWindowSize == rhs.defaultConsumerWindowSize) || (
                (this.defaultConsumerWindowSize != null) && this.defaultConsumerWindowSize.equals(rhs.defaultConsumerWindowSize)))) && (
                (this.deadLetterQueuePrefix == rhs.deadLetterQueuePrefix) || ((this.deadLetterQueuePrefix != null) && this.deadLetterQueuePrefix
                    .equals(rhs.deadLetterQueuePrefix)))) && ((this.defaultGroupRebalancePauseDispatch == rhs.defaultGroupRebalancePauseDispatch) || (
            (this.defaultGroupRebalancePauseDispatch != null) && this.defaultGroupRebalancePauseDispatch
                .equals(rhs.defaultGroupRebalancePauseDispatch)))) && ((this.autoCreateAddresses == rhs.autoCreateAddresses) || (
            (this.autoCreateAddresses != null) && this.autoCreateAddresses.equals(rhs.autoCreateAddresses)))) && (
            (this.defaultGroupBuckets == rhs.defaultGroupBuckets) || ((this.defaultGroupBuckets != null) && this.defaultGroupBuckets
                .equals(rhs.defaultGroupBuckets)))) && ((this.slowConsumerThreshold == rhs.slowConsumerThreshold) || (
            (this.slowConsumerThreshold != null) && this.slowConsumerThreshold.equals(rhs.slowConsumerThreshold)))) && (
            (this.autoCreateExpiryResources == rhs.autoCreateExpiryResources) || ((this.autoCreateExpiryResources != null)
                && this.autoCreateExpiryResources.equals(rhs.autoCreateExpiryResources)))) && ((this.pageSizeBytes == rhs.pageSizeBytes) || (
            (this.pageSizeBytes != null) && this.pageSizeBytes.equals(rhs.pageSizeBytes)))) && ((this.minExpiryDelay == rhs.minExpiryDelay) || (
            (this.minExpiryDelay != null) && this.minExpiryDelay.equals(rhs.minExpiryDelay)))) && ((this.expiryQueueSuffix == rhs.expiryQueueSuffix)
            || ((this.expiryQueueSuffix != null) && this.expiryQueueSuffix.equals(rhs.expiryQueueSuffix)))) && (
            (this.pageMaxCacheSize == rhs.pageMaxCacheSize) || ((this.pageMaxCacheSize != null) && this.pageMaxCacheSize
                .equals(rhs.pageMaxCacheSize)))) && ((this.defaultConsumersBeforeDispatch == rhs.defaultConsumersBeforeDispatch) || (
            (this.defaultConsumersBeforeDispatch != null) && this.defaultConsumersBeforeDispatch.equals(rhs.defaultConsumersBeforeDispatch)))) && ((
            this.configDeleteQueues == rhs.configDeleteQueues) || ((this.configDeleteQueues != null) && this.configDeleteQueues.equals(
            rhs.configDeleteQueues)))) && ((this.expiryAddress == rhs.expiryAddress) || ((this.expiryAddress != null) && this.expiryAddress
            .equals(rhs.expiryAddress)))) && ((this.autoDeleteCreatedQueues == rhs.autoDeleteCreatedQueues) || ((this.autoDeleteCreatedQueues != null)
            && this.autoDeleteCreatedQueues.equals(rhs.autoDeleteCreatedQueues)))) && ((this.managementBrowsePageSize == rhs.managementBrowsePageSize)
            || ((this.managementBrowsePageSize != null) && this.managementBrowsePageSize.equals(rhs.managementBrowsePageSize)))) && (
            (this.autoDeleteQueues == rhs.autoDeleteQueues) || ((this.autoDeleteQueues != null) && this.autoDeleteQueues
                .equals(rhs.autoDeleteQueues)))) && ((this.retroactiveMessageCount == rhs.retroactiveMessageCount) || (
            (this.retroactiveMessageCount != null) && this.retroactiveMessageCount.equals(rhs.retroactiveMessageCount)))) && (
            (this.maxExpiryDelay == rhs.maxExpiryDelay) || ((this.maxExpiryDelay != null) && this.maxExpiryDelay.equals(rhs.maxExpiryDelay)))) && (
            (this.lastValueQueue == rhs.lastValueQueue) || ((this.lastValueQueue != null) && this.lastValueQueue.equals(rhs.lastValueQueue)))) && (
            (this.maxDeliveryAttempts == rhs.maxDeliveryAttempts) || ((this.maxDeliveryAttempts != null) && this.maxDeliveryAttempts
                .equals(rhs.maxDeliveryAttempts)))) && ((this.defaultGroupFirstKey == rhs.defaultGroupFirstKey) || (
            (this.defaultGroupFirstKey != null) && this.defaultGroupFirstKey.equals(rhs.defaultGroupFirstKey)))) && (
            (this.autoDeleteJmsQueues == rhs.autoDeleteJmsQueues) || ((this.autoDeleteJmsQueues != null) && this.autoDeleteJmsQueues
                .equals(rhs.autoDeleteJmsQueues)))) && ((this.slowConsumerCheckPeriod == rhs.slowConsumerCheckPeriod) || (
            (this.slowConsumerCheckPeriod != null) && this.slowConsumerCheckPeriod.equals(rhs.slowConsumerCheckPeriod)))) && (
            (this.defaultPurgeOnNoConsumers == rhs.defaultPurgeOnNoConsumers) || ((this.defaultPurgeOnNoConsumers != null)
                && this.defaultPurgeOnNoConsumers.equals(rhs.defaultPurgeOnNoConsumers)))) && ((this.additionalProperties == rhs.additionalProperties)
            || ((this.additionalProperties != null) && this.additionalProperties.equals(rhs.additionalProperties)))) && (
            (this.defaultLastValueKey == rhs.defaultLastValueKey) || ((this.defaultLastValueKey != null) && this.defaultLastValueKey
                .equals(rhs.defaultLastValueKey)))) && ((this.autoCreateQueues == rhs.autoCreateQueues) || ((this.autoCreateQueues != null)
            && this.autoCreateQueues.equals(rhs.autoCreateQueues)))) && ((this.defaultExclusiveQueue == rhs.defaultExclusiveQueue) || (
            (this.defaultExclusiveQueue != null) && this.defaultExclusiveQueue.equals(rhs.defaultExclusiveQueue)))) && (
            (this.sendToDlaOnNoRoute == rhs.sendToDlaOnNoRoute) || ((this.sendToDlaOnNoRoute != null) && this.sendToDlaOnNoRoute
                .equals(rhs.sendToDlaOnNoRoute)))) && ((this.defaultMaxConsumers == rhs.defaultMaxConsumers) || ((this.defaultMaxConsumers != null)
            && this.defaultMaxConsumers.equals(rhs.defaultMaxConsumers)))) && ((this.defaultQueueRoutingType == rhs.defaultQueueRoutingType) || (
            (this.defaultQueueRoutingType != null) && this.defaultQueueRoutingType.equals(rhs.defaultQueueRoutingType)))) && (
            (this.messageCounterHistoryDayLimit == rhs.messageCounterHistoryDayLimit) || ((this.messageCounterHistoryDayLimit != null)
                && this.messageCounterHistoryDayLimit.equals(rhs.messageCounterHistoryDayLimit)))) && (
            (this.defaultGroupRebalance == rhs.defaultGroupRebalance) || ((this.defaultGroupRebalance != null) && this.defaultGroupRebalance
                .equals(rhs.defaultGroupRebalance)))) && ((this.maxSizeBytesRejectThreshold == rhs.maxSizeBytesRejectThreshold) || (
            (this.maxSizeBytesRejectThreshold != null) && this.maxSizeBytesRejectThreshold.equals(rhs.maxSizeBytesRejectThreshold)))) && (
            (this.defaultAddressRoutingType == rhs.defaultAddressRoutingType) || ((this.defaultAddressRoutingType != null)
                && this.defaultAddressRoutingType.equals(rhs.defaultAddressRoutingType)))) && (
            (this.autoCreateDeadLetterResources == rhs.autoCreateDeadLetterResources) || ((this.autoCreateDeadLetterResources != null)
                && this.autoCreateDeadLetterResources.equals(rhs.autoCreateDeadLetterResources)))) && (
            (this.autoCreateJmsQueues == rhs.autoCreateJmsQueues) || ((this.autoCreateJmsQueues != null) && this.autoCreateJmsQueues
                .equals(rhs.autoCreateJmsQueues)))) && ((this.autoDeleteJmsTopics == rhs.autoDeleteJmsTopics) || ((this.autoDeleteJmsTopics != null)
            && this.autoDeleteJmsTopics.equals(rhs.autoDeleteJmsTopics)))) && ((this.maxRedeliveryDelay == rhs.maxRedeliveryDelay) || (
            (this.maxRedeliveryDelay != null) && this.maxRedeliveryDelay.equals(rhs.maxRedeliveryDelay)))) && (
            (this.deadLetterAddress == rhs.deadLetterAddress) || ((this.deadLetterAddress != null) && this.deadLetterAddress
                .equals(rhs.deadLetterAddress)))) && ((this.configDeleteAddresses == rhs.configDeleteAddresses) || (
            (this.configDeleteAddresses != null) && this.configDeleteAddresses.equals(rhs.configDeleteAddresses)))) && (
            (this.autoDeleteQueuesMessageCount == rhs.autoDeleteQueuesMessageCount) || ((this.autoDeleteQueuesMessageCount != null)
                && this.autoDeleteQueuesMessageCount.equals(rhs.autoDeleteQueuesMessageCount)))) && (
            (this.autoDeleteAddresses == rhs.autoDeleteAddresses) || ((this.autoDeleteAddresses != null) && this.autoDeleteAddresses
                .equals(rhs.autoDeleteAddresses)))) && ((this.maxSizeBytes == rhs.maxSizeBytes) || ((this.maxSizeBytes != null) && this.maxSizeBytes
            .equals(rhs.maxSizeBytes)))) && ((this.defaultDelayBeforeDispatch == rhs.defaultDelayBeforeDispatch) || (
            (this.defaultDelayBeforeDispatch != null) && this.defaultDelayBeforeDispatch.equals(rhs.defaultDelayBeforeDispatch)))) && (
            (this.redistributionDelay == rhs.redistributionDelay) || ((this.redistributionDelay != null) && this.redistributionDelay
                .equals(rhs.redistributionDelay)))) && ((this.defaultRingSize == rhs.defaultRingSize) || ((this.defaultRingSize != null)
            && this.defaultRingSize.equals(rhs.defaultRingSize)))) && ((this.match == rhs.match) || ((this.match != null) && this.match
            .equals(rhs.match)))) && ((this.defaultLastValueQueue == rhs.defaultLastValueQueue) || ((this.defaultLastValueQueue != null)
            && this.defaultLastValueQueue.equals(rhs.defaultLastValueQueue)))) && ((this.slowConsumerPolicy == rhs.slowConsumerPolicy) || (
            (this.slowConsumerPolicy != null) && this.slowConsumerPolicy.equals(rhs.slowConsumerPolicy)))) && (
            (this.redeliveryCollisionAvoidanceFactor == rhs.redeliveryCollisionAvoidanceFactor) || ((this.redeliveryCollisionAvoidanceFactor != null)
                && this.redeliveryCollisionAvoidanceFactor.equals(rhs.redeliveryCollisionAvoidanceFactor)))) && (
            (this.autoDeleteQueuesDelay == rhs.autoDeleteQueuesDelay) || ((this.autoDeleteQueuesDelay != null) && this.autoDeleteQueuesDelay
                .equals(rhs.autoDeleteQueuesDelay)))) && ((this.autoDeleteAddressesDelay == rhs.autoDeleteAddressesDelay) || (
            (this.autoDeleteAddressesDelay != null) && this.autoDeleteAddressesDelay.equals(rhs.autoDeleteAddressesDelay)))) && (
            (this.addressFullPolicy == rhs.addressFullPolicy) || ((this.addressFullPolicy != null) && this.addressFullPolicy
                .equals(rhs.addressFullPolicy)))) && ((this.expiryDelay == rhs.expiryDelay) || ((this.expiryDelay != null) && this.expiryDelay
            .equals(rhs.expiryDelay)))) && ((this.enableMetrics == rhs.enableMetrics) || ((this.enableMetrics != null) && this.enableMetrics
            .equals(rhs.enableMetrics)))) && ((this.redeliveryDelayMultiplier == rhs.redeliveryDelayMultiplier) || (
            (this.redeliveryDelayMultiplier != null) && this.redeliveryDelayMultiplier.equals(rhs.redeliveryDelayMultiplier)))) && (
            (this.autoCreateJmsTopics == rhs.autoCreateJmsTopics) || ((this.autoCreateJmsTopics != null) && this.autoCreateJmsTopics
                .equals(rhs.autoCreateJmsTopics)))) && ((this.redeliveryDelay == rhs.redeliveryDelay) || ((this.redeliveryDelay != null)
            && this.redeliveryDelay.equals(rhs.redeliveryDelay)))) && ((this.deadLetterQueueSuffix == rhs.deadLetterQueueSuffix) || (
            (this.deadLetterQueueSuffix != null) && this.deadLetterQueueSuffix.equals(rhs.deadLetterQueueSuffix)))) && (
            (this.defaultNonDestructive == rhs.defaultNonDestructive) || ((this.defaultNonDestructive != null) && this.defaultNonDestructive
                .equals(rhs.defaultNonDestructive))));
    }

    /**
     * what happens when an address where maxSizeBytes is specified becomes full
     */
    @Generated("jsonschema2pojo")
    public enum AddressFullPolicy {

        DROP("DROP"),
        FAIL("FAIL"),
        PAGE("PAGE"),
        BLOCK("BLOCK");
        private final String value;
        private final static Map<String, AddressSetting.AddressFullPolicy> CONSTANTS = new HashMap<String, AddressSetting.AddressFullPolicy>();

        static {
            for (AddressSetting.AddressFullPolicy c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        AddressFullPolicy(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AddressSetting.AddressFullPolicy fromValue(String value) {
            AddressSetting.AddressFullPolicy constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

    /**
     * What to do when an address is no longer in broker.xml. OFF = will do nothing addresses will remain, FORCE = delete address and its queues
     * even if messages remaining.
     */
    @Generated("jsonschema2pojo")
    public enum ConfigDeleteAddresses {

        OFF("OFF"),
        FORCE("FORCE");
        private final String value;
        private final static Map<String, AddressSetting.ConfigDeleteAddresses> CONSTANTS =
            new HashMap<String, AddressSetting.ConfigDeleteAddresses>();

        static {
            for (AddressSetting.ConfigDeleteAddresses c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        ConfigDeleteAddresses(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AddressSetting.ConfigDeleteAddresses fromValue(String value) {
            AddressSetting.ConfigDeleteAddresses constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

    /**
     * What to do when a queue is no longer in broker.xml. OFF = will do nothing queues will remain, FORCE = delete queues even if messages remaining.
     */
    @Generated("jsonschema2pojo")
    public enum ConfigDeleteQueues {

        OFF("OFF"),
        FORCE("FORCE");
        private final String value;
        private final static Map<String, AddressSetting.ConfigDeleteQueues> CONSTANTS = new HashMap<String, AddressSetting.ConfigDeleteQueues>();

        static {
            for (AddressSetting.ConfigDeleteQueues c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        ConfigDeleteQueues(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AddressSetting.ConfigDeleteQueues fromValue(String value) {
            AddressSetting.ConfigDeleteQueues constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

    /**
     * the routing-type used on auto-created addresses
     */
    @Generated("jsonschema2pojo")
    public enum DefaultAddressRoutingType {

        ANYCAST("ANYCAST"),
        MULTICAST("MULTICAST");
        private final String value;
        private final static Map<String, AddressSetting.DefaultAddressRoutingType> CONSTANTS =
            new HashMap<String, AddressSetting.DefaultAddressRoutingType>();

        static {
            for (AddressSetting.DefaultAddressRoutingType c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        DefaultAddressRoutingType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AddressSetting.DefaultAddressRoutingType fromValue(String value) {
            AddressSetting.DefaultAddressRoutingType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

    /**
     * the routing-type used on auto-created queues
     */
    @Generated("jsonschema2pojo")
    public enum DefaultQueueRoutingType {

        ANYCAST("ANYCAST"),
        MULTICAST("MULTICAST");
        private final String value;
        private final static Map<String, AddressSetting.DefaultQueueRoutingType> CONSTANTS =
            new HashMap<String, AddressSetting.DefaultQueueRoutingType>();

        static {
            for (AddressSetting.DefaultQueueRoutingType c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        DefaultQueueRoutingType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AddressSetting.DefaultQueueRoutingType fromValue(String value) {
            AddressSetting.DefaultQueueRoutingType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

    /**
     * what happens when a slow consumer is identified
     */
    @Generated("jsonschema2pojo")
    public enum SlowConsumerPolicy {

        KILL("KILL"),
        NOTIFY("NOTIFY");
        private final String value;
        private final static Map<String, AddressSetting.SlowConsumerPolicy> CONSTANTS = new HashMap<String, AddressSetting.SlowConsumerPolicy>();

        static {
            for (AddressSetting.SlowConsumerPolicy c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        SlowConsumerPolicy(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @JsonValue
        public String value() {
            return this.value;
        }

        @JsonCreator
        public static AddressSetting.SlowConsumerPolicy fromValue(String value) {
            AddressSetting.SlowConsumerPolicy constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }
}
