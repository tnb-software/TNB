package software.tnb.product.interfaces;

import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.log.Log;

import java.nio.file.Path;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

public interface OpenshiftDeployer {

    OpenshiftDeployer setIntegrationBuilder(AbstractIntegrationBuilder<?> integrationBuilder);

    OpenshiftDeployer setBaseDirectory(Path baseDirectory);

    OpenshiftDeployer setName(String name);

    void deploy();

    void undeploy();

    Log getLog(Path logFile);

    Endpoint getEndpoint();

    Predicate<Pod> podSelector();

    boolean isFailed();
}
