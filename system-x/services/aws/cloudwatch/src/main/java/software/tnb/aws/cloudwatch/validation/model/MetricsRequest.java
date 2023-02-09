package software.tnb.aws.cloudwatch.validation.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

public class MetricsRequest {
    private String metricName;
    private String namespace;
    private String stat;
    private int maxDataPoints;
    private int period;
    private Instant start;
    private Instant end;
    private String queryId;

    public String metricName() {
        return metricName;
    }

    public String namespace() {
        return namespace;
    }

    public String stat() {
        return stat;
    }

    public int maxDataPoints() {
        return maxDataPoints;
    }

    public int period() {
        return period;
    }

    public Instant start() {
        return start;
    }

    public Instant end() {
        return end;
    }

    public String queryId() {
        return queryId;
    }

    public static final class MetricsRequestBuilder {
        private String metricName;
        private String namespace;
        private String stat;
        private int maxDataPoints = 100;
        private int period = 60;
        private Instant start;
        private Instant end = Instant.now();
        private String queryId = RandomStringUtils.randomAlphabetic(8).toLowerCase();

        public MetricsRequestBuilder() {
        }

        public MetricsRequestBuilder metricName(String metricName) {
            this.metricName = metricName;
            return this;
        }

        public MetricsRequestBuilder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public MetricsRequestBuilder stat(Stat stat) {
            this.stat = stat.value();
            return this;
        }

        public MetricsRequestBuilder stat(String stat) {
            this.stat = stat;
            return this;
        }

        public MetricsRequestBuilder maxDataPoints(int maxDataPoints) {
            this.maxDataPoints = maxDataPoints;
            return this;
        }

        public MetricsRequestBuilder period(int period) {
            this.period = period;
            return this;
        }

        public MetricsRequestBuilder start(Instant start) {
            this.start = start;
            return this;
        }

        public MetricsRequestBuilder end(Instant end) {
            this.end = end;
            return this;
        }

        public MetricsRequestBuilder queryId(String queryId) {
            this.queryId = queryId;
            return this;
        }

        public MetricsRequest build() {
            if (metricName == null || namespace == null || stat == null || start == null) {
                throw new IllegalArgumentException(
                    "At least one required parameter missing. Required parameters are metricName, namespace, stat, start");
            }

            MetricsRequest metricsRequest = new MetricsRequest();
            metricsRequest.maxDataPoints = this.maxDataPoints;
            metricsRequest.start = this.start;
            metricsRequest.metricName = this.metricName;
            metricsRequest.end = this.end;
            metricsRequest.namespace = this.namespace;
            metricsRequest.stat = this.stat;
            metricsRequest.queryId = this.queryId;
            metricsRequest.period = this.period;
            return metricsRequest;
        }
    }
}
