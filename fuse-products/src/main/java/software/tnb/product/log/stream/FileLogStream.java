package software.tnb.product.log.stream;

import software.tnb.common.config.TestConfiguration;
import software.tnb.product.util.executor.Executor;

import org.apache.commons.io.input.Tailer;

import java.nio.file.Path;

public class FileLogStream implements LogStream {
    private Tailer tailer;

    public FileLogStream(Path file, String marker) {
        if (TestConfiguration.streamLogs()) {
            tailer = new Tailer(file.toFile(), new FileTailer(marker), 50);
            Executor.get().submit(tailer);
        }
    }

    @Override
    public void stop() {
        if (tailer != null) {
            tailer.stop();
        }
    }
}
