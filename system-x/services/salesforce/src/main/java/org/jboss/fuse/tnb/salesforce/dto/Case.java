package org.jboss.fuse.tnb.salesforce.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Case {

    @JsonProperty(value = "Id")
    private String id;
    @JsonProperty(value = "ContactId")
    private String contactId;
    @JsonProperty(value = "AccountId")
    private String accountId;
    @JsonProperty(value = "AssetId")
    private String assetId;
    @JsonProperty(value = "SuppliedName")
    private String suppliedName;
    @JsonProperty(value = "SuppliedEmail")
    private String suppliedEmail;
    @JsonProperty(value = "SuppliedPhone")
    private String suppliedPhone;
    @JsonProperty(value = "SuppliedCompany")
    private String suppliedCompany;
    @JsonProperty(value = "Type")
    private String type;
    @JsonProperty(value = "Status")
    private String status;
    @JsonProperty(value = "Reason")
    private String reason;
    @JsonProperty(value = "Origin")
    private String origin;
    @JsonProperty(value = "Subject")
    private String subject;
    @JsonProperty(value = "Priority")
    private String priority;
    @JsonProperty(value = "Description")
    private String description;
    @JsonProperty(value = "Comments")
    private String comments;
    @JsonProperty(value = "EngineeringReqNumber__c")
    private String engineeringReqNumber;

    public Case(String accountId, String status, String origin, String subject) {
        this.accountId = accountId;
        this.status = status;
        this.origin = origin;
        this.subject = subject;
    }

    public Case() {
    }

    public String getId() {
        return this.id;
    }

    public String getContactId() {
        return this.contactId;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getAssetId() {
        return this.assetId;
    }

    public String getSuppliedName() {
        return this.suppliedName;
    }

    public String getSuppliedEmail() {
        return this.suppliedEmail;
    }

    public String getSuppliedPhone() {
        return this.suppliedPhone;
    }

    public String getSuppliedCompany() {
        return this.suppliedCompany;
    }

    public String getType() {
        return this.type;
    }

    public String getStatus() {
        return this.status;
    }

    public String getReason() {
        return this.reason;
    }

    public String getOrigin() {
        return this.origin;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getPriority() {
        return this.priority;
    }

    public String getDescription() {
        return this.description;
    }

    public String getComments() {
        return this.comments;
    }

    public String getEngineeringReqNumber() {
        return this.engineeringReqNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setSuppliedName(String suppliedName) {
        this.suppliedName = suppliedName;
    }

    public void setSuppliedEmail(String suppliedEmail) {
        this.suppliedEmail = suppliedEmail;
    }

    public void setSuppliedPhone(String suppliedPhone) {
        this.suppliedPhone = suppliedPhone;
    }

    public void setSuppliedCompany(String suppliedCompany) {
        this.suppliedCompany = suppliedCompany;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setEngineeringReqNumber(String engineeringReqNumber) {
        this.engineeringReqNumber = engineeringReqNumber;
    }

    public String toString() {
        return "Case(id=" + this.id + ", contactId=" + this.contactId + ", accountId=" + this.accountId + ", assetId=" + this.assetId
            + ", suppliedName=" + this.suppliedName + ", suppliedEmail=" + this.suppliedEmail + ", suppliedPhone=" + this.suppliedPhone
            + ", suppliedCompany=" + this.suppliedCompany + ", type=" + this.type + ", status=" + this.status + ", reason=" + this.reason
            + ", origin=" + this.origin + ", subject=" + this.subject + ", priority=" + this.priority + ", description=" + this.description
            + ", comments=" + this.comments + ", engineeringReqNumber=" + this.engineeringReqNumber + ")";
    }
}
