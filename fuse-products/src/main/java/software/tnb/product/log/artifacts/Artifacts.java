package software.tnb.product.log.artifacts;

import software.tnb.common.utils.IOUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class for gathering file paths to be saved into a single file.
 * <p></p>
 * The main usage is to gather diagnostic files related to the failed tests (see {@link ArtifactsTestExecutionListener} class
 */
public final class Artifacts {
    private static final String ARTIFACTS_FILE_NAME = "artifact-list.txt";
    private static final Path ARTIFACTS_FILE_PATH = Paths.get("target", ARTIFACTS_FILE_NAME);

    // Keep the artifacts as a set - in case of multiple test failures in a shared app class it would result in multiple uploads of the same file
    private static final Set<Path> artifacts = new HashSet<>();
    private static final List<Path> currentArtifacts = new ArrayList<>();

    private Artifacts() {
    }

    public static void add(Path p) {
        currentArtifacts.add(p);
    }

    public static void remove(Path p) {
        currentArtifacts.remove(p);
    }

    public static void clear() {
        currentArtifacts.clear();
    }

    public static void saveCurrentArtifacts() {
        artifacts.addAll(currentArtifacts);
    }

    public static void persist() {
        String content = artifacts.stream()
            .map(p -> p.toAbsolutePath().toString())
            .collect(Collectors.joining("\n"));
        IOUtils.writeFile(ARTIFACTS_FILE_PATH, content.getBytes());
    }
}
