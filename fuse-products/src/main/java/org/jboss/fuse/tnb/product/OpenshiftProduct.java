package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.TimeoutException;

public abstract class OpenshiftProduct extends Product {

    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        createTmpNamespace();//check if temporary namespace should be created
        setupProduct();
        WaitUtils.waitFor(this::isReady, 60, 5000L, "Waiting until the resource is ready");
    }

    public void afterAll(ExtensionContext extensionContext) throws Exception {
        teardownProduct();
        deleteTmpNamespace();//if namespace was temporary, delete it (TODO: check if it wasn't deleted yet)
    }

    public void createTmpNamespace(){
        //new temporary namespace name is generated when OpenshiftConfiguration is firstly loaded
        if (OpenshiftConfiguration.isTemporaryNamespace()) {
            OpenshiftClient.createTemporaryNamespace();
        }
    }

    public void deleteTmpNamespace(){
        if (OpenshiftConfiguration.isTemporaryNamespace()) {
            OpenshiftClient.deleteTemporaryNamespace();
        }
    }
    public abstract boolean isReady();
}
