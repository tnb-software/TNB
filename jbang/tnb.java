///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS software.tnb:system-x-all:1.0-SNAPSHOT
//DEPS org.jline:jline-groovy:3.22.0
//DEPS org.slf4j:slf4j-nop:1.7.36
//RUNTIME_OPTIONS --add-opens java.base/java.lang=ALL-UNNAMED

import static org.jline.builtins.SyntaxHighlighter.DEFAULT_NANORC_FILE;
import static org.jline.console.ConsoleEngine.VAR_NANORC;
import static org.jline.keymap.KeyMap.ctrl;
import static org.jline.keymap.KeyMap.key;

import software.tnb.common.account.Account;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.account.NoAccount;
import software.tnb.common.account.WithId;
import software.tnb.common.account.loader.CredentialsLoader;
import software.tnb.common.account.loader.DelegatingCredentialsLoader;
import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.Service;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.MapUtils;

import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jline.builtins.ConfigurationPath;
import org.jline.builtins.Options;
import org.jline.builtins.SyntaxHighlighter;
import org.jline.console.CmdDesc;
import org.jline.console.CommandInput;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.console.ConsoleEngine;
import org.jline.console.Printer;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.ConsoleEngineImpl;
import org.jline.console.impl.DefaultPrinter;
import org.jline.console.impl.JlineCommandRegistry;
import org.jline.console.impl.SystemHighlighter;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.script.GroovyCommand;
import org.jline.script.GroovyEngine;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.OSUtils;
import org.jline.widget.TailTipWidgets;
import org.jline.widget.Widgets;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import groovy.lang.Closure;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.openshift.client.OpenShiftConfig;

//CHECKSTYLE:OFF
public class tnb {
    //CHECKSTYLE:ON

    static final List<Service<?, ?, ?>> deployedServices = new ArrayList<>();

    static boolean ocpSetup = false;

    static Terminal terminal;
    static LineReader reader;

    static final List<String> defaultImportClasses = List.of(
        OpenshiftConfiguration.class.getName(),
        TestConfiguration.class.getName(),
        OpenshiftClient.class.getName()
    );

    static List<String> defaultImports() {
        return Stream.concat(TNBUtils.findAllRegisteredServices().stream(), defaultImportClasses.stream()).map(clazz -> "import " + clazz + ";")
            .collect(
                Collectors.toList());
    }

    private static Closure<?> wrapMethod(Function<Object, ?> callable) {
        return new Closure<>(null) {
            @Override
            public Object call(Object... args) {
                return callable.apply(args[0]);
            }
        };
    }

    private static class OptionSelector {
        private enum Operation {
            FORWARD_ONE_LINE,
            BACKWARD_ONE_LINE,
            EXIT
        }

        private final Terminal terminal;
        private final List<String> lines = new ArrayList<>();
        private final Size size = new Size();
        private final BindingReader bindingReader;

        OptionSelector(Terminal terminal, String title, Collection<String> options) {
            this.terminal = terminal;
            this.bindingReader = new BindingReader(terminal.reader());
            lines.add(title);
            lines.addAll(options);
        }

        private List<AttributedString> displayLines(int cursorRow) {
            List<AttributedString> out = new ArrayList<>();
            int i = 0;
            for (String s : lines) {
                if (i == cursorRow) {
                    out.add(new AttributedStringBuilder()
                        .append(s, AttributedStyle.INVERSE)
                        .toAttributedString());
                } else {
                    out.add(new AttributedString(s));
                }
                i++;
            }
            return out;
        }

        private void bindKeys(KeyMap<Operation> map) {
            map.bind(Operation.FORWARD_ONE_LINE, "e", ctrl('E'), key(terminal, InfoCmp.Capability.key_down));
            map.bind(Operation.BACKWARD_ONE_LINE, "y", ctrl('Y'), key(terminal, InfoCmp.Capability.key_up));
            map.bind(Operation.EXIT, "\r");
        }

        public String select() {
            Display display = new Display(terminal, true);
            Attributes attr = terminal.enterRawMode();
            try {
                terminal.puts(InfoCmp.Capability.enter_ca_mode);
                terminal.puts(InfoCmp.Capability.keypad_xmit);
                terminal.writer().flush();
                size.copy(terminal.getSize());
                display.clear();
                display.reset();
                int selectRow = 1;
                KeyMap<Operation> keyMap = new KeyMap<>();
                bindKeys(keyMap);
                while (true) {
                    display.resize(size.getRows(), size.getColumns());
                    display.update(
                        displayLines(selectRow),
                        size.cursorPos(0, lines.get(0).length()));
                    Operation op = bindingReader.readBinding(keyMap);
                    switch (op) {
                        case FORWARD_ONE_LINE:
                            selectRow++;
                            if (selectRow > lines.size() - 1) {
                                selectRow = 1;
                            }
                            break;
                        case BACKWARD_ONE_LINE:
                            selectRow--;
                            if (selectRow < 1) {
                                selectRow = lines.size() - 1;
                            }
                            break;
                        case EXIT:
                            return lines.get(selectRow);
                        default:
                            throw new RuntimeException("Invalid operation " + op);
                    }
                }
            } finally {
                terminal.setAttributes(attr);
                terminal.puts(InfoCmp.Capability.exit_ca_mode);
                terminal.puts(InfoCmp.Capability.keypad_local);
                terminal.writer().flush();
            }
        }
    }

    private static final class TNBFolderCredentialsLoader extends CredentialsLoader {

        private final String rootPath;

        private TNBFolderCredentialsLoader(String rootPath) {
            this.rootPath = rootPath;
        }

        @Override
        public Object loadCredentials(String id) {
            return new Yaml().load(IOUtils.readFile(Path.of(rootPath, id + ".properties")));
        }

        @Override
        public String toJson(Object o) {
            try {
                return mapper.writeValueAsString(o);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class JbangCredentialsLoader extends DelegatingCredentialsLoader {

        private final String credentialsFolder;

        JbangCredentialsLoader(String rootPath) {
            super(Stream.of(Try.call(AccountFactory::defaultLoader).toOptional().orElse(null), new TNBFolderCredentialsLoader(rootPath))
                .filter(Objects::nonNull).collect(Collectors.toList()));
            this.credentialsFolder = rootPath;
            IOUtils.createDirectory(Path.of(rootPath));
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        }

        @Override
        public <T extends Account> T get(List<String> credentialsIds, Class<T> accountClass) {
            try {
                return super.get(credentialsIds, accountClass);
            } catch (Exception e) {
                return readCredentialsFromTerminal(accountClass);
            }
        }

        private <T extends Account> T readCredentialsFromTerminal(Class<T> accountClass) {
            terminal.writer().println("Missing account credentials for " + accountClass.getSimpleName());
            T account = ReflectionUtils.newInstance(accountClass);
            Class<?> parentClass = accountClass.getSuperclass();
            while (Account.class.isAssignableFrom(parentClass)) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Account> castClass = (Class<? extends Account>) parentClass;
                    account = mapper.convertValue(AccountFactory.create(castClass), accountClass);
                    break;
                } catch (Exception ignore) {

                }
                parentClass = parentClass.getSuperclass();
            }

            try {
                for (Field field : ReflectionUtils.findFields(accountClass, (field) -> true, ReflectionUtils.HierarchyTraversalMode.TOP_DOWN)) {
                    if (ReflectionUtils.isFinal(field) && ReflectionUtils.isStatic(field)) {
                        //skip constants
                        continue;
                    }
                    if (FieldUtils.readField(field, account, true) != null) {
                        //skip fields that already have some value set
                        continue;
                    }
                    if (field.getType().equals(String.class)) {
                        final String value = reader.readLine(field.getName() + ": ");
                        FieldUtils.writeField(field, account, value, true);
                    } else {
                        throw new IllegalArgumentException("Accounts with field of non-Strings are not supported (yet ;))");
                    }
                }
            } catch (UserInterruptException ignore) {
                throw new RuntimeException("Filling in credentials was cancelled.");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            saveCredentials(account);

            return account;
        }

        private void saveCredentials(Object account) {
            while (account instanceof Account) {
                if (account instanceof WithId) {
                    final String id = ((WithId) account).credentialsId();
                    try {
                        IOUtils.writeFile(Path.of(credentialsFolder, id + ".properties"), mapper.writeValueAsString(account));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                account = mapper.convertValue(account, account.getClass().getSuperclass());
            }
        }
    }

    public static void main(String... args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (deployedServices.isEmpty()) {
                return;
            }
            System.out.println("Undeploying deployed services");
            System.out.flush();
            for (Service<?, ?, ?> service : deployedServices) {
                try {
                    service.afterAll(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        try {
            Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
            //
            // Parser & Terminal
            //
            DefaultParser parser = new DefaultParser();
            parser.setEofOnUnclosedBracket(DefaultParser.Bracket.CURLY, DefaultParser.Bracket.ROUND, DefaultParser.Bracket.SQUARE);
            parser.setEofOnUnclosedQuote(true);
            parser.setRegexCommand("[:]{0,1}[a-zA-Z!]{1,}\\S*"); // change default regex to support shell commands
            parser.blockCommentDelims(new DefaultParser.BlockCommentDelims("/*", "*/"))
                .lineCommentDelims(new String[] {"//"});

            terminal = TerminalBuilder.builder().build();

            terminal.puts(InfoCmp.Capability.clear_screen);

            File rootFile = Path.of(System.getProperty("user.home"), ".tnb").toFile();
            rootFile.mkdirs();

            final String root = rootFile.getCanonicalPath();

            File jnanorcFile = Paths.get(root, DEFAULT_NANORC_FILE).toFile();
            if (!jnanorcFile.exists()) {
                try (FileWriter fw = new FileWriter(jnanorcFile)) {
                    fw.write("theme " + root + "/dark.nanorctheme\n");
                    fw.write("include " + root + "/*.nanorc\n");
                }
            }
            final File groovynanorcFile = Paths.get(root, "groovy.nanorc").toFile();
            if (!groovynanorcFile.exists()) {
                IOUtils.writeFile(groovynanorcFile.toPath(),
                    HTTPUtils.getInstance().get("https://raw.githubusercontent.com/domix/nanorc/master/groovy.nanorc").getBody());
            }

            AccountFactory.setCredentialsLoader(new JbangCredentialsLoader(Path.of(root, "credentials").toString()));

            GroovyEngine scriptEngine = new GroovyEngine();
            scriptEngine.put("ROOT", root);
            scriptEngine.put("availableServices", TNBUtils.getRegisteredServices());
            scriptEngine.put("services", deployedServices);
            scriptEngine.put("leak", wrapMethod(DumpHelper::dumpObject));
            ConfigurationPath configPath = new ConfigurationPath(Paths.get(root), Paths.get(root));
            Printer printer = new DefaultPrinter(scriptEngine, configPath);
            ConsoleEngineImpl consoleEngine = new ConsoleEngineImpl(scriptEngine, printer, workDir, configPath);
            Builtins builtins = new Builtins(
                workDir, configPath, (String fun) -> new ConsoleEngine.WidgetCreator(consoleEngine, fun));
            ReplSystemRegistry systemRegistry = new ReplSystemRegistry(parser, terminal, workDir, configPath);
            Commands myCommands = new Commands(workDir, scriptEngine, consoleEngine);
            systemRegistry.register("groovy", new GroovyCommand(scriptEngine, printer));
            systemRegistry.setCommandRegistries(consoleEngine, builtins, myCommands);
            systemRegistry.addCompleter(scriptEngine.getScriptCompleter());
            systemRegistry.setScriptDescription(scriptEngine::scriptDescription);

            Path jnanorc = configPath.getConfig(DEFAULT_NANORC_FILE);
            scriptEngine.put(VAR_NANORC, jnanorc.toString());
            SyntaxHighlighter commandHighlighter = SyntaxHighlighter.build(jnanorc, "COMMAND");
            SyntaxHighlighter argsHighlighter = SyntaxHighlighter.build(jnanorc, "ARGS");
            SyntaxHighlighter groovyHighlighter = SyntaxHighlighter.build(jnanorc, "groovy");
            SystemHighlighter highlighter =
                new SystemHighlighter(commandHighlighter, argsHighlighter, groovyHighlighter);
            if (!OSUtils.IS_WINDOWS) {
                highlighter.setSpecificHighlighter("!", SyntaxHighlighter.build(jnanorc, "SH-REPL"));
            }
            highlighter.addFileHighlight("nano", "less", "slurp");
            highlighter.addFileHighlight("groovy", "classloader", Arrays.asList("-a", "--add"));
            highlighter.addExternalHighlighterRefresh(printer::refresh);
            highlighter.addExternalHighlighterRefresh(scriptEngine::refresh);
            //
            // LineReader
            //
            reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(systemRegistry.completer())
                .parser(parser)
                .highlighter(highlighter)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
                .variable(LineReader.INDENTATION, 2)
                .variable(LineReader.LIST_MAX, 100)
                .variable(LineReader.HISTORY_FILE, Paths.get(root, "history"))
                .option(LineReader.Option.INSERT_BRACKET, true)
                .option(LineReader.Option.EMPTY_WORD_OPTIONS, false)
                .option(LineReader.Option.USE_FORWARD_SLASH, true) // use forward slash in directory separator
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .option(LineReader.Option.CASE_INSENSITIVE_SEARCH, true)
                .option(LineReader.Option.CASE_INSENSITIVE, true)
                .build();
            if (OSUtils.IS_WINDOWS) {
                reader.setVariable(
                    LineReader.BLINK_MATCHING_PAREN, 0); // if enabled cursor remains in begin parenthesis (gitbash)
            }
            //
            // complete command registries
            //
            consoleEngine.setLineReader(reader);
            builtins.setLineReader(reader);
            myCommands.setLineReader(reader);
            //
            // widgets and console initialization
            //
            final TailTipWidgets tailTip = new TailTipWidgets(reader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.TAIL_TIP);
            KeyMap<Binding> keyMap = reader.getKeyMaps().get("main");
            keyMap.bind(new Reference(Widgets.TAILTIP_TOGGLE), KeyMap.alt("s"));
            tailTip.enable();

            var initFile = Paths.get(root, "init.groovy").toFile();
            if (initFile.exists()) {
                systemRegistry.initialize(initFile);
            }
            for (String line : defaultImports()) {
                scriptEngine.execute(line);
            }

            printHelp(terminal);

            while (true) {
                try {
                    String line = reader.readLine("tnb> ");
                    line = parser.getCommand(line).startsWith("!") ? line.replaceFirst("!", "! ") : line;
                    if ("help".equalsIgnoreCase(line)) {
                        //Bypass the default help command that's baked in to jline
                        printHelp(terminal);
                        continue;
                    } else if ("help?".equalsIgnoreCase(line)) {
                        line = "help";
                    }
                    String finalLine = line;
                    Object result = null;
                    try {
                        if (line.startsWith("def ")) {
                            new AttributedStringBuilder().append("If you want to create a variable don't use def, just ")
                                .styled(new AttributedStyle().foreground(AttributedStyle.CYAN).bold(), "`<name> = <value>`").println(terminal);
                        }
                        result = systemRegistry.execute(finalLine);
                        if (TNBUtils.getRegisteredServices().contains(result)) {
                            line = "deploy " + ((Class<?>) result).getSimpleName();
                            result = systemRegistry.execute(line);
                        }
                    } catch (Exception e) {
                        if (!line.startsWith("deploy")) {
                            //Check for service name case insensitive
                            final Optional<Class<? extends Service<?, ?, ?>>> matchingService =
                                TNBUtils.getRegisteredServices().stream().filter(clazz -> clazz.getSimpleName().equalsIgnoreCase(finalLine))
                                    .findFirst();
                            if (matchingService.isPresent()) {
                                line = "deploy " + matchingService.get().getSimpleName();
                                result = systemRegistry.execute(line);
                            } else {
                                throw e;
                            }
                        } else {
                            throw e;
                        }
                    }
                    if (result != null && DumpHelper.hasDefaultToStringImpl(result)) {
                        //Dump as much info as possible instead of <classname>@<hashcode>
                        consoleEngine.println(DumpHelper.dumpObject(result));
                    } else {
                        consoleEngine.println(result);
                    }
                } catch (UserInterruptException e) {
                    if (reader.getBuffer().length() == 0) {
                        System.exit(0);
                    }
                } catch (EndOfFileException e) {
                    String pl = e.getPartialLine();
                    if (pl != null) { // execute last line from redirected file (required for Windows)
                        try {
                            consoleEngine.println(systemRegistry.execute(pl));
                        } catch (Exception e2) {
                            systemRegistry.trace(e2);
                        }
                    }
                    break;
                } catch (Exception | Error e) {
                    if (e instanceof NullPointerException) {
                        final String[] stackFrames = ExceptionUtils.getStackFrames(e);
                        //Skip tailtips failing - is another jLine quirk :)
                        if (stackFrames.length > 2 && stackFrames[1].contains(
                            "org.jline.widget.TailTipWidgets.tailtipComplete(TailTipWidgets.java:199)")) {
                            continue;
                        }
                    }
                    systemRegistry.trace(e); // print exception and save it to console variable
                }
            }
            systemRegistry.close(); // persist pipeline completer names etc
        } catch (Throwable t) {
            t.printStackTrace();
        }
        //If program finishes normally sometimes it just gets stuck ¯\_(ツ)_/¯
        System.exit(0);
    }

    private static void setupOCP(PrintWriter printer) {
        if (ocpSetup) {
            return;
        }
        try {
            final Config config = OpenShiftConfig.fromKubeconfig(IOUtils.readFile(Path.of(OpenShiftConfig.getKubeconfigFilename())));
            final String namespace = config.getNamespace();
            if ("default".equalsIgnoreCase(namespace)) {
                final boolean autoGenerate = "yes".equals(
                    new OptionSelector(terminal, "Use autogenerated project on " + config.getMasterUrl() + "?", List.of("yes", "no")).select());
                if (autoGenerate) {
                    printer.println("Using project: " + OpenshiftClient.get().getNamespace());
                } else {
                    String projectName = reader.readLine("Set custom name: ");
                    System.setProperty(OpenshiftConfiguration.OPENSHIFT_NAMESPACE, projectName);
                    System.setProperty(OpenshiftConfiguration.OPENSHIFT_NAMESPACE_DELETE, "false");
                }
            }
        } catch (Exception e) {
            new AttributedStringBuilder()
                .style(new AttributedStyle().foreground(AttributedStyle.RED).bold())
                .append("Aborting deployment, failure initializing OCP: ")
                .println(terminal);
            throw new RuntimeException(e);
        }
    }

    private static void printDivider(Terminal terminal) {
        for (int i = 0; i < terminal.getWidth(); i++) {
            terminal.writer().print("=");
        }
        terminal.writer().println();
    }

    private static void printHelp(Terminal terminal) {
        final PrintWriter writer = terminal.writer();
        printDivider(terminal);
        new AttributedStringBuilder().append("Deploy any TNB service from terminal using ")
            .styled(new AttributedStyle().foreground(AttributedStyle.CYAN), "`deploy <service name>`")
            .append(" (use TAB for autocomplete)").println(terminal);
        new AttributedStringBuilder().append("Any groovy code should be executable ")
            .styled(new AttributedStyle().foreground(AttributedStyle.CYAN), "`<deployed service>.validation().method()`")
            .println(terminal);
        writer.println("Create local vars without def/<typename>! just <name> = <expr>");
        boolean isVaultValid =
            TestConfiguration.vaultToken() != null || (TestConfiguration.vaultRoleId() != null && TestConfiguration.vaultSecretId() != null);
        if (System.getProperty(TestConfiguration.CREDENTIALS_FILE) == null || !isVaultValid) {
            writer.println("If you want to use services needing credentials, set 'test.credentials.file' System property to a credentials.yaml file");
        }
        writer.println("use help? to get jline help with available commands");
        printDivider(terminal);
    }

    public static class Commands extends JlineCommandRegistry implements CommandRegistry {
        private LineReader reader;
        private final Supplier<Path> workDir;
        private final GroovyEngine groovyEngine;
        private final ConsoleEngineImpl consoleEngine;

        public Commands(Supplier<Path> workDir, GroovyEngine scriptEngine, ConsoleEngineImpl consoleEngine) {
            super();
            this.workDir = workDir;
            this.groovyEngine = scriptEngine;
            this.consoleEngine = consoleEngine;
            Map<String, CommandMethods> commandExecute = new HashMap<>();
            commandExecute.put("deploy", new CommandMethods(this::deploy, this::deployCompleter));
            commandExecute.put("undeploy", new CommandMethods(this::undeploy, this::undeployCompleter));
            commandExecute.put("setOpenshift",
                new CommandMethods((CommandInput arg) -> System.setProperty(OpenshiftConfiguration.USE_OPENSHIFT, "true"),
                    this::defaultCompleter));
            commandExecute.put("setLocal",
                new CommandMethods((CommandInput arg) -> System.setProperty(OpenshiftConfiguration.USE_OPENSHIFT, "false"),
                    this::defaultCompleter));
            commandExecute.put("!", new CommandMethods(this::shell, this::defaultCompleter));

            registerCommands(commandExecute);
        }

        private void executeCmnd(List<String> args) throws Exception {
            ProcessBuilder builder = new ProcessBuilder();
            List<String> cmdArgs = new ArrayList<>();
            if (OSUtils.IS_WINDOWS) {
                cmdArgs.add("cmd.exe");
                cmdArgs.add("/c");
            } else {
                cmdArgs.add("sh");
                cmdArgs.add("-c");
            }
            cmdArgs.add(String.join(" ", args));
            builder.command(cmdArgs);
            builder.directory(workDir.get().toFile());
            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Thread th = new Thread(streamGobbler);
            th.start();
            int exitCode = process.waitFor();
            th.join();
            if (exitCode != 0) {
                streamGobbler = new StreamGobbler(process.getErrorStream(), System.out::println);
                th = new Thread(streamGobbler);
                th.start();
                th.join();
                throw new Exception("Error occurred in shell!");
            }
        }

        private void shell(CommandInput input) {
            final String[] usage = {
                "!<command> -  execute shell command",
                "Usage: !<command>",
                "  -? --help                       Displays command help"
            };
            if (input.args().length == 1 && (input.args()[0].equals("-?") || input.args()[0].equals("--help"))) {
                try {
                    parseOptions(usage, input.args());
                } catch (Exception e) {
                    saveException(e);
                }
            } else {
                List<String> argv = new ArrayList<>(Arrays.asList(input.args()));
                if (!argv.isEmpty()) {
                    try {
                        executeCmnd(argv);
                    } catch (Exception e) {
                        saveException(e);
                    }
                }
            }
        }

        @Override
        public CmdDesc commandDescription(List<String> args) {
            try {
                return super.commandDescription(args);
            } catch (IllegalArgumentException e) {
                return new CmdDesc();
            }
        }

        private List<Completer> undeployCompleter(String s) {
            return Collections.singletonList(
                new StringsCompleter(
                    () -> TNBUtils.getServices().stream().map(svc -> svc.getClass().getSimpleName().toLowerCase()).collect(Collectors.toList())));
        }

        private void undeploy(CommandInput commandInput) {
            var name = commandInput.args()[0];
            if (name.equals("--help")) {
                //JLine sends --help argument to figure out the description through an Exception handler
                return;
            }
            final Object o = groovyEngine.get(name);
            if (o instanceof Service) {
                groovyEngine.del(name);
                TNBUtils.removeService((Service<?, ?, ?>) o);
            } else {
                terminal().writer().println("Can only undeploy services");
            }
        }

        private void deploy(CommandInput input) {
            final String[] usage = {
                "deploy - deploy a TNB service",
                "Usage: deploy [SERVICE]",
                "\t-? --help\t\t\t Displays command help",
                "\t-l --local\t\t\t Deploy locally",
                "\t-o --openshift\t\t\t Deploy on openshift"
            };
            if (input.args().length > 0 && input.args()[0].equals("--help")) {
                //JLine sends --help argument to figure out the description through an Exception handler
                return;
            }
            try {
                Options opt = parseOptions(usage, input.xargs());
                boolean useOCP = false;
                final List<String> argv = opt.args();
                if (argv.size() == 0) {
                    terminal().writer().println(opt.usage());
                    return;
                }
                if (opt.isSet("openshift") || opt.isSet("local") || argv.contains("openshift") || argv.contains("local")) {
                    if (opt.isSet("openshift") && opt.isSet("local") || (argv.contains("openshift") && argv.contains("local"))) {
                        throw new IllegalStateException("Choose just one deployment type smh");
                    } else {
                        useOCP = opt.isSet("openshift") || argv.contains("openshift");
                    }
                }
                useOCP |= Boolean.parseBoolean(System.getProperty(OpenshiftConfiguration.USE_OPENSHIFT, "false"));
                if (useOCP) {
                    setupOCP(terminal().writer());
                }
                for (String svc : argv) {
                    var service = TNBUtils.deployService(svc, useOCP);
                    if (service != null) {
                        var svcName = service.getClass().getSimpleName().toLowerCase();
                        groovyEngine.put(svcName, service);
                        new AttributedStringBuilder().append("Service " + svcName + " ready! You can use `")
                            .styled(AttributedStyle.BOLD, svcName)
                            .append("`  to call its methods.")
                            .println(terminal);
                        if (!(service.account() instanceof NoAccount)) {
                            consoleEngine.println("Associated account with service " + svc);
                            consoleEngine.println(DumpHelper.dumpObject(service.account()));
                        }
                    }
                }
            } catch (Exception e) {
                saveException(e);
            }
        }

        private List<Completer> deployCompleter(String line) {
            return List.of(
                new StringsCompleter(() -> TNBUtils.getRegisteredServices().stream().map(Class::getSimpleName).collect(Collectors.toList())),
                new StringsCompleter("--local", "--openshift"));
        }

        public void setLineReader(LineReader reader) {
            this.reader = reader;
        }

        private Terminal terminal() {
            return reader.getTerminal();
        }

        private static class StreamGobbler implements Runnable {
            private final InputStream inputStream;
            private final Consumer<String> consumer;

            StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
                this.inputStream = inputStream;
                this.consumer = consumer;
            }

            @Override
            public void run() {
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .forEach(consumer);
            }
        }
    }

    public static class ReplSystemRegistry extends SystemRegistryImpl {
        public ReplSystemRegistry(
            Parser parser, Terminal terminal, Supplier<Path> workDir, ConfigurationPath configPath) {
            super(parser, terminal, workDir, configPath);
        }

        @Override
        public boolean isCommandOrScript(String command) {
            return command.startsWith("!") || super.isCommandOrScript(command);
        }
    }

    public static class TNBUtils {

        private static final Set<Class<? extends Service<?, ?, ?>>> registeredServices = TNBUtils.initSvcClasses();
        private static Set<String> serviceClassnames;

        public static Service<?, ?, ?> deployService(String svc, Boolean useOCP) throws Exception {
            String previousUseOCP = System.getProperty(OpenshiftConfiguration.USE_OPENSHIFT, "false");

            System.setProperty(OpenshiftConfiguration.USE_OPENSHIFT, useOCP.toString());
            List<Class<? extends Service<?, ?, ?>>> availableSvcs =
                getRegisteredServices().stream().filter(clazz -> clazz.getSimpleName().toLowerCase().contains(svc.toLowerCase()))
                    .collect(Collectors.toList());
            if (availableSvcs.size() != 1) {
                availableSvcs =
                    availableSvcs.stream().filter(clazz -> clazz.getSimpleName().equalsIgnoreCase(svc)).collect(Collectors.toList());
                if (availableSvcs.size() > 1) {
                    throw new IllegalStateException("Too many services");
                } else if (availableSvcs.isEmpty()) {
                    throw new IllegalArgumentException("No matching service found for " + svc);
                }
            }
            final Class<? extends Service<?, ?, ?>> svcClazz = availableSvcs.get(0);
            terminal.writer().println("Deploying service " + svcClazz.getSimpleName());
            terminal.writer().flush();
            var service = ServiceFactory.create(svcClazz);

            deployedServices.add(service);
            service.beforeAll(null);

            System.setProperty(OpenshiftConfiguration.USE_OPENSHIFT, previousUseOCP);

            return service;
        }

        public static Set<String> findAllRegisteredServices() {
            if (serviceClassnames == null) {
                serviceClassnames = new HashSet<>();
                try {
                    final List<URL> tnbServices = Collections.list(Thread.currentThread().getContextClassLoader().getResources("META-INF/services/"))
                        .stream()
                        .filter(url -> url.getPath().contains("tnb"))
                        .collect(Collectors.toList());

                    for (URL tnbDependency : tnbServices) {
                        final String jarPath = tnbDependency.getPath().replace("file:", "").replace("!/META-INF/services/", "");
                        try (ZipFile zip = new ZipFile(jarPath)) {
                            serviceClassnames.addAll(Collections.list(zip.entries()).stream().map(ZipEntry::getName)
                                .filter(name -> name.startsWith("META-INF/services/software.tnb")).map(s -> s.replace("META-INF/services/", ""))
                                .collect(
                                    Collectors.toList()));
                        } catch (IOException e) {
                            System.err.println("Failed to load System-X service: " + e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return serviceClassnames;
        }

        public static Set<Class<? extends Service<?, ?, ?>>> getRegisteredServices() {
            return registeredServices;
        }

        @SuppressWarnings("unchecked")
        public static Set<Class<? extends Service<?, ?, ?>>> initSvcClasses() {
            return findAllRegisteredServices().stream()
                .map(svc -> Try.call(() -> (Class<Service<?, ?, ?>>) Class.forName(svc))
                    .ifFailure(ex -> terminal.writer().println("Failure while loading class: " + ex.getMessage()))
                    .toOptional().orElse(null)
                )
                .collect(Collectors.toSet());
        }

        public static void removeService(Service<?, ?, ?> service) {
            deployedServices.remove(service);
            try {
                service.afterAll(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static List<Service<?, ?, ?>> getServices() {
            return deployedServices;
        }
    }

    public static class DumpHelper {

        /**
         * Gets all public fields and methods returning String values.
         * Used primarily for Account classes that have hardcoded values in methods.
         *
         * @param obj object to be dumped
         * @return map where keys are field/method names and values its values
         */
        public static Map<String, Object> dumpObject(Object obj) {
            final List<Method> stringMethods = ReflectionUtils.findMethods(obj.getClass(),
                m -> ReflectionUtils.isPublic(m) && m.getParameterCount() == 0 && m.getReturnType() == String.class);
            final Map<String, Object> fields =
                ReflectionUtils.findFields(obj.getClass(), ReflectionUtils::isPublic, ReflectionUtils.HierarchyTraversalMode.TOP_DOWN).stream()
                    .collect(Collectors.toMap(Field::getName,
                        f -> ReflectionUtils.tryToReadFieldValue(f, obj).toOptional().orElse("Failed to get value")));
            final Map<String, Object> methods = stringMethods.stream()
                .collect(Collectors.toMap(Method::getName, m -> Try.call(() -> m.invoke(obj)).toOptional().orElse("Failed to get value")));
            return MapUtils.merge(fields, methods);
        }

        public static boolean hasDefaultToStringImpl(Object result) {
            return result.toString().equals(result.getClass().getName() + "@" + Integer.toHexString(result.hashCode()));
        }
    }
}
