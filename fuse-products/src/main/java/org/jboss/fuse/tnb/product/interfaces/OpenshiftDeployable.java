package org.jboss.fuse.tnb.product.interfaces;

import org.jboss.fuse.tnb.product.log.Log;

public interface OpenshiftDeployable {

    void deploy(String name);

    void undeploy(String name);

    Log getLog(String app);
}
