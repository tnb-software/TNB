package software.tnb.jaeger.validation;

import software.tnb.common.validation.Validation;
import software.tnb.jaeger.client.JaegerClient;
import software.tnb.jaeger.validation.model.Span;

import java.util.List;
import java.util.Map;

public class JaegerValidation implements Validation {

    private final JaegerClient client;

    public JaegerValidation(final JaegerClient client) {
        this.client = client;
    }

    public Map<String, Object> getFullTrace(String traceId) {
        return client.getTrace(traceId);
    }

    public List<Span> getSpans(String traceId) {
        return client.getSpans(traceId);
    }
}
