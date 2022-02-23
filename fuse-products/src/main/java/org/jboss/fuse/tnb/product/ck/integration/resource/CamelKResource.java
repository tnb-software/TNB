package org.jboss.fuse.tnb.product.ck.integration.resource;

import org.jboss.fuse.tnb.product.integration.Resource;

public class CamelKResource extends Resource {
    private ResourceType type;

    public CamelKResource(ResourceType type, String name, String content) {
        super(name, content);
        this.type = type;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }
}
