package software.tnb.cryostat.validation;

import software.tnb.cryostat.generated.recording.Recording;
import software.tnb.cryostat.generated.targets.Cryostat;
import software.tnb.cryostat.generated.targets.Target;
import software.tnb.cryostat.client.CryostatClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CryostatValidation {

    private static final Logger LOG = LoggerFactory.getLogger(CryostatValidation.class);

    private static final String API_AUTH = "/api/v1/auth";
    private static final String API_TARGET = "/api/v1/targets";
    private static final String API_RECORDING = "/api/v1/targets/%s/recordings";
    private static final String API_TARGET_CREATE = "/api/v2/targets";
    private final CryostatClient delegate;

    public CryostatValidation(CryostatClient client) {
        this.delegate = client;
    }

    public void init() {
        try {
            delegate.authenticate(API_AUTH);
        } catch (IOException e) {
            throw new RuntimeException("unable to initialize Cryostat connection", e);
        }
    }

    public List<Target> getTargets() {
        try {
            return delegate.targets(API_TARGET);
        } catch (IOException e) {
            throw new RuntimeException("unable to read targets", e);
        }
    }

    public void addTarget(String alias, String appName) {
        try {
            delegate.addTarget(API_TARGET_CREATE, alias, appName);
        } catch (IOException e) {
            throw new RuntimeException("unable to add target", e);
        }
    }

    public List<Recording> getRecordings(String targetId) {
        try {
            return delegate.recordings(String.format(API_RECORDING, targetId));
        } catch (IOException e) {
            throw new RuntimeException("unable to read recordings", e);
        }
    }

    public void startRecording(RecordingInfo recordingInfo, String app) {
        try {
            LOG.debug("Starting recording {} using template {}", recordingInfo.getRecordingName(), recordingInfo.getJfrTemplateName());
            delegate.setJfrTemplate(recordingInfo.getJfrTemplateName());
            delegate.startRecording(String.format(API_RECORDING, recordingInfo.getTargetId()), recordingInfo.getRecordingName(), Map.of("app", app));
        } catch (IOException e) {
            throw new RuntimeException("unable to start recording", e);
        }
    }

    public void stopRecording(RecordingInfo recordingInfo) {
        try {
            LOG.debug("Stop recording {}", recordingInfo.getRecordingName());
            delegate.stopRecording(String.format(API_RECORDING + "/%s", recordingInfo.getTargetId(), recordingInfo.getRecordingName()));
        } catch (IOException e) {
            throw new RuntimeException("unable to stop recording", e);
        }
    }

    public void deleteRecording(RecordingInfo recordingInfo) {
        try {
            LOG.debug("Delete recording {}", recordingInfo.getRecordingName());
            delegate.deleteRecording(String.format(API_RECORDING + "/%s", recordingInfo.getTargetId(), recordingInfo.getRecordingName()));
        } catch (IOException e) {
            throw new RuntimeException("unable to delete recording", e);
        }
    }

    public void downloadRecording(RecordingInfo recordingInfo, String destinationFile) {
        try {
            delegate.downloadRecording(String.format(API_RECORDING + "/%s", recordingInfo.getTargetId(), recordingInfo.getRecordingName())
                , destinationFile);
            LOG.debug("JFR recording saved to {}", destinationFile);
        } catch (IOException e) {
            throw new RuntimeException("unable to download recording", e);
        }
    }

    public String getPodName(String appName) {
        return delegate.getPodName(appName);
    }

    public RecordingInfo startRecordingOnNewTarget(String appName, String jfrTemplateName) {
        AtomicReference<RecordingInfo> info = new AtomicReference<>();
        String targetAlias = getPodName(appName);
        addTarget(targetAlias, appName);
        getTargets().stream().filter(t -> t.getAlias().equals(targetAlias))
            .findFirst().ifPresent(target -> {
                Cryostat cry = target.getAnnotations().getCryostat();
                info.set(new RecordingInfo(cry.getHost() + ":" + cry.getPort()
                    , appName + "-" + UUID.randomUUID().toString().substring(0, 4), jfrTemplateName));
                startRecording(info.get(), appName);
            });
        if (info.get() == null) {
            throw new RuntimeException("unable to find created cryostat target");
        }
        return info.get();
    }

    public static class RecordingInfo {

        final String targetId;
        final String recordingName;
        final String jfrTemplateName;

        public RecordingInfo(final String targetId, final String recordingName, final String jfrTemplateName) {
            this.targetId = targetId;
            this.recordingName = recordingName;
            this.jfrTemplateName = jfrTemplateName;
        }

        public String getTargetId() {
            return targetId;
        }

        public String getRecordingName() {
            return recordingName;
        }

        public String getJfrTemplateName() {
            return jfrTemplateName;
        }
    }
}
