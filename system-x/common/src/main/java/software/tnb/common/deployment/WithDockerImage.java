package software.tnb.common.deployment;

import software.tnb.common.util.ReflectionUtil;

public interface WithDockerImage {
    String SYSTEM_PROPERTY_FORMAT = "tnb.%s.image";

    /**
     * This method should always be called from Local/OpenshiftX, so it should always extend some X class and the property to use
     * to override the image would resolve to x.image
     *
     * @return docker image to use
     */
    default String image() {
        return System.getProperty(String.format(SYSTEM_PROPERTY_FORMAT, ReflectionUtil.getSuperClassName(this.getClass()).toLowerCase()),
            defaultImage());
    }

    /**
     * The default image to use.
     *
     * @return default image to use
     */
    String defaultImage();
}
