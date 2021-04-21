package org.jboss.fuse.tnb.product.util.maven.handler;

import java.io.Reader;
import java.io.StringReader;

public class MavenStringOutputHandler implements MavenOutputHandler {

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void consumeLine(String s){
        stringBuilder.append(s);
    }

    public Reader getOutput() {
        return new StringReader(stringBuilder.toString());
    }
}
