package software.tnb.aws.iam.validation;

import software.tnb.common.service.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.Role;

public class IAMValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(IAMValidation.class);

    private final IamClient client;

    public IAMValidation(IamClient client) {
        this.client = client;
    }

    public String createRole(String name, String description, String rolePolicyDocument) {
        if (roleExists(name)) {
            LOG.debug("Role {} already exists, skipping creation.", name);
            return getRoleArn(name).get();
        } else {
            LOG.debug("Creating IAM role {}", name);
            return client.createRole(b -> b.roleName(name)
                .description(description)
                .assumeRolePolicyDocument(rolePolicyDocument)
            ).role().arn();
        }
    }

    public String createPolicy(String name, String policyDocument) {
        return client.createPolicy(b -> b.policyName(name)
            .policyDocument(policyDocument)
        ).policy().arn();
    }

    public void attachPolicy(String role, String policyArn) {
        final Optional<AttachedPolicy> policy = client.listAttachedRolePolicies(b -> b.roleName(role)).attachedPolicies().stream()
            .filter(p -> policyArn.equals(p.policyArn())).findFirst();
        if (policy.isEmpty()) {
            LOG.debug("Attaching policy {} to role {}", policyArn, role);
            client.attachRolePolicy(b -> b.roleName(role).policyArn(policyArn));
        }
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
