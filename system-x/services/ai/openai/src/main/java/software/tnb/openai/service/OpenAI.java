package software.tnb.openai.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.openai.account.OpenAIAccount;
import software.tnb.openai.validation.OpenAIValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(OpenAI.class)
public class OpenAI extends Service<OpenAIAccount, NoClient, OpenAIValidation> {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }

    public OpenAIValidation validation() {
        if (validation == null) {
            validation = new OpenAIValidation(account().chat(), account().embedding());
        }
        return validation;
    }
}
