package software.tnb.horreum.tools;

import software.tnb.horreum.validation.generated.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

public class PrettyPrinter {
    private static final Logger LOG = LoggerFactory.getLogger(PrettyPrinter.class);
    private Set<String> sensitiveHeaders = new HashSet<>();

    public PrettyPrinter() {
        this.mapper = new ObjectMapper();
    }

    private ObjectMapper mapper;

    public void printJsonBody(String jsonString) throws JsonProcessingException {
        Object json = mapper.readValue(jsonString, Object.class);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        System.out.println(writer.writeValueAsString(json) + "\n");
    }

    public String convertBodyToString(RequestBody body) {
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to convert RequestBody to String");
        }
    }

    public void printUrl(Request request) {
        System.out.println(request.url() + "\n");
    }

    public void printHeaders(Request request) {
        Map<String, List<String>> headers = request.headers().toMultimap();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (sensitiveHeaders.stream().map(String::toLowerCase).collect(Collectors.toSet())
                .contains(entry.getKey())) {
                headers.put(entry.getKey(), List.of("*****"));
            }
        }
        StringBuilder output = new StringBuilder();
        headers.forEach((key, values) -> {
            output.append(key)
                .append(": ")
                .append(String.join(", ", values))
                .append("\n");
        });
        System.out.println(output.toString());
    }

    public void printHeaders(ApiResponse<String> response) {
        System.out.println(
            response.getHeaders().entrySet()
                .stream()
                .map(entry -> entry.getKey() + " : " + String.join(", ", entry.getValue()))
                .collect(Collectors.joining("\n")) + "\n");
    }

    public void printData(ApiResponse response) {
        System.out.println(response.getData() + "\n");
    }

    public void printStatusCode(ApiResponse response) {
        System.out.println(response.getStatusCode() + "\n");
    }

    public void printRequest(Request request) throws JsonProcessingException {
        System.out.println("\nURL ->");
        printUrl(request);
        System.out.println("\nHeaders ->");
        printHeaders(request);
        System.out.println("\nBody ->");
        printJsonBody(convertBodyToString(request.body()));
    }

    public void printResponse(ApiResponse<String> response) {
        System.out.println("\nStatus code -> ");
        printStatusCode(response);
        System.out.println("\nHeaders ->");
        printHeaders(response);
        System.out.println("\nRunData ID -> ");
        printData(response);
    }

    /**
     * Add sensitive header which value needs to be hidden on output.
     *
     * @param headerName
     */
    public void addSensitiveHeader(String headerName) {
        this.sensitiveHeaders.add(headerName);
    }
}
