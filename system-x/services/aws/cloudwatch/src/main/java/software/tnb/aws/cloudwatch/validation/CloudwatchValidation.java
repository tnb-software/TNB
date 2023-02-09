package software.tnb.aws.cloudwatch.validation;

import software.tnb.aws.cloudwatch.validation.model.MetricsRequest;
import software.tnb.common.service.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;

public class CloudwatchValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(CloudwatchValidation.class);

    private final CloudWatchClient client;

    public CloudwatchValidation(CloudWatchClient client) {
        this.client = client;
    }

    public List<MetricDataResult> getMetrics(Consumer<MetricsRequest.MetricsRequestBuilder> requestBuilder) {
        MetricsRequest.MetricsRequestBuilder builder = new MetricsRequest.MetricsRequestBuilder();
        requestBuilder.accept(builder);
        MetricsRequest request = builder.build();
        LOG.debug("Fetching Couldwatch metrics {} from namespace {}", request.metricName(), request.namespace());

        GetMetricDataResponse response = client.getMetricData(b -> b
            .maxDatapoints(request.maxDataPoints())
            .startTime(request.start())
            .endTime(request.end())
            .metricDataQueries(dq -> dq
                .id(request.queryId())
                .returnData(true)
                .metricStat(ms -> ms
                    .stat(request.stat())
                    .period(request.period())
                    .metric(m -> m.metricName(request.metricName()).namespace(request.namespace()))
                )
            )
        );

        return response.hasMetricDataResults() ? response.metricDataResults() : List.of();
    }

    public ListMetricsResponse listMetrics(String namespace, String metricName) {
        LOG.debug("Fetching Couldwatch metric {} from namespace {}", metricName, namespace);
        return client.listMetrics(b -> b.metricName(metricName).namespace(namespace));
    }
}
