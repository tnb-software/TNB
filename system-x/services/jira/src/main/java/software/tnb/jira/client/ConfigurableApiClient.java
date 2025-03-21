package software.tnb.jira.client;

import software.tnb.jira.validation.generated.ApiClient;
import software.tnb.jira.validation.generated.model.Project;

public class ConfigurableApiClient extends ApiClient {

    public ConfigurableApiClient() {
        super();
        configureAdditionalFields();
    }

    /**
     * The fields coming from REST API response, but not in the Java models, without this, the validation fails
     * even if the gson is lenient, since the validation is performed after the deserialization
     */
    private void configureAdditionalFields() {
        Project.openapiFields.add("entityId");
    }
}
