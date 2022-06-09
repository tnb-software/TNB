package software.tnb.ftp.common;

public interface FileTransferValidation {
    void createFile(String fileName, String fileContent);

    String downloadFile(String fileName);

    void createDirectory(String dirName);
}
