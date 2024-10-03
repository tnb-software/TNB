package software.tnb.elasticsearch.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.elasticsearch.service.Elasticsearch;
import software.tnb.searchengine.common.resource.local.LocalSearch;
import software.tnb.searchengine.common.resource.local.SearchContainer;

import com.google.auto.service.AutoService;

@AutoService(Elasticsearch.class)
public class LocalElasticsearch extends Elasticsearch implements Deployable {

    private final SearchContainer container;
    private final LocalSearch localSearch;

    public LocalElasticsearch() {
        container = new SearchContainer(image(), port(),
            containerEnv(), containerStartRegex(), getNetworkAliases());
        localSearch = new LocalSearch(container);
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
    public void openResources() {
        localSearch.openResources();
    }

    @Override
    public void closeResources() {
        localSearch.closeResources();
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public String containerStartRegex() {
        return ".*(\"message\":\\s?\"started[\\s?|\"].*|] started\n$)";
    }
}
