package software.tnb.image.util;

import software.tnb.common.deployment.WithDockerImage;

public class ParentClass implements WithDockerImage {
    @Override
    public String defaultImage() {
        return "parentclassdefaultimage";
    }
}
