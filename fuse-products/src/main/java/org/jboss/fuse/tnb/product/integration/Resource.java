package org.jboss.fuse.tnb.product.integration;

public class Resource {
    private ResourceType type;
    private String name;
    private String content;

    public Resource(ResourceType type, String name, String content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
