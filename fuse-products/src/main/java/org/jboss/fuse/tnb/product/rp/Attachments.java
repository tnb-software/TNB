package org.jboss.fuse.tnb.product.rp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Attachments {

    private static final Logger LOG = LoggerFactory.getLogger(Attachments.class);
    private static final List<Path> attachments = new ArrayList<>();
    private static String currentTestCase;
    private static String currentTestClass;

    private Attachments() {
    }

    static void startTestClass(String testClass) {
        currentTestClass = testClass;
    }

    static void startTestCase(String testCase) {
        currentTestCase = testCase;
    }

    static void endTestCase(boolean failure) {
        if (failure) {
            createAttachments();
        }
        attachments.clear();
    }

    private static void createAttachments() {
        try {
            final Path testCaseDir = Path.of("target", "attachments", currentTestClass + "." + currentTestCase);
            Files.createDirectories(testCaseDir);

            for (Path p : attachments) {
                Files.copy(p, testCaseDir.resolve(p.getFileName()));
            }
        } catch (IOException e) {
            LOG.error("Couldn't create an attachment for test case {}#{}", currentTestClass, currentTestCase, e);
        }
    }

    public static void addAttachment(Path path) {
        attachments.add(path);
    }
}
