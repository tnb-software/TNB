package software.tnb.cryostat.validation;

import software.tnb.common.validation.Validation;
import software.tnb.cryostat.client.CryostatClient;
import software.tnb.cryostat.generated.recording.Recording;
import software.tnb.cryostat.generated.targets.Target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CryostatValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(CryostatValidation.class);

    private static final String API_AUTH = "/api/v4/auth";
    private static final String API_TARGET = "/api/v4/targets";
    private static final String API_RECORDING = "/api/v4/targets/%s/recordings";
    private static final String API_TARGET_CREATE = "/api/v4/targets";
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
            try {
                List<Recording> recordings = getRecordings(recordingInfo.getTargetId());
                LOG.info("Found {} recordings for target {}", recordings.size(), recordingInfo.getTargetId());
                recordings.stream()
                    .filter(r -> recordingInfo.getRecordingName().equals(r.getName()))
                    .findFirst()
                    .ifPresent(r -> {
                        int rid = r.getRemoteId() != null ? r.getRemoteId().intValue() : r.getId().intValue();
                        LOG.info("Recording {} has remoteId {}, downloadUrl {}", r.getName(), rid, r.getDownloadUrl());
                        recordingInfo.setRemoteId(rid);
                        recordingInfo.setDownloadUrl(r.getDownloadUrl());
                    });
            } catch (Exception e) {
                LOG.warn("Failed to retrieve remoteId for recording {}: {}", recordingInfo.getRecordingName(), e.getMessage());
            }
            LOG.debug("Recording {} started with remoteId {}", recordingInfo.getRecordingName(), recordingInfo.getRemoteId());
        } catch (IOException e) {
            throw new RuntimeException("unable to start recording", e);
        }
    }

    private String recordingPath(RecordingInfo recordingInfo) {
        return String.format(API_RECORDING + "/%s", recordingInfo.getTargetId(), recordingInfo.getRemoteId());
    }

    public void stopRecording(RecordingInfo recordingInfo) {
        try {
            LOG.debug("Stop recording {}", recordingInfo.getRecordingName());
            delegate.stopRecording(recordingPath(recordingInfo));
        } catch (Exception e) {
            LOG.warn("Failed to stop recording {} (target may have been shut down): {}", recordingInfo.getRecordingName(), e.getMessage());
        }
    }

    public void deleteRecording(RecordingInfo recordingInfo) {
        try {
            LOG.debug("Delete recording {}", recordingInfo.getRecordingName());
            delegate.deleteRecording(recordingPath(recordingInfo));
        } catch (Exception e) {
            LOG.warn("Failed to delete recording {} (target may have been shut down): {}", recordingInfo.getRecordingName(), e.getMessage());
        }
    }

    public void downloadRecording(RecordingInfo recordingInfo, String destinationFile) {
        try {
            String path = recordingInfo.getDownloadUrl() != null ? recordingInfo.getDownloadUrl() : recordingPath(recordingInfo);
            LOG.debug("Downloading recording from {}", path);
            delegate.downloadRecording(path, destinationFile);
            LOG.debug("JFR recording saved to {}", destinationFile);
        } catch (Exception e) {
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
        List<Target> targets = getTargets();
        LOG.info("Discovered targets: {}", targets.stream().map(t -> "alias=" + t.getAlias() + " id=" + t.getAdditionalProperties().get("id"))
            .collect(java.util.stream.Collectors.joining(", ")));
        targets.stream().filter(t -> t.getAlias().equals(targetAlias))
            .findFirst().ifPresent(target -> {
                String targetId = String.valueOf(target.getId());
                LOG.info("Found target: alias={}, id={}", target.getAlias(), targetId);
                info.set(new RecordingInfo(targetId
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
        private Integer remoteId;
        private String downloadUrl;

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

        public Integer getRemoteId() {
            return remoteId;
        }

        public void setRemoteId(Integer remoteId) {
            this.remoteId = remoteId;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
}
