package software.tnb.fhir.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.fhir.service.Fhir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Fhir.class)
public class LocalFhir extends Fhir implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFhir.class);
    private FhirContainer container;

    @Override
    public String defaultImage() {
        return FHIR_IMAGE;
    }

    @Override
    public void deploy() {
        LOG.info("Starting FHIR container");
        container = new FhirContainer(defaultImage(), PORT, containerEnvironment());
        container.start();
        LOG.info("FHIR container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping FHIR container");
            container.stop();
        }
    }

    @Override
    public int getPortMapping() {
        return container.getMappedPort(PORT);
    }

    @Override
    public String getServerUrl() {
        return String.format("http://%s:%d/", container.getHost(), getPortMapping());
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }
}
