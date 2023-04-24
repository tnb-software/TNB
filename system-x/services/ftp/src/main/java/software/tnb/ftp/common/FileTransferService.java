package software.tnb.ftp.common;

public interface FileTransferService {
    String host();

    int port();

    String hostForActiveConnection();

    FileTransferAccount account();

    FileTransferValidation validation();

    String logs();
}
