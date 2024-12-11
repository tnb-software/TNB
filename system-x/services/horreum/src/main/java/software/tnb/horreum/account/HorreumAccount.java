package software.tnb.horreum.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Requires following Horreum account definition in credentials file:
 *
 *   horreum:
 *     credentials:
 *       users:
 *         [username]:
 *           apiKey: [apiKey]
 *         [username]:
 *           apiKey: [apiKey]
 *         ...
 */
public class HorreumAccount implements Account, WithId {
    private static final Logger LOG = LoggerFactory.getLogger(HorreumAccount.class);

    private Map<String, HorreumDataSetAccount> users;

    public void setUsers(Map<String, HorreumDataSetAccount> users) {
        this.users = users;
    }

    @Override
    public String credentialsId() {
        return "horreum";
    }

    public String apiKey(String userName) {
        return getUser(userName).apiKey();
    }

    public String user(String userName) {
        HorreumDataSetAccount account = getUser(userName);
        return account.userName() == null ? userName : account.userName();
    }

    private HorreumDataSetAccount getUser(String userName) {
        HorreumDataSetAccount account = users.get(userName);
        if (account == null) {
            throw new IllegalArgumentException(String.format("Secret for user %s not found", userName));
        }
        LOG.info("Using secret for user: " + userName);
        return account;
    }

    static class HorreumDataSetAccount {
        private String userName;

        private String apiKey;

        public String userName() {
            return userName;
        }

        public void setUser(String userName) {
            this.userName = userName;
        }

        public String apiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }
}
