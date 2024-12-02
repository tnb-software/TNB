package software.tnb.samba.validation;

import software.tnb.common.validation.Validation;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;

public class SambaValidation implements Validation {

    private final Session session;
    private final String shareName;

    public SambaValidation(String shareName, Session session) {
        this.shareName = shareName;
        this.session = session;
    }

    public void create(File file, String content) {
        try {
            DiskShare share = (DiskShare) session.connectShare(this.shareName);

            mkdirs(share, file.getParent());

            write(share, file.getPath(), content);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create Samba file", e);
        }
    }

    public String read(File file) {
        try {
            DiskShare share = (DiskShare) session.connectShare(this.shareName);
            return read(share, file.getPath());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Samba file", e);
        }
    }

    private String read(DiskShare share, String filePath) throws IOException {
        if (!share.fileExists(filePath)) {
            throw new IllegalStateException("File not found: " + filePath);
        }

        try (com.hierynomus.smbj.share.File file = share.openFile(
            filePath,
            EnumSet.of(AccessMask.GENERIC_READ),
            EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
            EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ),
            SMB2CreateDisposition.FILE_OPEN,
            EnumSet.noneOf(SMB2CreateOptions.class)
        ); InputStream inputStream = file.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void write(DiskShare share, String fileName, String content) throws IOException {
        if (!share.fileExists(fileName)) {
            try (com.hierynomus.smbj.share.File file = share.openFile(
                fileName,
                EnumSet.of(AccessMask.GENERIC_WRITE),
                EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_CREATE,
                EnumSet.of(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE)
            )) {
                try (OutputStream outputStream = file.getOutputStream()) {
                    outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                }
            }
        } else {
            throw new IllegalStateException("File " + fileName + " already exists in share " + share);
        }
    }

    private void mkdirs(DiskShare share, String parentPath) {
        if (parentPath == null || parentPath.isEmpty()) {
            return;
        }

        String[] pathParts = parentPath.split("[/\\\\]");
        String currentPath = "";

        for (String part : pathParts) {
            if (part.isEmpty()) {
                continue;
            }

            currentPath = currentPath.isEmpty() ? part : currentPath + "/" + part;

            if (!share.folderExists(currentPath)) {
                share.mkdir(currentPath);
            }
        }
    }
}
