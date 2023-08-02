package software.tnb.product.customizer;

import software.tnb.common.config.TestConfiguration;
import software.tnb.product.util.maven.Maven;

import org.apache.maven.model.Model;

import java.io.File;
import java.util.function.Supplier;

public abstract class POMCustomizer extends ProductsCustomizer {
    private final Supplier<File> pomFile = () ->
        TestConfiguration.appLocation().resolve(getIntegrationBuilder().getIntegrationName()).resolve("pom.xml").toFile();

    @Override
    public void customizeQuarkus() {
        Model pom = Maven.loadPom(pomFile.get());
        customizeQuarkus(pom);
        Maven.writePom(pomFile.get(), pom);
    }

    @Override
    public void customizeSpringboot() {
        Model pom = Maven.loadPom(pomFile.get());
        customizeSpringboot(pom);
        Maven.writePom(pomFile.get(), pom);
    }

    public abstract void customizeQuarkus(Model pom);

    public abstract void customizeSpringboot(Model pom);

    @Override
    public void customizeCamelK() {
    }
}
