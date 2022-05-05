package org.jboss.fuse.tnb.cryostat.service;

public abstract class BaseCryostatClient implements CryostatClient {

    private String jfrTemplate;

    public String getJfrTemplate() {
        return jfrTemplate != null ? jfrTemplate : CryostatClient.StandardJfrTemplates.ALL.name();
    }

    @Override
    public void setJfrTemplate(final String jfrTemplate) {
        this.jfrTemplate = jfrTemplate;
    }
}
