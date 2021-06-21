package org.jboss.fuse.tnb.iam.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.Role;

public class IAMValidation {
    private static final Logger LOG = LoggerFactory.getLogger(IAMValidation.class);

    private final IamClient client;

    public IAMValidation(IamClient client) {
        this.client = client;
    }

    public String createRole(String name, String description, String rolePolicyDocument) {
        LOG.debug("Creating IAM role {}", name);
        return client.createRole(b -> b.roleName(name)
            .description(description)
            .assumeRolePolicyDocument(rolePolicyDocument)
            .build()
        ).role().arn();
    }

    public boolean roleExists(String name) {
        return getRoleArn(name).isPresent();
    }

    public Optional<String> getRoleArn(String name) {
        for (Role role : client.listRoles().roles()) {
            if (name.equals(role.roleName())) {
                return Optional.of(role.arn());
            }
        }
        return Optional.empty();
    }

    public void deleteRole(String name) {
        LOG.debug("Deleting role {}", name);
        client.deleteRole(b -> b.roleName(name));
    }
}
