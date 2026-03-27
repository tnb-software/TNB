package software.tnb.filesystem.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class FileSystemConfiguration extends ServiceConfiguration {
    private static final String APP_NAME = "fs.app.name";

    public FileSystemConfiguration applicationName(String appName) {
        set(APP_NAME, appName);
        return this;
    }

    public String getApplicationName() {
        return get(APP_NAME, String.class);
    }
}
