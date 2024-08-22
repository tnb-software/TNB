package software.tnb.aws.cloudwatch.validation.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

public record MetricsRequest(String metricName, String namespace, String stat, int maxDataPoints, int period,
                             Instant start, Instant end, String queryId) {

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

            return new MetricsRequest(this.metricName, this.namespace, this.stat, this.maxDataPoints, this.period, this.start, this.end,
                this.queryId);
        }
    }
}
