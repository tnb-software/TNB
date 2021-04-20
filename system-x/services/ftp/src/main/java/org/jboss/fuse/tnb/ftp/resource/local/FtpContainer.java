package org.jboss.fuse.tnb.ftp.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class FtpContainer extends GenericContainer<FtpContainer> {

    public FtpContainer(String image, int port, Map<String, String> env) {
        super(image);
        withEnv(env);
        waitingFor(Wait.forLogMessage(".*event=Starting.*", 1));
    }
}
