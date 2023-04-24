package software.tnb.google.api.common.service;

import software.tnb.common.service.Service;
import software.tnb.common.utils.FIPSUtils;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.google.api.common.account.GoogleAPIAccount;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;

import java.io.IOException;

public abstract class GoogleAPIService<C, V extends Validation> extends Service<GoogleAPIAccount, C, V> {
    protected static final Logger LOG = LoggerFactory.getLogger(GoogleAPIService.class);
    protected static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    protected final NetHttpTransport httpTransport;

    protected GoogleAPIService() {
        try {
            if (FIPSUtils.isFipsEnabled()) {
                httpTransport = new NetHttpTransport.Builder()
                    .setSslSocketFactory(HTTPUtils.getSslContext().getSocketFactory()).build();
            } else {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't create http transport", e);
        }
    }

    protected HttpCredentialsAdapter createCredentials() {
        GenericJson json = new GenericJson();
        json.put("type", "authorized_user");
        json.put("refresh_token", account().refreshToken());
        json.put("client_id", account().clientId());
        json.put("client_secret", account().clientSecret());
        json.setFactory(JSON_FACTORY);

        try {
            return new HttpCredentialsAdapter(UserCredentials.fromStream(IOUtils.toInputStream(json.toPrettyString(), "UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create credentials", e);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }
}
