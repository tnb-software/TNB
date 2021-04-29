package org.jboss.fuse.tnb.salesforce.validation;

import org.jboss.fuse.tnb.salesforce.account.SalesforceAccount;
import org.jboss.fuse.tnb.salesforce.dto.Lead;

import org.junit.jupiter.api.Assertions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.force.api.ForceApi;
import com.force.api.QueryResult;

import java.util.Optional;

public class SalesforceValidation {
    private static final Logger LOG = LoggerFactory.getLogger(SalesforceValidation.class);

    private ForceApi client;
    private SalesforceAccount account;

    public SalesforceValidation(ForceApi client, SalesforceAccount account) {
        this.client = client;
        this.account = account;
    }

    public void createNewLead(String firstName, String lastName, String email, String companyName) {
        final Lead lead = new Lead(firstName, lastName, email, companyName);
        String leadId = client.createSObject("lead", lead);
        LOG.debug("Created lead with id " + leadId);
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

    public Optional<Lead> getLeadByEmail(String emailAddress) {
        final QueryResult<Lead> queryResult =
            client.query("SELECT Id,FirstName,LastName,Email,Company FROM lead where Email = '"
                + emailAddress + "'", Lead.class
            );
        return queryResult.getTotalSize() > 0 ? Optional.of(queryResult.getRecords().get(0)) : Optional.empty();
    }
}
