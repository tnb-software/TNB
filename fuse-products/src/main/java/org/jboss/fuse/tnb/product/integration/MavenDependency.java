package org.jboss.fuse.tnb.product.integration;

import java.util.ArrayList;
import java.util.List;

public class MavenDependency {
    private final String dependency;
    private final List<String> exclusions;

    public MavenDependency(String dependency) {
        this.dependency = dependency;
        this.exclusions = new ArrayList<>();
    }

    public MavenDependency(String dependency, List<String> exclusions) {
        this.dependency = dependency;
        this.exclusions = exclusions;
    }

    public String getDependency() {
        return dependency;
    }

    public List<String> getExclusions() {
        return exclusions;
    }
}
