package software.tnb.salesforce.validation;

import software.tnb.common.validation.Validation;
import software.tnb.salesforce.dto.Account;
import software.tnb.salesforce.dto.Case;
import software.tnb.salesforce.dto.Lead;

import org.junit.jupiter.api.Assertions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.ApiException;
import com.force.api.ForceApi;
import com.force.api.QueryResult;
import com.force.api.ResourceRepresentation;
import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SalesforceValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(SalesforceValidation.class);

    private final ForceApi client;

    public SalesforceValidation(ForceApi client) {
        this.client = client;
    }

    public String createNewLead(Lead lead) {
        String leadId = client.createSObject("lead", lead);
        LOG.debug("Created lead with id " + leadId);
        return leadId;
    }

    public String createNewLead(String firstName, String lastName, String email, String companyName) {
        return createNewLead(new Lead(firstName, lastName, email, companyName));
    }

    public void updateLead(String email, Lead newLead) {
        Optional<Lead> sfLead = getLeadByEmail(email);
        Assertions.assertTrue(sfLead.isPresent());
        String leadId = sfLead.get().getId();
        client.updateSObject("lead", leadId, newLead);
    }

    public void deleteLead(String email) {
        final Optional<Lead> lead = getLeadByEmail(email);
        if (lead.isPresent()) {
            String leadId = lead.get().getId();
            client.deleteSObject("lead", leadId);
            LOG.debug("Deleting salesforce lead: {}", lead.get());
        }
    }

    public String createCase(String accountId, String status, String origin, String subject) {
        final Case newCase = new Case(accountId, status, origin, subject);
        String caseId = client.createSObject("Case", newCase);
        LOG.debug("Created case with id " + caseId);
        return caseId;
    }

    public void deleteCase(String id) {
        client.deleteSObject("case", id);
        LOG.debug("Deleting salesforce case with id : {}", id);
    }

    public String createAccount(String name, String phone) {
        final Account sfAccount = new Account(name, phone);
        String accountId = client.createSObject("Account", sfAccount);
        LOG.debug("Created account with id " + accountId);
        return accountId;
    }

    public void deleteAccount(String id) {
        client.deleteSObject("account", id);
        LOG.debug("Deleting salesforce account with id : {}", id);
    }

    public List<Map> getTopics() {
        QueryResult<Map> queryResult = client.query("SELECT Id,Name FROM PushTopic");
        return queryResult.getRecords();
    }

    public void deleteTopic(String topicName) {
        LOG.info("deleting topic {}", topicName);
        QueryResult<Map> queryResult = client.query("SELECT Id FROM PushTopic where Name = '" + topicName + "'");
        if (queryResult.getTotalSize() > 0) {
            String topicId = (String) queryResult.getRecords().get(0).get("Id");
            client.deleteSObject("PushTopic", topicId);
        }
    }

    public Optional<Lead> getLeadByEmail(String emailAddress) {
        final QueryResult<Lead> queryResult =
            client.query("SELECT Id,FirstName,LastName,Email,Company FROM lead where Email = '"
                + emailAddress + "'", Lead.class
            );
        return queryResult.getTotalSize() > 0 ? Optional.of(queryResult.getRecords().get(0)) : Optional.empty();
    }

    public List<Case> getCasesByAccountId(String accountId) {
        final QueryResult<Case> queryResult =
            client.query("SELECT Id,Status,Origin,AccountId,Subject FROM case where AccountId = '"
                + accountId + "'", Case.class
            );
        return queryResult.getRecords();
    }

    public ResourceRepresentation getBulkJob(String id, Boolean isBulkV2) throws ApiException {
        if (isBulkV2) {
            return client.get("/jobs/ingest/" + id);
        } else {
            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            HttpResponse res = Http.send(new HttpRequest()
                .url(client.getSession().getApiEndpoint() + "/services/async/54.0/job/" + id)
                .method("GET")
                .header("X-SFDC-Session", client.getSession().getAccessToken())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json"));
            return new ResourceRepresentation(res, jsonMapper);
        }
    }
}
