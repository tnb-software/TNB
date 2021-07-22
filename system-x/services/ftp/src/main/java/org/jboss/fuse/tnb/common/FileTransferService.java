package org.jboss.fuse.tnb.common;

import org.jboss.fuse.tnb.common.service.Service;

public interface FileTransferService extends Service {
    String host();

    int port();

    FileTransferAccount account();

    FileTransferValidation validation();
}
