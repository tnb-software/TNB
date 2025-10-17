package software.tnb.observability.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class ObservabilityConfiguration extends ServiceConfiguration {

    private static final String DEPLOY_TRACES_UI_PLUGIN = "observability.plugin.traces.deploy";
    private static final String TEMPOSTACK_NAME = "observability.tempo.stack.name";

    public Boolean isDeployTracesUiPlugin() {
        return get(DEPLOY_TRACES_UI_PLUGIN, Boolean.class);
    }

    public ObservabilityConfiguration withDeployTracesUiPlugin(Boolean deployTracesUiPlugin) {
        set(DEPLOY_TRACES_UI_PLUGIN, deployTracesUiPlugin);
        return this;
    }

    public String getTempoStackName() {
        return get(TEMPOSTACK_NAME, String.class);
    }

    public ObservabilityConfiguration withTempoStackName(String tempoStackName) {
        set(TEMPOSTACK_NAME, tempoStackName);
        return this;
    }

}
