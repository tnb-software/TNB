package software.tnb.mllp;

import org.apache.camel.test.junit.rule.mllp.MllpServerResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public final class MllpServerRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MllpServerRunner.class);

    private MllpServerRunner() {
    }

    public static void main(String[] args) {
        final MllpServerResource mllpServer = args.length > 0 && StringUtils.isNumeric(args[0])
            ? new MllpServerResource(Integer.parseInt(args[0])) : new MllpServerResource();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> mllpServer.shutdown()));
            mllpServer.startup();
            LOG.info("listening on {}:{}", Optional.ofNullable(mllpServer.getListenHost()).orElse("0.0.0.0"), mllpServer.getListenPort());
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
