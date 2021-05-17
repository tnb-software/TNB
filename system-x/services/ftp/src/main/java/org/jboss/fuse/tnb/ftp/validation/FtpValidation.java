package org.jboss.fuse.tnb.ftp.validation;

import org.jboss.fuse.tnb.ftp.service.CustomFtpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.fail;

public class FtpValidation {

    private final CustomFtpClient client;

    public FtpValidation(CustomFtpClient client) {
        this.client = client;
    }

    public void createFile(String fileName, String fileContent) {
        try {
            client.storeFile(fileName, new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            fail("Validation could not store file in FTP", e);
        }
    }

    public String downloadFile(String fileName) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            client.retrieveFile(fileName, baos);
            return baos.toString();
        } catch (IOException e) {
            return fail("Validation could not download file from FTP", e);
        }
    }

    public void createDirectory(String dirName) {
        try {
            client.makeDirectory(dirName);
        } catch (IOException e) {
            fail("Validation could not create directory in FTP", e);
        }
    }
}
