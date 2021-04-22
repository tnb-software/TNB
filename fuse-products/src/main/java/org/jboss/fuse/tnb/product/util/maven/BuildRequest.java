package org.jboss.fuse.tnb.product.util.maven;

import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.product.util.maven.handler.MavenFileOutputHandler;
import org.jboss.fuse.tnb.product.util.maven.handler.MavenOutputHandler;
import org.jboss.fuse.tnb.product.util.maven.handler.MavenStringOutputHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BuildRequest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH-mm-ss-SSS").withZone(ZoneId.systemDefault());

    private File baseDirectory;
    private List<String> goals = new ArrayList<>();
    private List<String> profiles = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private MavenOutputHandler outputHandler = new MavenStringOutputHandler();
    private Path logFile;

    public File getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public Properties getProperties() {
        return MapUtils.toProperties(properties);
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public MavenOutputHandler getOutputHandler() {
        return outputHandler;
    }

    public void setOutputHandler(MavenOutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }

    public Path getLogFile() {
        return logFile;
    }

    public void setLogFile(Path logFile) {
        this.logFile = logFile;
    }

    public static class Builder {
        private final BuildRequest request;

        public Builder() {
            request = new BuildRequest();
        }

        public Builder withBaseDirectory(File dir) {
            request.setBaseDirectory(dir);
            return this;
        }

        public Builder withBaseDirectory(Path dir) {
            request.setBaseDirectory(dir.toFile());
            return this;
        }

        public Builder withGoals(String... goals) {
            request.setGoals(Arrays.asList(goals));
            return this;
        }

        public Builder withProfiles(String... profiles) {
            request.setProfiles(Arrays.asList(profiles));
            return this;
        }

        public Builder withProperties(Map<String, String> properties) {
            request.setProperties(properties);
            return this;
        }

        public Builder withLogFile(Path logFile) {
            request.setLogFile(logFile);
            return this;
        }

        public BuildRequest build() {
            try {
                request.setOutputHandler(
                    request.getLogFile() == null ? new MavenStringOutputHandler() : new MavenFileOutputHandler(request.getLogFile()));
            } catch (IOException e) {
                throw new RuntimeException("Can't create the log file", e);
            }

            return request;
        }
    }
}
