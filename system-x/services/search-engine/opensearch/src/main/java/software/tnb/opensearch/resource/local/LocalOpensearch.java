package software.tnb.opensearch.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.opensearch.service.Opensearch;
import software.tnb.searchengine.common.resource.local.LocalSearch;
import software.tnb.searchengine.common.resource.local.SearchContainer;

import com.google.auto.service.AutoService;

@AutoService(Opensearch.class)
public class LocalOpensearch extends Opensearch implements ContainerDeployable<SearchContainer> {
    private final SearchContainer container;
    private final LocalSearch localSearch;

    public LocalOpensearch() {
        container = new SearchContainer(image(), port(),
            containerEnv(), containerStartRegex(), getNetworkAliases());
        localSearch = new LocalSearch(container);
    }

    @Override
    public SearchContainer container() {
        return container;
    }

    @Override
    public void deploy() {
        localSearch.deploy();
    }

    @Override
    public void undeploy() {
        localSearch.undeploy();
    }

    @Override
    public String host() {
        return container.getHost();
    }
}
