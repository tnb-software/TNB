package org.jboss.fuse.tnb.product.interfaces;

import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;
import org.jboss.fuse.tnb.product.log.Log;

import java.nio.file.Path;

public interface OpenshiftDeployer {

    OpenshiftDeployer setIntegrationBuilder(AbstractIntegrationBuilder<?> integrationBuilder);

    OpenshiftDeployer setBaseDirectory(Path baseDirectory);

    OpenshiftDeployer setName(String name);

    void deploy();

    void undeploy();

    Log getLog();

    Endpoint getEndpoint();
}
