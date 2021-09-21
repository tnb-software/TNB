package org.jboss.fuse.tnb.servicenow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

/**
 * Class representing incident object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Incident {
    private Boolean active;
    @JsonProperty("activity_due")
    private String activityDue;
    @JsonProperty("additional_assignee_list")
    private String additionalAssigneeList;
    private String approval;
    @JsonProperty("approval_set")
    private String approvalSet;
    @JsonProperty("approval_history")
    private String approvalHistory;
    @JsonProperty("assigned_to")
    private LinkValue assignedTo;
    @JsonProperty("assignment_group")
    private LinkValue assignmentGroup;
    @JsonProperty("business_duration")
    private String businessDuration;
    @JsonProperty("business_service")
    private String businessService;
    @JsonProperty("business_stc")
    private BigInteger businessStc;
    @JsonProperty("calendar_duration")
    private String calendarDuration;
    @JsonProperty("calendar_stc")
    private BigInteger calendarStc;
    @JsonProperty("caller_id")
    private LinkValue callerId;
    private String category;
    @JsonProperty("caused_by")
    private LinkValue causedBy;
    @JsonProperty("child_incidents")
    private BigInteger childIncidents;
    @JsonProperty("close_code")
    private String closeCode;
    @JsonProperty("close_notes")
    private String closeNotes;
    @JsonProperty("closed_at")
    private String closedAt;
    @JsonProperty("closed_by")
    private LinkValue closedBy;
    @JsonProperty("cmdb_ci")
    private LinkValue cmdbCi;
    private String comments;
    @JsonProperty("comments_and_work_notes")
    private String commentsAndWorkNotes;
    private LinkValue company;
    @JsonProperty("contact_type")
    private String contactType;
    @JsonProperty("correlation_display")
    private String correlationDisplay;
    @JsonProperty("correlation_id")
    private String correlationId;
    @JsonProperty("delivery_plan")
    private String deliveryPlan;
    @JsonProperty("delivery_task")
    private String deliveryTask;
    private String description;
    @JsonProperty("due_date")
    private String dueDate;
    private BigInteger escalation;
    @JsonProperty("expected_start")
    private String expectedStart;
    @JsonProperty("follow_up")
    private String followUp;
    @JsonProperty("group_list")
    private String groupList;
    private BigInteger impact;
    @JsonProperty("incident_state")
    private BigInteger incidentState;
    private Boolean knowledge;
    private LinkValue location;
    @JsonProperty("made_sla")
    private Boolean madeSla;
    private BigInteger notify;
    private String number;
    @JsonProperty("opened_at")
    private String openedAt;
    @JsonProperty("opened_by")
    private LinkValue openedBy;
    private BigInteger order;
    private String parent;
    @JsonProperty("parent_incident")
    private String parentIncident;
    private BigInteger priority;
    @JsonProperty("problem_id")
    private LinkValue problemId;
    @JsonProperty("reassignment_count")
    private BigInteger reassignmentCount;
    @JsonProperty("reopen_count")
    private BigInteger reopenCount;
    @JsonProperty("resolved_at")
    private String resolvedAt;
    @JsonProperty("resolved_by")
    private LinkValue resolvedBy;
    private String rfc;
    private BigInteger severity;
    @JsonProperty("short_description")
    private String shortDescription;
    @JsonProperty("sla_due")
    private String slaDue;
    private BigInteger state;
    private String subcategory;
    @JsonProperty("sys_class_name")
    private String sysClassName;
    @JsonProperty("sys_created_by")
    private String sysCreatedBy;
    @JsonProperty("sys_created_on")
    private String sysCreatedOn;
    @JsonProperty("sys_domain")
    private LinkValue sysDomain;
    @JsonProperty("sys_domain_path")
    private String sysDomainPath;
    @JsonProperty("sys_id")
    private String sysId;
    @JsonProperty("sys_mod_count")
    private BigInteger sysModCount;
    @JsonProperty("sys_tags")
    private String sysTags;
    @JsonProperty("sys_updated_by")
    private String sysUpdatedBy;
    @JsonProperty("sys_updated_on")
    private String sysUpdatedOn;
    @JsonProperty("time_worked")
    private String timeWorked;
    @JsonProperty("upon_approval")
    private String uponApproval;
    @JsonProperty("upon_reject")
    private String uponReject;
    private BigInteger urgency;
    @JsonProperty("user_input")
    private String userInput;
    @JsonProperty("watch_list")
    private String watchList;
    @JsonProperty("work_end")
    private String workEnd;
    @JsonProperty("work_notes")
    private String workNotes;
    @JsonProperty("work_notes_list")
    private String workNotesList;
    @JsonProperty("work_start")
    private String workStart;

    public Incident() {
    }

    public Boolean getActive() {
        return this.active;
    }

    public String getActivityDue() {
        return this.activityDue;
    }

    public String getAdditionalAssigneeList() {
        return this.additionalAssigneeList;
    }

    public String getApproval() {
        return this.approval;
    }

    public String getApprovalSet() {
        return this.approvalSet;
    }

    public String getApprovalHistory() {
        return this.approvalHistory;
    }

    public LinkValue getAssignedTo() {
        return this.assignedTo;
    }

    public LinkValue getAssignmentGroup() {
        return this.assignmentGroup;
    }

    public String getBusinessDuration() {
        return this.businessDuration;
    }

    public String getBusinessService() {
        return this.businessService;
    }

    public BigInteger getBusinessStc() {
        return this.businessStc;
    }

    public String getCalendarDuration() {
        return this.calendarDuration;
    }

    public BigInteger getCalendarStc() {
        return this.calendarStc;
    }

    public LinkValue getCallerId() {
        return this.callerId;
    }

    public String getCategory() {
        return this.category;
    }

    public LinkValue getCausedBy() {
        return this.causedBy;
    }

    public BigInteger getChildIncidents() {
        return this.childIncidents;
    }

    public String getCloseCode() {
        return this.closeCode;
    }

    public String getCloseNotes() {
        return this.closeNotes;
    }

    public String getClosedAt() {
        return this.closedAt;
    }

    public LinkValue getClosedBy() {
        return this.closedBy;
    }

    public LinkValue getCmdbCi() {
        return this.cmdbCi;
    }

    public String getComments() {
        return this.comments;
    }

    public String getCommentsAndWorkNotes() {
        return this.commentsAndWorkNotes;
    }

    public LinkValue getCompany() {
        return this.company;
    }

    public String getContactType() {
        return this.contactType;
    }

    public String getCorrelationDisplay() {
        return this.correlationDisplay;
    }

    public String getCorrelationId() {
        return this.correlationId;
    }

    public String getDeliveryPlan() {
        return this.deliveryPlan;
    }

    public String getDeliveryTask() {
        return this.deliveryTask;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public BigInteger getEscalation() {
        return this.escalation;
    }

    public String getExpectedStart() {
        return this.expectedStart;
    }

    public String getFollowUp() {
        return this.followUp;
    }

    public String getGroupList() {
        return this.groupList;
    }

    public BigInteger getImpact() {
        return this.impact;
    }

    public BigInteger getIncidentState() {
        return this.incidentState;
    }

    public Boolean getKnowledge() {
        return this.knowledge;
    }

    public LinkValue getLocation() {
        return this.location;
    }

    public Boolean getMadeSla() {
        return this.madeSla;
    }

    public BigInteger getNotify() {
        return this.notify;
    }

    public String getNumber() {
        return this.number;
    }

    public String getOpenedAt() {
        return this.openedAt;
    }

    public LinkValue getOpenedBy() {
        return this.openedBy;
    }

    public BigInteger getOrder() {
        return this.order;
    }

    public String getParent() {
        return this.parent;
    }

    public String getParentIncident() {
        return this.parentIncident;
    }

    public BigInteger getPriority() {
        return this.priority;
    }

    public LinkValue getProblemId() {
        return this.problemId;
    }

    public BigInteger getReassignmentCount() {
        return this.reassignmentCount;
    }

    public BigInteger getReopenCount() {
        return this.reopenCount;
    }

    public String getResolvedAt() {
        return this.resolvedAt;
    }

    public LinkValue getResolvedBy() {
        return this.resolvedBy;
    }

    public String getRfc() {
        return this.rfc;
    }

    public BigInteger getSeverity() {
        return this.severity;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public String getSlaDue() {
        return this.slaDue;
    }

    public BigInteger getState() {
        return this.state;
    }

    public String getSubcategory() {
        return this.subcategory;
    }

    public String getSysClassName() {
        return this.sysClassName;
    }

    public String getSysCreatedBy() {
        return this.sysCreatedBy;
    }

    public String getSysCreatedOn() {
        return this.sysCreatedOn;
    }

    public LinkValue getSysDomain() {
        return this.sysDomain;
    }

    public String getSysDomainPath() {
        return this.sysDomainPath;
    }

    public String getSysId() {
        return this.sysId;
    }

    public BigInteger getSysModCount() {
        return this.sysModCount;
    }

    public String getSysTags() {
        return this.sysTags;
    }

    public String getSysUpdatedBy() {
        return this.sysUpdatedBy;
    }

    public String getSysUpdatedOn() {
        return this.sysUpdatedOn;
    }

    public String getTimeWorked() {
        return this.timeWorked;
    }

    public String getUponApproval() {
        return this.uponApproval;
    }

    public String getUponReject() {
        return this.uponReject;
    }

    public BigInteger getUrgency() {
        return this.urgency;
    }

    public String getUserInput() {
        return this.userInput;
    }

    public String getWatchList() {
        return this.watchList;
    }

    public String getWorkEnd() {
        return this.workEnd;
    }

    public String getWorkNotes() {
        return this.workNotes;
    }

    public String getWorkNotesList() {
        return this.workNotesList;
    }

    public String getWorkStart() {
        return this.workStart;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setActivityDue(String activityDue) {
        this.activityDue = activityDue;
    }

    public void setAdditionalAssigneeList(String additionalAssigneeList) {
        this.additionalAssigneeList = additionalAssigneeList;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public void setApprovalSet(String approvalSet) {
        this.approvalSet = approvalSet;
    }

    public void setApprovalHistory(String approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    public void setAssignedTo(LinkValue assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setAssignmentGroup(LinkValue assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public void setBusinessDuration(String businessDuration) {
        this.businessDuration = businessDuration;
    }

    public void setBusinessService(String businessService) {
        this.businessService = businessService;
    }

    public void setBusinessStc(BigInteger businessStc) {
        this.businessStc = businessStc;
    }

    public void setCalendarDuration(String calendarDuration) {
        this.calendarDuration = calendarDuration;
    }

    public void setCalendarStc(BigInteger calendarStc) {
        this.calendarStc = calendarStc;
    }

    public void setCallerId(LinkValue callerId) {
        this.callerId = callerId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCausedBy(LinkValue causedBy) {
        this.causedBy = causedBy;
    }

    public void setChildIncidents(BigInteger childIncidents) {
        this.childIncidents = childIncidents;
    }

    public void setCloseCode(String closeCode) {
        this.closeCode = closeCode;
    }

    public void setCloseNotes(String closeNotes) {
        this.closeNotes = closeNotes;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public void setClosedBy(LinkValue closedBy) {
        this.closedBy = closedBy;
    }

    public void setCmdbCi(LinkValue cmdbCi) {
        this.cmdbCi = cmdbCi;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCommentsAndWorkNotes(String commentsAndWorkNotes) {
        this.commentsAndWorkNotes = commentsAndWorkNotes;
    }

    public void setCompany(LinkValue company) {
        this.company = company;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public void setCorrelationDisplay(String correlationDisplay) {
        this.correlationDisplay = correlationDisplay;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setDeliveryPlan(String deliveryPlan) {
        this.deliveryPlan = deliveryPlan;
    }

    public void setDeliveryTask(String deliveryTask) {
        this.deliveryTask = deliveryTask;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setEscalation(BigInteger escalation) {
        this.escalation = escalation;
    }

    public void setExpectedStart(String expectedStart) {
        this.expectedStart = expectedStart;
    }

    public void setFollowUp(String followUp) {
        this.followUp = followUp;
    }

    public void setGroupList(String groupList) {
        this.groupList = groupList;
    }

    public void setImpact(BigInteger impact) {
        this.impact = impact;
    }

    public void setIncidentState(BigInteger incidentState) {
        this.incidentState = incidentState;
    }

    public void setKnowledge(Boolean knowledge) {
        this.knowledge = knowledge;
    }

    public void setLocation(LinkValue location) {
        this.location = location;
    }

    public void setMadeSla(Boolean madeSla) {
        this.madeSla = madeSla;
    }

    public void setNotify(BigInteger notify) {
        this.notify = notify;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public void setOpenedBy(LinkValue openedBy) {
        this.openedBy = openedBy;
    }

    public void setOrder(BigInteger order) {
        this.order = order;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setParentIncident(String parentIncident) {
        this.parentIncident = parentIncident;
    }

    public void setPriority(BigInteger priority) {
        this.priority = priority;
    }

    public void setProblemId(LinkValue problemId) {
        this.problemId = problemId;
    }

    public void setReassignmentCount(BigInteger reassignmentCount) {
        this.reassignmentCount = reassignmentCount;
    }

    public void setReopenCount(BigInteger reopenCount) {
        this.reopenCount = reopenCount;
    }

    public void setResolvedAt(String resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public void setResolvedBy(LinkValue resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public void setSeverity(BigInteger severity) {
        this.severity = severity;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setSlaDue(String slaDue) {
        this.slaDue = slaDue;
    }

    public void setState(BigInteger state) {
        this.state = state;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setSysClassName(String sysClassName) {
        this.sysClassName = sysClassName;
    }

    public void setSysCreatedBy(String sysCreatedBy) {
        this.sysCreatedBy = sysCreatedBy;
    }

    public void setSysCreatedOn(String sysCreatedOn) {
        this.sysCreatedOn = sysCreatedOn;
    }

    public void setSysDomain(LinkValue sysDomain) {
        this.sysDomain = sysDomain;
    }

    public void setSysDomainPath(String sysDomainPath) {
        this.sysDomainPath = sysDomainPath;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public void setSysModCount(BigInteger sysModCount) {
        this.sysModCount = sysModCount;
    }

    public void setSysTags(String sysTags) {
        this.sysTags = sysTags;
    }

    public void setSysUpdatedBy(String sysUpdatedBy) {
        this.sysUpdatedBy = sysUpdatedBy;
    }

    public void setSysUpdatedOn(String sysUpdatedOn) {
        this.sysUpdatedOn = sysUpdatedOn;
    }

    public void setTimeWorked(String timeWorked) {
        this.timeWorked = timeWorked;
    }

    public void setUponApproval(String uponApproval) {
        this.uponApproval = uponApproval;
    }

    public void setUponReject(String uponReject) {
        this.uponReject = uponReject;
    }

    public void setUrgency(BigInteger urgency) {
        this.urgency = urgency;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public void setWatchList(String watchList) {
        this.watchList = watchList;
    }

    public void setWorkEnd(String workEnd) {
        this.workEnd = workEnd;
    }

    public void setWorkNotes(String workNotes) {
        this.workNotes = workNotes;
    }

    public void setWorkNotesList(String workNotesList) {
        this.workNotesList = workNotesList;
    }

    public void setWorkStart(String workStart) {
        this.workStart = workStart;
    }

    public String toString() {
        return "Incident(active=" + this.getActive() + ", activityDue=" + this.getActivityDue() + ", additionalAssigneeList="
            + this.getAdditionalAssigneeList() + ", approval=" + this.getApproval() + ", approvalSet=" + this.getApprovalSet() + ", approvalHistory="
            + this.getApprovalHistory() + ", assignedTo=" + this.getAssignedTo() + ", assignmentGroup=" + this.getAssignmentGroup()
            + ", businessDuration=" + this.getBusinessDuration() + ", businessService=" + this.getBusinessService() + ", businessStc="
            + this.getBusinessStc() + ", calendarDuration=" + this.getCalendarDuration() + ", calendarStc=" + this.getCalendarStc() + ", callerId="
            + this.getCallerId() + ", category=" + this.getCategory() + ", causedBy=" + this.getCausedBy() + ", childIncidents="
            + this.getChildIncidents() + ", closeCode=" + this.getCloseCode() + ", closeNotes=" + this.getCloseNotes() + ", closedAt="
            + this.getClosedAt() + ", closedBy=" + this.getClosedBy() + ", cmdbCi=" + this.getCmdbCi() + ", comments=" + this.getComments()
            + ", commentsAndWorkNotes=" + this.getCommentsAndWorkNotes() + ", company=" + this.getCompany() + ", contactType=" + this.getContactType()
            + ", correlationDisplay=" + this.getCorrelationDisplay() + ", correlationId=" + this.getCorrelationId() + ", deliveryPlan="
            + this.getDeliveryPlan() + ", deliveryTask=" + this.getDeliveryTask() + ", description=" + this.getDescription() + ", dueDate="
            + this.getDueDate() + ", escalation=" + this.getEscalation() + ", expectedStart=" + this.getExpectedStart() + ", followUp="
            + this.getFollowUp() + ", groupList=" + this.getGroupList() + ", impact=" + this.getImpact() + ", incidentState="
            + this.getIncidentState() + ", knowledge=" + this.getKnowledge() + ", location=" + this.getLocation() + ", madeSla=" + this.getMadeSla()
            + ", notify=" + this.getNotify() + ", number=" + this.getNumber() + ", openedAt=" + this.getOpenedAt() + ", openedBy="
            + this.getOpenedBy() + ", order=" + this.getOrder() + ", parent=" + this.getParent() + ", parentIncident=" + this.getParentIncident()
            + ", priority=" + this.getPriority() + ", problemId=" + this.getProblemId() + ", reassignmentCount=" + this.getReassignmentCount()
            + ", reopenCount=" + this.getReopenCount() + ", resolvedAt=" + this.getResolvedAt() + ", resolvedBy=" + this.getResolvedBy() + ", rfc="
            + this.getRfc() + ", severity=" + this.getSeverity() + ", shortDescription=" + this.getShortDescription() + ", slaDue=" + this.getSlaDue()
            + ", state=" + this.getState() + ", subcategory=" + this.getSubcategory() + ", sysClassName=" + this.getSysClassName() + ", sysCreatedBy="
            + this.getSysCreatedBy() + ", sysCreatedOn=" + this.getSysCreatedOn() + ", sysDomain=" + this.getSysDomain() + ", sysDomainPath="
            + this.getSysDomainPath() + ", sysId=" + this.getSysId() + ", sysModCount=" + this.getSysModCount() + ", sysTags=" + this.getSysTags()
            + ", sysUpdatedBy=" + this.getSysUpdatedBy() + ", sysUpdatedOn=" + this.getSysUpdatedOn() + ", timeWorked=" + this.getTimeWorked()
            + ", uponApproval=" + this.getUponApproval() + ", uponReject=" + this.getUponReject() + ", urgency=" + this.getUrgency() + ", userInput="
            + this.getUserInput() + ", watchList=" + this.getWatchList() + ", workEnd=" + this.getWorkEnd() + ", workNotes=" + this.getWorkNotes()
            + ", workNotesList=" + this.getWorkNotesList() + ", workStart=" + this.getWorkStart() + ")";
    }
}
