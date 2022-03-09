package org.jboss.fuse.tnb.product.git;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.product.integration.builder.AbstractGitIntegrationBuilder;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class GitRepository {
    private static final Logger LOG = LoggerFactory.getLogger(GitRepository.class);
    private final String repositoryUrl;
    private final String branch;

    public GitRepository(String repositoryUrl, String branch) {
        this.repositoryUrl = repositoryUrl;
        this.branch = branch;
        checkout();
    }

    public GitRepository(AbstractGitIntegrationBuilder<?> gitIntegrationBuilder) {
        this(gitIntegrationBuilder.getRepositoryUrl(), gitIntegrationBuilder.getBranch());
    }

    private String getFolder() {
        return repositoryUrl.substring(repositoryUrl.lastIndexOf("/") + 1, repositoryUrl.lastIndexOf(".git"));
    }

    public Path getPath() {
        return TestConfiguration.appLocation().resolve(getFolder());
    }

    private void checkout() {
        File projectDirectory = getPath().toFile();
        if (!projectDirectory.exists()) {
            LOG.info("Check out {} on branch {}", repositoryUrl, branch);

            try (Git ignored = Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(projectDirectory)
                .setBranch(branch)
                .call()) {
            } catch (GitAPIException e) {
                throw new IllegalStateException("Can't clone QS git repository: " + repositoryUrl, e);
            }

            LOG.info("{} checked out in {}", getFolder(), TestConfiguration.appLocation());
        }
    }
}
