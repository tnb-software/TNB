package software.tnb.ftp.ftp.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.ftp.common.FileTransferService;
import software.tnb.ftp.ftp.account.FTPAccount;
import software.tnb.ftp.ftp.validation.FTPValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class FTP extends Service<FTPAccount, CustomFTPClient, FTPValidation> implements FileTransferService, WithDockerImage {

    private static final Logger LOG = LoggerFactory.getLogger(FTP.class);

    @Override
    public FTPValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Ftp validation");
            validation = new FTPValidation(client());
        }
        return validation;
    }

    protected Map<String, String> containerEnvironment() {
        return Map.of(
            "FTP_USERNAME", account().username(),
            "FTP_PASSWORD", account().password(),
            "USERS", String.format("%s|%s", account().username(), account().password())
        );
    }

    protected List<Integer> containerPorts() {
        return IntStream.range(2121, 2131).boxed().collect(Collectors.toList());
    }

    protected String basePath() {
        // base path in the container to put files on (because we use out of band transfer)
        return "/tmp/" + account().username();
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/apache-ftp:latest";
    }

    public int port() {
        return 2121;
    }
}
