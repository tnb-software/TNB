package software.tnb.cryostat.client;

import software.tnb.cryostat.generated.recording.Recording;
import software.tnb.cryostat.generated.targets.Target;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CryostatClient {

    enum StandardJfrTemplates {
        Continuous
        , Profiling
        , ALL
    }

    void authenticate(String apiContextUrl) throws IOException;

    List<Target> targets(String apiContextUrl) throws IOException;

    void addTarget(String apiContextUrl, String alias, String appName) throws IOException;

    List<Recording> recordings(String apiContextUrl) throws IOException;

    void startRecording(String apiContextUrl, String name, Map<String, String> labels) throws IOException;

    void stopRecording(String apiContextUrl) throws IOException;

    void downloadRecording(String apiContextUrl, String destinationPath) throws IOException;

    void deleteRecording(String apiContextUrl) throws IOException;

    String getIp(String appName);

    String getPort();

    String getPodName(String appName);

    String getJfrTemplate();

    void setJfrTemplate(String jfrTemplate);
}
