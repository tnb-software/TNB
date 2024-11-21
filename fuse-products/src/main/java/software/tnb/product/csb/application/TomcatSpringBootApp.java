package software.tnb.product.csb.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.product.application.App;
import software.tnb.product.application.Phase;
import software.tnb.product.csb.configuration.SpringBootConfiguration;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.rest.RestCustomizer;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.log.FileLog;
import software.tnb.product.log.Log;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.ZipUtils;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TomcatSpringBootApp extends SpringBootApp {
    private static final Logger LOG = LoggerFactory.getLogger(TomcatSpringBootApp.class);

    private AbstractIntegrationBuilder<?> integrationBuilder;
    private Path tomcatTmpDirectory;
    private Path tomcatHome = null;
    private Process tomcatProcess = null;
    private static final String TOMCAT_PARENT_DIRECTORY = "tomcat";
    private static final String TOMCAT_ARCHIVE_NAME = "tomcat-archive.zip";

    @Override
    public Log getLog() {
        return new FileLog(getLogPath());
    }

    public TomcatSpringBootApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        downloadTomcat();

        this.integrationBuilder = integrationBuilder;
    }

    private void startTomcat() {
        File[] file = tomcatTmpDirectory.resolve(TOMCAT_PARENT_DIRECTORY).toFile().listFiles();
        for (File f : file) {
            if (f.isDirectory()) {
                if (f.getName().contains("apache-tomcat")) {
                    tomcatHome = f.toPath();
                } else if (f.getName().contains("jws")) {
                    tomcatHome = f.toPath().resolve("tomcat"); // Case JWS
                }
            }
        }

        if (tomcatHome == null) {
            throw new RuntimeException("Could not find Tomcat home in " + tomcatTmpDirectory.resolve(TOMCAT_PARENT_DIRECTORY));
        }

        Path logFile = getLogPath();
        // startup.sh starts on another process, let's use catalina run so that we can control the lifecyle
        ProcessBuilder processBuilder = new ProcessBuilder(tomcatHome + File.separator + "bin" + File.separator + "catalina.sh", "run")
            .redirectError(logFile.toFile())
            .redirectOutput(logFile.toFile());

        try {
            tomcatProcess = processBuilder.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (tomcatProcess != null) {
                    tomcatProcess.destroyForcibly();
                }
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Starting tomcat in {}", tomcatHome);
    }

    private void downloadTomcat() {
        try {
            tomcatTmpDirectory = Files.createTempDirectory("tnb-tomcat");
            LOG.info("Downloading tomcat in {}", tomcatTmpDirectory);
            FileUtils.copyURLToFile(new URL(SpringBootConfiguration.tomcatZipUrl()), tomcatTmpDirectory.resolve(TOMCAT_ARCHIVE_NAME).toFile());

            ZipUtils.unzip(tomcatTmpDirectory.resolve(TOMCAT_ARCHIVE_NAME), tomcatTmpDirectory.resolve(TOMCAT_PARENT_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        startTomcat();

        // replace packaging and extend SpringBootServletInitializer
        try {
            Path pomPath = TestConfiguration.appLocation().resolve(name).resolve("pom.xml");
            String pom = Files.readString(pomPath);
            pom = pom.replace("<artifactId>" + name + "</artifactId>",
                "<artifactId>" + name + "</artifactId><packaging>war</packaging>");
            Files.write(pomPath, pom.getBytes(StandardCharsets.UTF_8));

            Path mainPath = TestConfiguration.appLocation().resolve(name)
                .resolve("src")
                .resolve("main")
                .resolve("java")
                .resolve("com")
                .resolve("test")
                .resolve("MySpringBootApplication.java");
            String main = Files.readString(mainPath);

            main = main.replace("class MySpringBootApplication {",
                """
                    class MySpringBootApplication extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer {
                    
                        @Override
                        protected org.springframework.boot.builder.SpringApplicationBuilder 
                                configure(org.springframework.boot.builder.SpringApplicationBuilder application) {
                            return application.sources(MySpringBootApplication.class);
                        }
                    """);
            Files.write(mainPath, main.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // package application
        BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
            .withGoals("clean", "package")
            .withProperties(Map.of(
                "skipTests", "true"
            ))
            .withLogFile(getLogPath(Phase.BUILD))
            .withLogMarker(LogStream.marker(name, Phase.BUILD));

        LOG.info("Building {} application project for tomcat", name);
        Maven.invoke(requestBuilder.build());

        // copy generated WAR in tomcat
        String warName = name + "-1.0.0-SNAPSHOT.war";
        try {
            Files.copy(TestConfiguration.appLocation().resolve(name).resolve("target").resolve(warName),
                tomcatHome.resolve("webapps").resolve(warName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (logStream != null) {
            logStream.stop();
        }

        if (log != null) {
            log.save();
        }

        if (tomcatProcess != null) {
            try {
                Process shutdown = new ProcessBuilder(tomcatHome + File.separator + "bin" + File.separator + "shutdown.sh")
                    .start();
                shutdown.waitFor();

                tomcatProcess.destroyForcibly();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isReady() {
        return tomcatProcess != null && tomcatProcess.isAlive();
    }

    @Override
    public boolean isFailed() {
        return tomcatProcess != null && !tomcatProcess.isAlive();
    }
}
