package software.tnb.filesystem.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.filesystem.service.configuration.FileSystemConfiguration;
import software.tnb.filesystem.validation.FileSystemValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class FileSystem extends ConfigurableService<NoAccount, NoClient, FileSystemValidation, FileSystemConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystem.class);

    public void deploy() {
    }

    public void undeploy() {
    }

    public void openResources() {
    }

    public void closeResources() {
    }

    public abstract Path root();

    public void setApplicationName(String name) {
        getConfiguration().applicationName(name);
        // invalidate validation as openshift filesystem requires the name
        validation = null;
    }

    @Override
    protected void defaultConfiguration() {
    }

    public String getLogs() {
        LOG.warn("getLogs method not supported in FileSystem, returning an empty string");
        return "";
    }
}
