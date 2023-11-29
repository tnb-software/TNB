package software.tnb.jms.amq.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.jms.amq.service.AMQBroker;

import com.google.auto.service.AutoService;

@AutoService(AMQBroker.class)
public class RemoteAMQBroker extends AMQBroker implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }

    @Override
    public int getPortMapping(int port) {
        return port;
    }
}
