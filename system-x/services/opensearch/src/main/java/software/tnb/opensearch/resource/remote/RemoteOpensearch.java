package software.tnb.opensearch.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.opensearch.service.Opensearch;

import com.google.auto.service.AutoService;

@AutoService(Opensearch.class)
public class RemoteOpensearch extends Opensearch implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }
}
