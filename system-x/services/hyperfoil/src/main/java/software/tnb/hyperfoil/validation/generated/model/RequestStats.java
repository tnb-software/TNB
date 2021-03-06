/*
 * Hyperfoil Controller API
 * Hyperfoil Controller API
 *
 * The version of the OpenAPI document: 0.5
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package software.tnb.hyperfoil.validation.generated.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RequestStats
 */
public class RequestStats {
    public static final String SERIALIZED_NAME_PHASE = "phase";
    public static final String SERIALIZED_NAME_STEP_ID = "stepId";
    public static final String SERIALIZED_NAME_METRIC = "metric";
    public static final String SERIALIZED_NAME_SUMMARY = "summary";
    public static final String SERIALIZED_NAME_FAILED_S_L_AS = "failedSLAs";
    public static final String SERIALIZED_NAME_IS_WARMUP = "isWarmup";
    @SerializedName(SERIALIZED_NAME_PHASE)
    private String phase;
    @SerializedName(SERIALIZED_NAME_STEP_ID)
    private Integer stepId = 0;
    @SerializedName(SERIALIZED_NAME_METRIC)
    private String metric;
    @SerializedName(SERIALIZED_NAME_SUMMARY)
    private Object summary;
    @SerializedName(SERIALIZED_NAME_FAILED_S_L_AS)
    private List<String> failedSLAs = null;
    @SerializedName(SERIALIZED_NAME_IS_WARMUP)
    private Boolean isWarmup;

    public RequestStats() {
    }

    public RequestStats phase(String phase) {

        this.phase = phase;
        return this;
    }

    /**
     * Get phase
     *
     * @return phase
     **/
    

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public RequestStats stepId(Integer stepId) {

        this.stepId = stepId;
        return this;
    }

    /**
     * Get stepId
     *
     * @return stepId
     **/
    

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public RequestStats metric(String metric) {

        this.metric = metric;
        return this;
    }

    /**
     * Get metric
     *
     * @return metric
     **/
    

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public RequestStats summary(Object summary) {

        this.summary = summary;
        return this;
    }

    /**
     * Get summary
     *
     * @return summary
     **/
    

    public Object getSummary() {
        return summary;
    }

    public void setSummary(Object summary) {
        this.summary = summary;
    }

    public RequestStats failedSLAs(List<String> failedSLAs) {

        this.failedSLAs = failedSLAs;
        return this;
    }

    public RequestStats addFailedSLAsItem(String failedSLAsItem) {
        if (this.failedSLAs == null) {
            this.failedSLAs = new ArrayList<String>();
        }
        this.failedSLAs.add(failedSLAsItem);
        return this;
    }

    /**
     * Get failedSLAs
     *
     * @return failedSLAs
     **/
    

    public List<String> getFailedSLAs() {
        return failedSLAs;
    }

    public void setFailedSLAs(List<String> failedSLAs) {
        this.failedSLAs = failedSLAs;
    }

    public RequestStats isWarmup(Boolean isWarmup) {

        this.isWarmup = isWarmup;
        return this;
    }

    /**
     * Get isWarmup
     *
     * @return isWarmup
     **/
    

    public Boolean getIsWarmup() {
        return isWarmup;
    }

    public void setIsWarmup(Boolean isWarmup) {
        this.isWarmup = isWarmup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestStats requestStats = (RequestStats) o;
        return Objects.equals(this.phase, requestStats.phase)
            && Objects.equals(this.stepId, requestStats.stepId)
            && Objects.equals(this.metric, requestStats.metric)
            && Objects.equals(this.summary, requestStats.summary)
            && Objects.equals(this.failedSLAs, requestStats.failedSLAs)
            && Objects.equals(this.isWarmup, requestStats.isWarmup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phase, stepId, metric, summary, failedSLAs, isWarmup);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestStats {\n");
        sb.append("    phase: ").append(toIndentedString(phase)).append("\n");
        sb.append("    stepId: ").append(toIndentedString(stepId)).append("\n");
        sb.append("    metric: ").append(toIndentedString(metric)).append("\n");
        sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
        sb.append("    failedSLAs: ").append(toIndentedString(failedSLAs)).append("\n");
        sb.append("    isWarmup: ").append(toIndentedString(isWarmup)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

