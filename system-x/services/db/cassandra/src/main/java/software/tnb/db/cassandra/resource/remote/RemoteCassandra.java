package software.tnb.db.cassandra.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.db.cassandra.service.Cassandra;

import com.google.auto.service.AutoService;

@AutoService(Cassandra.class)
public class RemoteCassandra extends Cassandra implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }
}
