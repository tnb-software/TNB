package software.tnb.searchengine.common.resource.local;

import software.tnb.common.deployment.ContainerDeployable;

public class LocalSearch implements ContainerDeployable<SearchContainer> {
    private final SearchContainer container;

    public LocalSearch(SearchContainer container) {
        // Specify max 2GB of memory, seems to work ok, but without it the container can eat a lot of ram
        this.container = container
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(1024L * 1024 * 1024 * 2));
    }

    @Override
    public SearchContainer container() {
        return container;
    }

    @Override
    public void openResources() {
        // no-op
    }

    @Override
    public void closeResources() {
        // no-op
    }
}
