package org.jboss.fuse.tnb.cryostat.generated.recording;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "downloadUrl",
    "reportUrl",
    "id",
    "name",
    "state",
    "startTime",
    "duration",
    "continuous",
    "toDisk",
    "maxSize",
    "maxAge"
})
@Generated("jsonschema2pojo")
public class Recording {

    @JsonProperty("downloadUrl")
    private String downloadUrl;
    @JsonProperty("reportUrl")
    private String reportUrl;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("state")
    private String state;
    @JsonProperty("startTime")
    private Long startTime;
    @JsonProperty("duration")
    private Integer duration;
    @JsonProperty("continuous")
    private Boolean continuous;
    @JsonProperty("toDisk")
    private Boolean toDisk;
    @JsonProperty("maxSize")
    private Integer maxSize;
    @JsonProperty("maxAge")
    private Integer maxAge;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("downloadUrl")
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @JsonProperty("downloadUrl")
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @JsonProperty("reportUrl")
    public String getReportUrl() {
        return reportUrl;
    }

    @JsonProperty("reportUrl")
    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("startTime")
    public Long getStartTime() {
        return startTime;
    }

    @JsonProperty("startTime")
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("duration")
    public Integer getDuration() {
        return duration;
    }

    @JsonProperty("duration")
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @JsonProperty("continuous")
    public Boolean getContinuous() {
        return continuous;
    }

    @JsonProperty("continuous")
    public void setContinuous(Boolean continuous) {
        this.continuous = continuous;
    }

    @JsonProperty("toDisk")
    public Boolean getToDisk() {
        return toDisk;
    }

    @JsonProperty("toDisk")
    public void setToDisk(Boolean toDisk) {
        this.toDisk = toDisk;
    }

    @JsonProperty("maxSize")
    public Integer getMaxSize() {
        return maxSize;
    }

    @JsonProperty("maxSize")
    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    @JsonProperty("maxAge")
    public Integer getMaxAge() {
        return maxAge;
    }

    @JsonProperty("maxAge")
    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
