package software.tnb.util.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.utils.cli.CommandLineException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMavenInvoker implements Invoker {
    private final List<InvocationRequest> requests = new ArrayList<>();
    private final List<Runnable> mocks = new ArrayList<>();

    public List<InvocationRequest> getRequests() {
        return requests;
    }

    public void clearRequests() {
        requests.clear();
    }

    public void mockExecution(Runnable... runnables) {
        mocks.addAll(new ArrayList<>(Arrays.asList(runnables)));
    }

    @Override
    public InvocationResult execute(InvocationRequest request) throws MavenInvocationException {
        requests.add(request);
        mocks.forEach(Runnable::run);
        mocks.clear();
        return new InvocationResult() {
            @Override
            public CommandLineException getExecutionException() {
                return null;
            }

            @Override
            public int getExitCode() {
                return 0;
            }
        };
    }

    @Override
    public File getLocalRepositoryDirectory() {
        return null;
    }

    @Override
    public File getWorkingDirectory() {
        return null;
    }

    @Override
    public InvokerLogger getLogger() {
        return null;
    }

    @Override
    public File getMavenHome() {
        return null;
    }

    @Override
    public Invoker setMavenHome(File mavenHome) {
        return null;
    }

    @Override
    public File getMavenExecutable() {
        return null;
    }

    @Override
    public Invoker setMavenExecutable(File mavenExecutable) {
        return null;
    }

    @Override
    public Invoker setLocalRepositoryDirectory(File localRepositoryDirectory) {
        return null;
    }

    @Override
    public Invoker setLogger(InvokerLogger logger) {
        return null;
    }

    @Override
    public Invoker setWorkingDirectory(File workingDirectory) {
        return null;
    }

    @Override
    public Invoker setInputStream(InputStream inputStream) {
        return null;
    }

    @Override
    public Invoker setOutputHandler(InvocationOutputHandler outputHandler) {
        return null;
    }

    @Override
    public Invoker setErrorHandler(InvocationOutputHandler errorHandler) {
        return null;
    }
}
