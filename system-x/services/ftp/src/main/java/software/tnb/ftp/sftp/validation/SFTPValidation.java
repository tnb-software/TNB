package software.tnb.ftp.sftp.validation;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.common.utils.IOUtils;
import software.tnb.ftp.common.FileTransferValidation;
import software.tnb.ftp.sftp.account.SFTPAccount;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;

public class SFTPValidation implements FileTransferValidation {

    private final SFTPClient client;
    private final SFTPAccount account;

    public SFTPValidation(SFTPAccount account, SFTPClient client) {
        this.client = client;
        this.account = account;
    }

    @Override
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

    @Override
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

    @Override
    public void createDirectory(String dirName) {
        try {
            client.mkdir(account.baseDir() + "/" + dirName);
        } catch (IOException e) {
            fail("Validation could not create directory in SFTP", e);
        }
    }

    @Override
    public Map<String, String> downloadAllFiles(String dirName) {
        return listAllFiles(dirName).stream()
            .collect(Collectors.toMap(file -> file, file -> this.downloadFile(String.format("%s/%s", dirName, file))));
    }

    @Override
    public List<String> listAllFiles(String dirName) {
        try {
            return client.ls(account.baseDir() + "/" + dirName).stream()
                .filter(RemoteResourceInfo::isRegularFile)
                .map(RemoteResourceInfo::getName)
                .collect(Collectors.toList());
        } catch (IOException e) {
            return fail("Validation could not list directory in SFTP", e);
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
