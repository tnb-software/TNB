package org.jboss.fuse.tnb.product.interfaces;

import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.log.Log;

import java.nio.file.Path;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

public interface OpenshiftDeployer {

    OpenshiftDeployer setIntegrationBuilder(AbstractIntegrationBuilder<?> integrationBuilder);

    OpenshiftDeployer setBaseDirectory(Path baseDirectory);

    OpenshiftDeployer setName(String name);

    void deploy();

    void undeploy();

    Log getLog();

    Endpoint getEndpoint();

    Predicate<Pod> podSelector();
}
