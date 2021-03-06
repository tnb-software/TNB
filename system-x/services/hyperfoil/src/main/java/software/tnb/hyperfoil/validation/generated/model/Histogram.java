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

import java.util.Objects;

/**
 * Histogram
 */
public class Histogram {
    public static final String SERIALIZED_NAME_PHASE = "phase";
    public static final String SERIALIZED_NAME_METRIC = "metric";
    public static final String SERIALIZED_NAME_START_TIME = "startTime";
    public static final String SERIALIZED_NAME_END_TIME = "endTime";
    public static final String SERIALIZED_NAME_DATA = "data";
    @SerializedName(SERIALIZED_NAME_PHASE)
    private String phase;
    @SerializedName(SERIALIZED_NAME_METRIC)
    private String metric;
    @SerializedName(SERIALIZED_NAME_START_TIME)
    private Integer startTime;
    @SerializedName(SERIALIZED_NAME_END_TIME)
    private Integer endTime;
    @SerializedName(SERIALIZED_NAME_DATA)
    private String data;

    public Histogram() {
    }

    public Histogram phase(String phase) {

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

    public Histogram metric(String metric) {

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

    public Histogram startTime(Integer startTime) {

        this.startTime = startTime;
        return this;
    }

    /**
     * Get startTime
     *
     * @return startTime
     **/
    

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Histogram endTime(Integer endTime) {

        this.endTime = endTime;
        return this;
    }

    /**
     * Get endTime
     *
     * @return endTime
     **/
    

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Histogram data(String data) {

        this.data = data;
        return this;
    }

    /**
     * Get data
     *
     * @return data
     **/
    

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Histogram histogram = (Histogram) o;
        return Objects.equals(this.phase, histogram.phase)
            && Objects.equals(this.metric, histogram.metric)
            && Objects.equals(this.startTime, histogram.startTime)
            && Objects.equals(this.endTime, histogram.endTime)
            && Objects.equals(this.data, histogram.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phase, metric, startTime, endTime, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Histogram {\n");
        sb.append("    phase: ").append(toIndentedString(phase)).append("\n");
        sb.append("    metric: ").append(toIndentedString(metric)).append("\n");
        sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
        sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

