package software.tnb.common.deployment;

public interface WithDockerImage {
    /**
     * This method should always be called from Local/OpenshiftX, so it should always extend some X class and the property to use
     * to override the image would resolve to x.image
     *
     * @return docker image to use
     */
    default String image() {
        final Class<?> superclass = this.getClass().getSuperclass();
        if (Object.class.equals(superclass)) {
            throw new IllegalStateException("Current class " + this.getClass().getSimpleName() + " does not extend any other class"
                + " and default method from WithDockerImage was called, either override this method or check what's wrong as this shouldn't happen");
        }
        return System.getProperty(superclass.getSimpleName().toLowerCase() + ".image", defaultImage());
    }

    /**
     * The default image to use.
     *
     * @return default image to use
     */
    String defaultImage();
}
