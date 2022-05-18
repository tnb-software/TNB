package org.jboss.fuse.tnb.product.integration.builder;

import java.util.Optional;

public abstract class AbstractGitIntegrationBuilder<SELF extends AbstractGitIntegrationBuilder<SELF>> extends AbstractIntegrationBuilder<SELF> {
    private String repositoryUrl;
    private String subDirectory;
    private String branch = "main";

    private boolean cleanBeforeBuild = true;

    public AbstractGitIntegrationBuilder(String integrationName) {
        super(integrationName);
    }

    public SELF fromGitRepository(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
        return self();
    }

    public SELF withSubDirectory(String subDirectory) {
        this.subDirectory = subDirectory;
        return self();
    }

    public SELF withBranch(String branch) {
        this.branch = branch;
        return self();
    }

    /**
     * Execute goal `clean` before build application
     * @param cleanBeforeBuild true or false
     * @return self instance
     */
    public SELF cleanBeforeBuild(boolean cleanBeforeBuild) {
        this.cleanBeforeBuild = cleanBeforeBuild;
        return self();
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public Optional<String> getSubDirectory() {
        return Optional.ofNullable(subDirectory);
    }

    public String getBranch() {
        return branch;
    }

    public boolean cleanBeforeBuild() {
        return this.cleanBeforeBuild;
    }
}
