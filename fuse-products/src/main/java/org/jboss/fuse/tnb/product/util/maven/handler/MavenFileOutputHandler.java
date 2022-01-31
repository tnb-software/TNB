package org.jboss.fuse.tnb.product.util.maven.handler;

import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.rp.Attachments;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class MavenFileOutputHandler implements MavenOutputHandler, Closeable {

    private final PrintWriter in;
    private final Path file;

    public MavenFileOutputHandler(Path outputFile) throws IOException {
        this.in = new PrintWriter(new FileWriter(outputFile.toFile()), true);
        this.file = outputFile;
        Attachments.addAttachment(file);
    }

    @Override
    public void consumeLine(String s) throws IOException {
        in.println(s);
    }

    @Override
    public String getOutput() {
        return IOUtils.readFile(file);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
