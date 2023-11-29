package software.tnb.elasticsearch.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.elasticsearch.service.Elasticsearch;

import com.google.auto.service.AutoService;

@AutoService(Elasticsearch.class)
public class RemoteElasticsearch extends Elasticsearch implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }
}
