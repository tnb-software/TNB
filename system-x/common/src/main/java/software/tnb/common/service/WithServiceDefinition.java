package software.tnb.common.service;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.util.ReflectionUtil;

public interface WithServiceDefinition {
    String NAME_PROPERTY_FORMAT = "tnb.%s.service.name";
    String VERSION_PROPERTY_FORMAT = "tnb.%s.service.version";

    default String serviceName() {
        String key = ReflectionUtil.getSuperClassName(this.getClass()).toLowerCase();
        return TestConfiguration.getProperty(String.format(NAME_PROPERTY_FORMAT, key), defaultServiceName());
    }

    default String serviceVersion() {
        String key = ReflectionUtil.getSuperClassName(this.getClass()).toLowerCase();
        return TestConfiguration.getProperty(String.format(VERSION_PROPERTY_FORMAT, key), defaultServiceVersion());
    }

    default String driverVersion() {
        return null;
    }

    private String defaultServiceName() {
        String csvName = csvPart(0);
        if (csvName != null) {
            return csvName;
        }
        String[] imageParts = imageParts();
        if (imageParts != null) {
            String tag = imageParts[1];
            if (tag == null || "latest".equals(tag)) {
                int slashIndex = imageParts[0].lastIndexOf('/');
                if (slashIndex >= 0) {
                    return imageParts[0].substring(slashIndex + 1);
                }
            }
        }
        return ReflectionUtil.getSuperClassName(this.getClass());
    }

    private String defaultServiceVersion() {
        String csvVersion = csvPart(1);
        if (csvVersion != null) {
            return csvVersion;
        }
        String[] imageParts = imageParts();
        if (imageParts != null && imageParts[1] != null) {
            return imageParts[1];
        }
        return null;
    }

    private String csvPart(int part) {
        if (!(this instanceof WithOperatorHub)) {
            return null;
        }
        String csv = ((WithOperatorHub) this).currentCSV();
        if (csv == null) {
            return null;
        }
        int dot = csv.indexOf('.');
        if (dot < 0) {
            return null;
        }
        return part == 0 ? csv.substring(0, dot) : csv.substring(dot + 1);
    }

    private String[] imageParts() {
        if (!(this instanceof WithDockerImage)) {
            return null;
        }
        String image = ((WithDockerImage) this).image();
        int colonIndex = image.lastIndexOf(':');
        String path = colonIndex >= 0 ? image.substring(0, colonIndex) : image;
        String tag = colonIndex >= 0 ? image.substring(colonIndex + 1) : null;
        return new String[]{path, tag};
    }
}
