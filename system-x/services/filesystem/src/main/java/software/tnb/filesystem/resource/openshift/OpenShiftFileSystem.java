package software.tnb.filesystem.resource.openshift;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.filesystem.service.FileSystem;
import software.tnb.filesystem.validation.FileSystemValidation;
import software.tnb.filesystem.validation.OpenshiftFileSystemValidation;

import com.google.auto.service.AutoService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.Pod;

@AutoService(FileSystem.class)
public class OpenShiftFileSystem extends FileSystem implements OpenshiftDeployable {
    public FileSystemValidation validation() {
        if (validation == null) {
            validation = new OpenshiftFileSystemValidation(getConfiguration().getApplicationName());
        }
        return validation;
    }

    @Override
    public Path root() {
        return Paths.get("/tmp", "tnb-filesystem");
    }

    @Override
    public void create() {
    }

    @Override
    public boolean isDeployed() {
        return true;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return null;
    }
}
