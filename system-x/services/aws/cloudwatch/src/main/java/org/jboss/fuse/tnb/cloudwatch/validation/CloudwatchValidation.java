package org.jboss.fuse.tnb.cloudwatch.validation;

import org.jboss.fuse.tnb.aws.account.AWSAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;

public class CloudwatchValidation {
    private static final Logger LOG = LoggerFactory.getLogger(CloudwatchValidation.class);

    private final CloudWatchClient client;
    private final AWSAccount account;

    public CloudwatchValidation(CloudWatchClient client, AWSAccount account) {
        this.client = client;
        this.account = account;
    }

    public List<MetricDataResult> getMetrics(String namespace, String metricName, Instant start) {
        LOG.debug("Fetching Couldwatch metrics {} from namespace {}", metricName, namespace);
        List<MetricDataResult> data = new ArrayList<>();
        Instant endDate = Instant.now();

        Metric met = Metric.builder()
            .metricName(metricName)
            .namespace(namespace)
            .build();

        MetricStat metStat = MetricStat.builder()
            .stat("Minimum")
            .period(60)
            .metric(met)
            .build();

        MetricDataQuery dataQUery = MetricDataQuery.builder()
            .metricStat(metStat)
            .id("foo2")
            .returnData(true)
            .build();

        List<MetricDataQuery> dq = new ArrayList<>();
        dq.add(dataQUery);

        GetMetricDataRequest getMetReq = GetMetricDataRequest.builder()
            .maxDatapoints(100)
            .startTime(start)
            .endTime(endDate)
            .metricDataQueries(dq)
            .build();

        GetMetricDataResponse response = client.getMetricData(getMetReq);

        if (response.hasMetricDataResults()) {
            data = response.metricDataResults();
        }
        return data;
    }
}
