package software.tnb.webhook.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import java.util.Map;

/**
 * Expects following Webhook.site definition:
 *
 *   webhook-site:
 *     credentials:
 *       tokens:
 *         [name]:
 *           token: [token]
 *         ...
 */
public class WebhookAccount implements Account, WithId {
    private Map<String, Token> tokens;

    @Override
    public String credentialsId() {
        return "webhook-site";
    }

    public String token(String tokenName) {
        Token token = tokens.get(tokenName);
        if (token == null) {
            throw new IllegalArgumentException("Unknown token " + tokenName);
        }
        return token.token;
    }
    
    public void setTokens(Map<String, Token> tokens) {
        this.tokens = tokens;
    }

    public static class Token {
        private String token;

        public String token() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
