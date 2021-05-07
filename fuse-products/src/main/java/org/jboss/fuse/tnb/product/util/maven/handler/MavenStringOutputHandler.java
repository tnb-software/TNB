package org.jboss.fuse.tnb.product.util.maven.handler;

public class MavenStringOutputHandler implements MavenOutputHandler {

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void consumeLine(String s) {
        stringBuilder.append(s);
    }

    public String getOutput() {
        return stringBuilder.toString();
    }
}
