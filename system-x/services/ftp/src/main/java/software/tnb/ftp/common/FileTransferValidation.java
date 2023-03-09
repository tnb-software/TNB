package software.tnb.ftp.common;

import java.util.List;
import java.util.Map;

public interface FileTransferValidation {
    void createFile(String fileName, String fileContent);

    String downloadFile(String fileName);

    void createDirectory(String dirName);

    /**
     * Get values of all files in the specific folder
     */
    Map<String, String> downloadAllFiles(String dirName);

    /**
     * List all files in the specific folder
     */
    List<String> listAllFiles(String dirName);
}
