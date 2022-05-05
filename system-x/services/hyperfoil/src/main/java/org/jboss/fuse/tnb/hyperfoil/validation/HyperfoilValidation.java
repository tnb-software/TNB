package org.jboss.fuse.tnb.hyperfoil.validation;

import org.jboss.fuse.tnb.common.utils.HTTPUtils;
import org.jboss.fuse.tnb.hyperfoil.validation.generated.ApiClient;
import org.jboss.fuse.tnb.hyperfoil.validation.generated.ApiException;
import org.jboss.fuse.tnb.hyperfoil.validation.generated.Configuration;
import org.jboss.fuse.tnb.hyperfoil.validation.generated.api.DefaultApi;
import org.jboss.fuse.tnb.hyperfoil.validation.generated.model.Run;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class HyperfoilValidation {
    private static final Logger LOG = LoggerFactory.getLogger(HyperfoilValidation.class);
    private static final Long WAIT_BENCHMARK_SLEEP_TIME = 1000L;
    private static final ObjectMapper yamlMapper = new YAMLMapper();
    private final DefaultApi defaultApi;

    public HyperfoilValidation(String basePath) {
        ApiClient apiClient = Configuration.getDefaultApiClient();
        apiClient.setBasePath(basePath);
        apiClient.setVerifyingSsl(false);

        defaultApi = new DefaultApi(apiClient);
    }

    /**
     * Load the benchmark hf.yaml on running hyperfoil server, run the benchmark,
     * wait for result and save the report in the target folder
     *
     * @param benchmark - classpath or http/s endpoint
     * @param applicationUnderTestEndpoint
     */
    public void startAndWaitForBenchmark(String benchmark, String applicationUnderTestEndpoint) {
        LOG.info("Add benchmark " + benchmark);
        String benchmarkName = addBenchmark(benchmark, applicationUnderTestEndpoint);

        LOG.info("Run benchmark");
        Run run = runBenchmark(benchmarkName);
        LOG.info("Run started");
        LOG.info(run.toString());

        waitForRun(run);
        LOG.info("Benchmark finished");
        LOG.info(getRun(run.getId()).toString());

        LOG.info("Generating report");
        String report = generateReport(run);
        Path reportFile = saveReportToFile(run, report);
        LOG.info("Report generated " + reportFile.toAbsolutePath());
    }

    private Path saveReportToFile(Run run, String report) {
        try {
            final Path destination = Paths.get("target", run.getBenchmark() + "-" + LocalDateTime.now() + ".html");
            org.jboss.fuse.tnb.common.utils.IOUtils.writeFile(destination, report);
            return destination;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public String generateReport(Run run) {
        try {
            return getDefaultApi().createReport(run.getId(), null);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public String addBenchmark(String benchmarkUri, String applicationUnderTestEndpoint) {
        try {
            String benchmark;
            if (benchmarkUri.startsWith("http:") || benchmarkUri.startsWith("https:")) {
                HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
                    .get(benchmarkUri);
                if (response.getResponseCode() != 200) {
                    throw new RuntimeException("Call to " + benchmarkUri + " failed " + response.getResponseCode() + " " + response.getBody());
                }
                benchmark = response.getBody();
            } else {
                if (!benchmarkUri.startsWith("/")) {
                    benchmarkUri = "/" + benchmarkUri;
                }
                try (InputStream is = this.getClass().getResourceAsStream(benchmarkUri)) {
                    byte[] benchmarkByteArray = IOUtils.toByteArray(is);
                    benchmark = new String(benchmarkByteArray);
                }
            }
            ObjectNode node = yamlMapper.readValue(benchmark, ObjectNode.class);
            // Update http.host with applicationUTEndpoint
            ((ObjectNode) node.get("http")).put("host", applicationUnderTestEndpoint);

            String hyperfoilYaml = yamlMapper.writeValueAsString(node);
            LOG.info("Using the following hyperfoil yaml");
            LOG.info(hyperfoilYaml);

            String name = node.get("name").asText();
            File tempBenchmarkFile = Files.createTempFile(name, ".yaml").toFile();
            yamlMapper.writeValue(tempBenchmarkFile, node);

            getDefaultApi().addBenchmark(null, null, tempBenchmarkFile);

            return name;
        } catch (ApiException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Run getRun(String runId) {
        try {
            return getDefaultApi().getRun(runId);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            return new Run();
        }
    }

    public void waitForRun(Run run) {
        try {
            while (!run.getCompleted()) {
                run = getDefaultApi().getRun(run.getId());
                if (LOG.isTraceEnabled()) {
                    LOG.trace(run.toString());
                }

                Thread.sleep(WAIT_BENCHMARK_SLEEP_TIME);
            }
        } catch (ApiException | InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public List<String> listBenchmarks() throws ApiException {
        return getDefaultApi().listBenchmarks();
    }

    public Run runBenchmark(String benchmarkName) {
        try {
            if (listBenchmarks().size() == 0) {
                throw new RuntimeException("Benchmark not found, execute addBenchmark(...)");
            }

            String benchmark = listBenchmarks().stream()
                .filter(n -> n.equals(benchmarkName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Benchmark with name " + benchmarkName + " does not exists"));

            String triggerJob = System.getenv("BUILD_URL") != null ? System.getenv("BUILD_URL") : null;

            return getDefaultApi().startBenchmark(benchmark, "TNB - " + benchmark, triggerJob, null, null);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public DefaultApi getDefaultApi() {
        return defaultApi;
    }
}
