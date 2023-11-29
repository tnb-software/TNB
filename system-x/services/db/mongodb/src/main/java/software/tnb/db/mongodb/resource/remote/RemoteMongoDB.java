package software.tnb.db.mongodb.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.db.mongodb.service.MongoDB;

import com.google.auto.service.AutoService;

@AutoService(MongoDB.class)
public class RemoteMongoDB extends MongoDB implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }
}
