package software.tnb.jaeger.client;

import software.tnb.jaeger.validation.model.Span;

import java.util.List;
import java.util.Map;

public interface JaegerClient {

    Map<String, Object> getTrace(String traceId);

    List<Span> getSpans(String traceId);
}
