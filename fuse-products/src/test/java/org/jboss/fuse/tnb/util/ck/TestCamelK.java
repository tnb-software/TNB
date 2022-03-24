package org.jboss.fuse.tnb.util.ck;

import org.jboss.fuse.tnb.product.ck.CamelK;

import io.fabric8.camelk.client.CamelKClient;

public class TestCamelK extends CamelK {
    public void setClient(CamelKClient client) {
        this.camelKClient = client;
    }
}
