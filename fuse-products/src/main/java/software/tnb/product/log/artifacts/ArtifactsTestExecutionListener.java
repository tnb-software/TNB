package software.tnb.product.log.artifacts;

import software.tnb.common.config.TestConfiguration;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import org.jspecify.annotations.NonNull;

import com.google.auto.service.AutoService;

@AutoService(TestExecutionListener.class)
public class ArtifactsTestExecutionListener implements TestExecutionListener {
    @Override
    public void testPlanExecutionFinished(@NonNull TestPlan testPlan) {
        if (TestConfiguration.artifactsEnabled()) {
            Artifacts.persist();
        }
    }

    @Override
    public void executionStarted(@NonNull TestIdentifier testIdentifier) {
        Artifacts.clear();
    }

    @Override
    public void executionFinished(@NonNull TestIdentifier testIdentifier, @NonNull TestExecutionResult testExecutionResult) {
        if (testExecutionResult != TestExecutionResult.successful()) {
            Artifacts.saveCurrentArtifacts();
        }
    }
}
