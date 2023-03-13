package software.tnb.ftp.ftp.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Custom "FTP" client which may not actually use the FTP protocol at all
 *
 * This is used because using FTP over port-forwards is unreliable.
 */
public interface CustomFTPClient {
    void storeFile(String fileName, InputStream fileContent) throws IOException;

    void retrieveFile(String fileName, OutputStream local) throws IOException;

    void makeDirectory(String dirName) throws IOException;

    /**
     * Returns names of all files (exclude directories) in the specific directory
     */
    List<String> listFolder(String dirName) throws IOException;
}
