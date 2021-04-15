package org.jboss.fuse.tnb.product.util;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

import java.io.IOException;

public class MavenOutputHandler implements InvocationOutputHandler {
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void consumeLine(String s) throws IOException {
        stringBuilder.append(s).append("\n");
    }

    public String getOutput() {
        return stringBuilder.toString();
    }
}
