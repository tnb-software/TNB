package org.jboss.fuse.tnb.product.log.stream;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class FileTailer implements TailerListener {
    private static final Logger LOG = LoggerFactory.getLogger(FileTailer.class);
    private final Marker logMarker;

    public FileTailer(String marker) {
        logMarker = MarkerFactory.getMarker(marker);
    }

    @Override
    public void init(Tailer tailer) {
    }

    @Override
    public void fileNotFound() {
    }

    @Override
    public void fileRotated() {
    }

    @Override
    public void handle(String line) {
        LOG.info(logMarker, line);
    }

    @Override
    public void handle(Exception ex) {
    }
}
