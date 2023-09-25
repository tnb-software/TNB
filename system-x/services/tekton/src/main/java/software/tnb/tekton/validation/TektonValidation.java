package software.tnb.tekton.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.tekton.client.TektonClient;
import io.fabric8.tekton.pipeline.v1beta1.Pipeline;
import io.fabric8.tekton.pipeline.v1beta1.PipelineRun;
import io.fabric8.tekton.pipeline.v1beta1.Task;
import io.fabric8.tekton.pipeline.v1beta1.TaskRun;

public class TektonValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(TektonValidation.class);
    private final TektonClient client;

    public TektonValidation(TektonClient client) {
        this.client = client;
    }

    public Task createTektonTask(Task task) {
        LOG.info("Creating Tekton task " + task.getMetadata().getName());
        return client.v1beta1().tasks().inNamespace(OpenshiftClient.get().getNamespace()).createOrReplace(task);
    }

    public Task createTektonTask(InputStream isTask) {
        return createTektonTask(client.v1beta1().tasks().load(isTask).get());
    }

    public Pipeline createTektonPipeline(Pipeline pipeline) {
        LOG.info("Creating Tekton pipeline " + pipeline.getMetadata().getName());
        return client.v1beta1().pipelines().inNamespace(OpenshiftClient.get().getNamespace()).createOrReplace(pipeline);
    }

    public Pipeline createTektonPipeline(InputStream isPipeline) {
        return createTektonPipeline(client.v1beta1().pipelines().load(isPipeline).get());
    }

    public PipelineRun createTektonPipelineRun(PipelineRun pipelineRun) {
        LOG.info("Creating Tekton PipelineRun " + pipelineRun.getMetadata().getName());
        return client.v1beta1().pipelineRuns()
            .inNamespace(OpenshiftClient.get().getNamespace()).createOrReplace(pipelineRun);
    }

    public PipelineRun createTektonPipelineRun(InputStream isPipelineRun) {
        return createTektonPipelineRun(client.v1beta1().pipelineRuns().load(isPipelineRun).get());
    }

    public List<Task> getAllTasks() {
        LOG.info("Getting all Tekton tasks");
        return client.v1beta1().tasks().inNamespace(OpenshiftClient.get().getNamespace()).list().getItems();
    }

    public List<Pipeline> getAllPipelines() {
        LOG.info("Getting all Tekton pipelines");
        return client.v1beta1().pipelines().inNamespace(OpenshiftClient.get().getNamespace()).list().getItems();
    }

    public List<PipelineRun> getAllPipelineRuns() {
        LOG.info("Getting all Tekton PipelineRuns");
        return client.v1beta1().pipelineRuns().inNamespace(OpenshiftClient.get().getNamespace()).list().getItems();
    }

    public List<TaskRun> getAllTaskRuns() {
        LOG.info("Getting all Tekton TaskRuns");
        return client.v1beta1().taskRuns().inNamespace(OpenshiftClient.get().getNamespace()).list().getItems();
    }

    public String getTaskRunLog(PipelineRun pipelineRun, Task task) {
        LOG.info("Getting TaskRun log for task: " + task.getMetadata().getName());
        List<TaskRun> taskRuns = client.v1beta1().taskRuns()
            .inNamespace(OpenshiftClient.get().getNamespace())
            .withLabel("tekton.dev/task", task.getMetadata().getName())
            .list()
            .getItems();

        TaskRun taskrun = taskRuns.stream()
            .filter(taskRun -> taskRun.getMetadata().getOwnerReferences().get(0).getName()
                .equals(pipelineRun.getMetadata().getName()))
            .findFirst()
            .get();

        return OpenshiftClient.get().getLogs(OpenshiftClient.get()
            .getPod(taskrun.getMetadata().getName() + "-pod")
        );
    }

    public void waitForTaskRunPodsExecuted(Task task) {
        LOG.info("Waiting for TaskRun pod with name " + task.getMetadata().getName() + " to be executed");
        WaitUtils.waitFor(() -> {
            Optional<TaskRun> taskRun = getAllTaskRuns().stream()
                .filter(tr -> tr.getStatus().getPodName().contains(task.getMetadata().getName()))
                .findFirst();
            if (taskRun.isEmpty()) {
                return false;
            }
            Pod pod = OpenshiftClient.get().pods().withName(taskRun.get().getStatus().getPodName()).get();
            if (pod != null) {
                return pod.getStatus().getPhase().equals("Succeeded");
            }
            return false;
        }, "TaskRun pod is not in Succeeded state");
    }

    public Boolean pipelineRunIsExecuted(PipelineRun pipelineRun) {
        LOG.info("Checking that PipelineRun " + pipelineRun.getMetadata().getName() + " is executed successfully");
        Optional<PipelineRun> pipelineRunOptional = getAllPipelineRuns().stream()
            .filter(p -> Objects.equals(p.getMetadata().getName(), pipelineRun.getMetadata().getName()))
            .findFirst();
        return pipelineRunOptional.isPresent()
            && "Succeeded".equals(pipelineRunOptional.get().getStatus().getConditions().get(0).getReason());
    }
}
