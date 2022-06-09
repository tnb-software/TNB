package software.tnb.jms.amq.service.openshift.generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.KubernetesResource;

/**
 * a list of address settings
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "applyRule",
    "addressSetting"
})
@Generated("jsonschema2pojo")
public class AddressSettings implements KubernetesResource {

    /**
     * a flag APPLY_RULE that indicates on what parts of address settings in broker.xml to perform the merge. It has 3 possible values:
     * =replace_all The merge performs merge on the address-settings as a whole part. =merge_replace The merge performs merge on each
     * address-setting element =merge_all The merge performs merge on each property of every address-setting This is the default value
     */
    @JsonProperty("applyRule")
    @JsonPropertyDescription("a flag APPLY_RULE that indicates on what parts of address settings in broker.xml to perform the merge. It has 3 "
        + "possible values: =replace_all The merge performs merge on the address-settings as a whole part. =merge_replace The merge performs merge "
        + "on each address-setting element =merge_all The merge performs merge on each property of every address-setting This is the default value")
    private String applyRule;
    /**
     * address setting configuration
     */
    @JsonProperty("addressSetting")
    @JsonPropertyDescription("address setting configuration")
    private List<AddressSetting> addressSetting = new ArrayList<AddressSetting>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * a flag APPLY_RULE that indicates on what parts of address settings in broker.xml to perform the merge. It has 3 possible values:
     * =replace_all The merge performs merge on the address-settings as a whole part. =merge_replace The merge performs merge on each
     * address-setting element =merge_all The merge performs merge on each property of every address-setting This is the default value
     */
    @JsonProperty("applyRule")
    public String getApplyRule() {
        return applyRule;
    }

    /**
     * a flag APPLY_RULE that indicates on what parts of address settings in broker.xml to perform the merge. It has 3 possible values:
     * =replace_all The merge performs merge on the address-settings as a whole part. =merge_replace The merge performs merge on each
     * address-setting element =merge_all The merge performs merge on each property of every address-setting This is the default value
     */
    @JsonProperty("applyRule")
    public void setApplyRule(String applyRule) {
        this.applyRule = applyRule;
    }

    public AddressSettings withApplyRule(String applyRule) {
        this.applyRule = applyRule;
        return this;
    }

    /**
     * address setting configuration
     */
    @JsonProperty("addressSetting")
    public List<AddressSetting> getAddressSetting() {
        return addressSetting;
    }

    /**
     * address setting configuration
     */
    @JsonProperty("addressSetting")
    public void setAddressSetting(List<AddressSetting> addressSetting) {
        this.addressSetting = addressSetting;
    }

    public AddressSettings withAddressSetting(List<AddressSetting> addressSetting) {
        this.addressSetting = addressSetting;
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

    public AddressSettings withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AddressSettings.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("applyRule");
        sb.append('=');
        sb.append(((this.applyRule == null) ? "<null>" : this.applyRule));
        sb.append(',');
        sb.append("addressSetting");
        sb.append('=');
        sb.append(((this.addressSetting == null) ? "<null>" : this.addressSetting));
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
        result = ((result * 31) + ((this.additionalProperties == null) ? 0 : this.additionalProperties.hashCode()));
        result = ((result * 31) + ((this.applyRule == null) ? 0 : this.applyRule.hashCode()));
        result = ((result * 31) + ((this.addressSetting == null) ? 0 : this.addressSetting.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AddressSettings) == false) {
            return false;
        }
        AddressSettings rhs = ((AddressSettings) other);
        return ((((this.additionalProperties == rhs.additionalProperties) || ((this.additionalProperties != null) && this.additionalProperties
            .equals(rhs.additionalProperties))) && ((this.applyRule == rhs.applyRule) || ((this.applyRule != null) && this.applyRule
            .equals(rhs.applyRule)))) && ((this.addressSetting == rhs.addressSetting) || ((this.addressSetting != null) && this.addressSetting
            .equals(rhs.addressSetting))));
    }
}
