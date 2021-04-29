package org.jboss.fuse.tnb.sftp.resource.local;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class SftpContainer extends GenericContainer<SftpContainer> {

    public SftpContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        waitingFor(Wait.forLogMessage(".*Server listening on.*", 1));
    }
}
