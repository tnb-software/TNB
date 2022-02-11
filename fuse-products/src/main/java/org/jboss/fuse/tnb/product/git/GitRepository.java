package org.jboss.fuse.tnb.product.git;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.product.integration.GitIntegrationBuilder;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GitRepository {
    private static final Logger LOG = LoggerFactory.getLogger(GitRepository.class);
    protected final String parentFolderName;
    private final GitIntegrationBuilder gitIntegrationBuilder;

    public GitRepository(GitIntegrationBuilder gitIntegrationBuilder) {
        this.gitIntegrationBuilder = gitIntegrationBuilder;

        parentFolderName = checkout();
    }

    public static String getName(GitIntegrationBuilder gitIntegrationBuilder) {
        return gitIntegrationBuilder.getProjectPath()
            .orElse(getParentFolderName(gitIntegrationBuilder));
    }

    public static String getParentFolderName(GitIntegrationBuilder gitIntegrationBuilder) {
        return gitIntegrationBuilder.getRepositoryUrl()
            .substring(
                gitIntegrationBuilder.getRepositoryUrl().lastIndexOf("/") + 1,
                gitIntegrationBuilder.getRepositoryUrl().lastIndexOf(".git"));
    }

    private String checkout() {
        File projectDirectory = TestConfiguration.appLocation().resolve(getParentFolderName(gitIntegrationBuilder)).toFile();
        if (!projectDirectory.exists()) {
            LOG.info("Check out {} on branch {}", gitIntegrationBuilder.getRepositoryUrl(), gitIntegrationBuilder.getBranch());

            try (Git ignored = Git.cloneRepository()
                .setURI(gitIntegrationBuilder.getRepositoryUrl())
                .setDirectory(projectDirectory)
                .setBranch(gitIntegrationBuilder.getBranch())
                .call()) {
            } catch (GitAPIException e) {
                throw new IllegalStateException("Can't clone QS git repository: " + gitIntegrationBuilder.getRepositoryUrl(), e);
            }

            LOG.info("{} checked out in {}", gitIntegrationBuilder.getIntegrationName(), TestConfiguration.appLocation());
        }

        return getParentFolderName(gitIntegrationBuilder);
    }

    public String getParentFolderName() {
        return parentFolderName;
    }
}
