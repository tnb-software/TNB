package software.tnb.searchengine.common.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.searchengine.common.service.Search;

public class RemoteSearch implements RemoteService {
    private Search service;

    public RemoteSearch(Search service) {
        this.service = service;
    }

    @Override
    public String host() {
        return RemoteService.super.host();
    }

    @Override
    public void openResources() {
        service.openResources();
    }

    @Override
    public void closeResources() {
        service.closeResources();
    }
}
