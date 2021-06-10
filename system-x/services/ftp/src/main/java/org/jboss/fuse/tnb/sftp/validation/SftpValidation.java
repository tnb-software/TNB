package org.jboss.fuse.tnb.sftp.validation;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.sftp.account.SftpAccount;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.schmizz.sshj.sftp.SFTPClient;

public class SftpValidation {

    private final SFTPClient client;
    private final SftpAccount account;

    public SftpValidation(SftpAccount account, SFTPClient client) {
        this.client = client;
        this.account = account;
    }

    public void createFile(String fileName, String fileContent) {
        Path tempFile = createTempFile();
        try {
            IOUtils.writeFile(tempFile, fileContent);
            client.put(tempFile.toString(), getRemoteFileName(fileName));
        } catch (IOException e) {
            fail("Validation could not create file in SFTP", e);
        } finally {
            tempFile.toFile().delete();
        }
    }

    public String downloadFile(String fileName) {
        Path tempFile = createTempFile();
        try {
            client.get(getRemoteFileName(fileName), tempFile.toString());
            return IOUtils.readFile(tempFile);
        } catch (IOException e) {
            return fail("Validation could not download file from SFTP", e);
        } finally {
            tempFile.toFile().delete();
        }
    }

    public void createDirectory(String dirName) {
        try {
            client.mkdir(account.baseDir() + "/" + dirName);
        } catch (IOException e) {
            fail("Validation could not create directory in SFTP", e);
        }
    }

    private String getRemoteFileName(String fileName) {
        // the root dir of remote is owned by root, we create a subdir owned by us during container startup, prepend that path here
        return account.baseDir() + "/" + fileName;
    }

    private Path createTempFile() {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(null, null);
        } catch (IOException e) {
            fail("Validation could not create temp file", e);
        }
        return tempFile;
    }
}
