package software.tnb.splunk.resource.remote;

import software.tnb.common.deployment.RemoteService;
import software.tnb.splunk.service.Splunk;
import software.tnb.splunk.service.configuration.SplunkProtocol;

import com.google.auto.service.AutoService;

@AutoService(Splunk.class)
public class RemoteSplunk extends Splunk implements RemoteService {
    @Override
    public String host() {
        return RemoteService.super.host();
    }

    @Override
    protected void defaultConfiguration() {
        getConfiguration().protocol(SplunkProtocol.HTTP);
    }
}
