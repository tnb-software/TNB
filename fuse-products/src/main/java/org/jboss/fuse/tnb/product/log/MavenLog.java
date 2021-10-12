package org.jboss.fuse.tnb.product.log;

import org.jboss.fuse.tnb.common.utils.StringUtils;
import org.jboss.fuse.tnb.product.util.maven.handler.MavenOutputHandler;

public class MavenLog extends Log {
    private final MavenOutputHandler handler;

    public MavenLog(MavenOutputHandler handler) {
        this.handler = handler;
    }

    @Override
    public String toString() {
        return StringUtils.removeColorCodes(handler.getOutput());
    }
}
