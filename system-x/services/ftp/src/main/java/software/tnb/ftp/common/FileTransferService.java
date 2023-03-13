package software.tnb.ftp.common;

import software.tnb.common.service.Service;

public interface FileTransferService extends Service {
    String host();

    int port();

    String hostForActiveConnection();

    FileTransferAccount account();

    FileTransferValidation validation();

    String logs();
}
