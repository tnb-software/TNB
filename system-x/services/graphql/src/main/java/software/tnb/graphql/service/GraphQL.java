package software.tnb.graphql.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.graphql.validation.GraphQLValidation;

public abstract class GraphQL extends Service<NoAccount, NoClient, GraphQLValidation> implements WithDockerImage {

    protected static final int PORT = 8080;

    public abstract String host();

    public abstract int port();

    public GraphQLValidation validation() {
        if (validation == null) {
            validation = new GraphQLValidation(host(), port());
        }
        return validation;
    }

    public String defaultImage() {
        return "dgraph/standalone:latest";
    }

    public void openResources() {

    }

    public void closeResources() {

    }
}
