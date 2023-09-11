package software.tnb.ldap.validation;

import software.tnb.common.validation.Validation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SimpleBindRequest;

import java.util.List;

public class LDAPValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(LDAPValidation.class);
    private LDAPConnectionPool ldapConnectionPool;

    public LDAPValidation(LDAPConnectionPool ldapConnectionPool) {
        this.ldapConnectionPool = ldapConnectionPool;
    }

    public List<SearchResultEntry> getEntries(String filter) {
        try {
            final String baseDN = StringUtils.substringAfter(((SimpleBindRequest) ldapConnectionPool.getConnection().getLastBindRequest())
                .getBindDN(), ",");
            SearchRequest searchRequest = new SearchRequest(baseDN, SearchScope.SUB, filter);
            SearchResult searchResult = ldapConnectionPool.search(searchRequest);
            return searchResult.getSearchEntries();
        } catch (LDAPException e) {
            LOG.error("Error when searching for entry");
            throw new RuntimeException("Error when searching for entry");
        }
    }
}
