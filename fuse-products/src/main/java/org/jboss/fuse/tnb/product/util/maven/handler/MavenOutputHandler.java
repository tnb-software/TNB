package org.jboss.fuse.tnb.product.util.maven.handler;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public interface MavenOutputHandler extends InvocationOutputHandler {
    Reader getOutput();
}
