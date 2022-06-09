package software.tnb.product.util.maven.handler;

import org.apache.maven.shared.invoker.InvocationOutputHandler;

public interface MavenOutputHandler extends InvocationOutputHandler {
    String getOutput();
}
