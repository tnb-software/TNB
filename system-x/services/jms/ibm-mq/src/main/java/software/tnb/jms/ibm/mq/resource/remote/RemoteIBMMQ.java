package software.tnb.jms.ibm.mq.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.jms.ibm.mq.service.IBMMQ;

import com.google.auto.service.AutoService;

@AutoService(IBMMQ.class)
public class RemoteIBMMQ extends IBMMQ implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }
}
