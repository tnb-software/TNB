package software.tnb.hyperfoil.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.hyperfoil.validation.generated.ApiClient;
import software.tnb.hyperfoil.validation.generated.ApiException;
import software.tnb.hyperfoil.validation.generated.Configuration;
import software.tnb.hyperfoil.validation.generated.api.DefaultApi;
import software.tnb.hyperfoil.validation.generated.model.RequestStatisticsResponse;
import software.tnb.hyperfoil.validation.generated.model.Run;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HyperfoilValidation {
    private static final Logger LOG = LoggerFactory.getLogger(HyperfoilValidation.class);
    private static final Long WAIT_BENCHMARK_SLEEP_TIME = 10000L;
    private static final ObjectMapper yamlMapper = new YAMLMapper();
    private final DefaultApi defaultApi;

    public HyperfoilValidation(String basePath) {
        ApiClient apiClient = Configuration.getDefaultApiClient();
        apiClient.setBasePath(basePath);
        apiClient.setVerifyingSsl(false);
        defaultApi = new DefaultApi(apiClient);
    }

    private TestResult doStartAndWaitForBenchmark(Run run) {
        Run finalRun = waitForRun(run);
        LOG.info("Benchmark finished");

        LOG.info(getRun(run.getId()).toString());

        LOG.info("Generating report");
        String report = generateReport(run);
        Path reportFile = saveReportToFile(run, report);
        LOG.info("Report generated " + reportFile.toAbsolutePath());
        if (finalRun == null) {
            throw new IllegalStateException("Unexpected error, probably the hyperfoil test, failed");
        }
        try {
            RequestStatisticsResponse totalStats = getDefaultApi().getTotalStats(finalRun.getId());
            return new TestResult(finalRun, totalStats);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Load the benchmark hf.yaml on running hyperfoil server, run the benchmark,
     * wait for result and save the report in the target folder
     *
     * @param benchmark - classpath or http/s endpoint
     */
    public void startAndWaitForBenchmark(String benchmark) {
        startAndWaitForBenchmark(benchmark, null);
    }

    /**
     * Load the benchmark hf.yaml on running hyperfoil server, run the benchmark,
     * wait for result and save the report in the target folder
     *
     * @param benchmark - classpath or http/s endpoint
     * @param applicationUnderTestEndpoint
     * @return an object holding the startTime and endTime of the test
     */
    public TestResult startAndWaitForBenchmark(String benchmark, String applicationUnderTestEndpoint) {
        LOG.info("Add benchmark " + benchmark);
        String benchmarkName = addBenchmark(benchmark, applicationUnderTestEndpoint);

        LOG.info("Run benchmark");
        Run run = runBenchmark(benchmarkName);
        LOG.info("Run started");
        LOG.info(run.toString());
        return doStartAndWaitForBenchmark(run);
    }

    /**
     * Load the benchmark hf.yaml on running hyperfoil server, run the benchmark,
     * wait for result and save the report in the target folder
     *
     * @param benchmark classpath or http/s endpoint of a benchmark yaml or template
     * @param parameters if not null the parameters will be supplied to the template
     * @return an object holding the startTime and endTime of the test
     */
    public TestResult startAndWaitForBenchmarkTemplate(String benchmark, Map<String, ?> parameters) {
        LOG.info("Add benchmark " + benchmark);
        String benchmarkName = addBenchmark(benchmark);

        LOG.info("Run benchmark");
        Run run = runBenchmark(benchmarkName, parameters);
        LOG.info("Run started");
        LOG.info(run.toString());
        return doStartAndWaitForBenchmark(run);
    }

    private Path saveReportToFile(Run run, String report) {
        try {
            final Path destination = Paths.get("target", run.getBenchmark() + "-" + LocalDateTime.now() + ".html");
            software.tnb.common.utils.IOUtils.writeFile(destination, report);
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

    private String retrieveBenchmarkFile(String benchmarkUri) throws IOException {
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
        if (benchmark == null) {
            throw new IllegalStateException("Benchmark file at " + benchmarkUri + " not found");
        }
        return benchmark;
    }

    /**
     * the benchmark located at the benchmarkUri value could be a simple yaml benchmark or a template
     *
     * @param benchmarkUri
     * @return
     */
    public String addBenchmark(String benchmarkUri) {
        try {
            String benchmark = retrieveBenchmarkFile(benchmarkUri);
            Pattern pattern = Pattern.compile("^name:[ ]*(.*)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(benchmark);
            if (!matcher.find()) {
                throw new IllegalArgumentException("the benchmark file don't contain the field 'name'");
            }
            String name = matcher.group(1);
            LOG.info("Using benchmark with name: " + name);
            File tempBenchmarkFile = Files.createTempFile(name, ".yaml").toFile();
            Files.writeString(tempBenchmarkFile.toPath(), benchmark);
            getDefaultApi().addBenchmark(null, null, tempBenchmarkFile);
            return name;
        } catch (ApiException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * the benchmark located at the benchmarkUri value is expected to be a yaml benchmark with a placeholder for the target application host value
     *
     * @param benchmarkUri
     * @param applicationUnderTestEndpoint
     * @return
     */
    public String addBenchmark(String benchmarkUri, String applicationUnderTestEndpoint) {
        try {
            String benchmark = retrieveBenchmarkFile(benchmarkUri);
            ObjectNode node = yamlMapper.readValue(benchmark, ObjectNode.class);

            if (Objects.nonNull(applicationUnderTestEndpoint)) {
                // Update http.host with applicationUTEndpoint
                JsonNode httpNode = node.get("http");
                if (httpNode.isObject()) {
                    ((ObjectNode) httpNode).put("host", applicationUnderTestEndpoint);
                } else if (httpNode.isArray()) {
                    throw new RuntimeException("Array node detected for http.host, impossible to distinguish which host should be replaced");
                }
            }

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

    public Run waitForRun(Run run) {
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
        return run;
    }

    public List<String> listBenchmarks() throws ApiException {
        return getDefaultApi().listBenchmarks();
    }

    public Run runBenchmark(String benchmarkName) {
        return runBenchmark(benchmarkName, null);
    }

    public Run runBenchmark(String benchmarkName, Map<String, ?> parameters) {
        try {
            // benchmark defined as template are not being got back by listBenchmark operation
            if (parameters == null && !listBenchmarks().contains(benchmarkName)) {
                throw new RuntimeException("Benchmark with name " + benchmarkName + " does not exists");
            }
            String triggerJob = System.getenv("BUILD_URL") != null ? System.getenv("BUILD_URL") : null;

            List<String> params = null;
            if (parameters != null && parameters.size() > 0) {
                params = parameters.entrySet().stream().map(en -> String.format("%s=%s", en.getKey(), en.getValue().toString()))
                    .collect(Collectors.toList());
            }

            return getDefaultApi().startBenchmark(benchmarkName, "TNB - " + benchmarkName, triggerJob, null, params);
        } catch (ApiException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public DefaultApi getDefaultApi() {
        return defaultApi;
    }
}
