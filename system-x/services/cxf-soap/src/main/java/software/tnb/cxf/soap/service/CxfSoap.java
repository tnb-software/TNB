package software.tnb.cxf.soap.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

public abstract class CxfSoap extends Service<NoAccount, NoClient, NoValidation> implements WithDockerImage {

    protected static final int PORT = 8080;

    public abstract String host();

    public abstract int port();

    public String defaultImage() {
        return "quay.io/l2x6/calculator-ws:1.3";
    }

    public void openResources() {

    }

    public void closeResources() {

    }

    public String serviceEndpointUrl() {
        return String.format("http://%s:%s/calculator-ws/CalculatorService", host(), port());
    }

    public String wsdlUrl() {
        return serviceEndpointUrl() + "?wsdl";
    }
}
