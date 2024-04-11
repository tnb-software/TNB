package software.tnb.kudu.resource.openshift;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.kudu.resource.client.KuduClient;

import org.apache.commons.lang3.StringUtils;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.xtf.core.openshift.PodShellOutput;

/**
 * Run commands in the client POD using <a href=https://kudu.apache.org/docs/command_line_tools_reference.html>Kudu CLI</a>
 */
public class RemoteClient implements KuduClient {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteClient.class);

    private final List<String> masters;
    private final String clientPodName;
    private final ObjectMapper mapper;

    private String leader;

    public RemoteClient(List<String> masters, String clientPodName) {
        this.masters = masters;
        this.clientPodName = clientPodName;
        mapper = new ObjectMapper();
    }

    @Override
    public String findLeaderMasterServer() {
        if (leader == null) {
            Awaitility.await("wait for master leader")
                .atMost(Duration.ofSeconds(20))
                .ignoreExceptions()
                .until(() -> {
                    final String cmdOut = runCommand("master", "list", "-format=json", getMasters());
                    if (StringUtils.isBlank(cmdOut)) {
                        throw new RuntimeException("unable to read leader master");
                    }
                    final List<Map> result = mapper.readValue(cmdOut, List.class);
                    leader = result.stream().filter(row -> "LEADER".equals(row.get("role")))
                        .map(row -> row.get("rpc-addresses"))
                        .map(Objects::toString).findFirst()
                        .orElseThrow(() -> new IllegalStateException("no leader master found"));
                    return true;
                });
        }
        return leader;
    }

    @Override
    public boolean tableExists(String tableName) {
        final String cmdOut = runCommand("table", "list", "-tables=" + tableName, getMasters());
        return cmdOut.contains(tableName);
    }

    @Override
    public void createTable(Map<String, Object> tableDefinition) {
        try {
            runCommand("table", "create", getMasters(), mapper.writeValueAsString(tableDefinition));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> listRows(String tableName, List<String> columns, String predicates) {
        final List<String> commands = new ArrayList<>(List.of("table", "scan", getMasters(), tableName));
        if (columns != null && !columns.isEmpty()) {
            commands.add("-columns=" + String.join(",", columns));
        }
        if (StringUtils.isNotBlank(predicates)) {
            commands.add("-predicates=" + predicates);
        }
        final String cmdOut = runCommand(commands);
        LOG.debug("table rows:\n{}", cmdOut);
        return Arrays.stream(cmdOut.split(System.lineSeparator())).filter(line -> line.startsWith("("))
            .map(line -> line.replaceAll("\\(", ""))
            .map(line -> line.replaceAll("\\)", ""))
            .toList();
    }

    @Override
    public void close() {
        leader = null;
    }

    /**
     * Run command in POD
     * @param commands String[], the commands without initial kudu
     * @return String, output
     */
    private String runCommand(final String... commands) {
        final String[] com = new String[commands.length + 1];
        com[0] = "kudu";
        System.arraycopy(commands, 0, com, 1, com.length - 1);
        final PodShellOutput outCmd = OpenshiftClient.get().podShell(OpenshiftClient.get().getPod(clientPodName)).execute(com);
        if (StringUtils.isNotBlank(outCmd.getError())) {
            throw new RuntimeException(outCmd.getError());
        }
        return outCmd.getOutput();
    }

    private String runCommand(final List<String> commands) {
        return runCommand(commands.toArray(new String[0]));
    }

    private String getMasters() {
        return String.join(",", masters);
    }
}
