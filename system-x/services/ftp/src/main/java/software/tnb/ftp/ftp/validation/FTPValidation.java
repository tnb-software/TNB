package software.tnb.ftp.ftp.validation;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.ftp.common.FileTransferValidation;
import software.tnb.ftp.ftp.service.CustomFTPClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FTPValidation implements FileTransferValidation {

    private final CustomFTPClient client;

    public FTPValidation(CustomFTPClient client) {
        this.client = client;
    }

    @Override
    public void createFile(String fileName, String fileContent) {
        try {
            client.storeFile(fileName, new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            fail("Validation could not store file in FTP", e);
        }
    }

    @Override
    public String downloadFile(String fileName) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            client.retrieveFile(fileName, baos);
            return baos.toString();
        } catch (IOException e) {
            return fail("Validation could not download file from FTP", e);
        }
    }

    @Override
    public void createDirectory(String dirName) {
        try {
            client.makeDirectory(dirName);
        } catch (IOException e) {
            fail("Validation could not create directory in FTP", e);
        }
    }
}
