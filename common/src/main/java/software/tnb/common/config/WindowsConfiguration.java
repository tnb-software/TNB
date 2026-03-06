package software.tnb.common.config;

public class WindowsConfiguration extends Configuration {
    public static final String CYGWIN_BASH_PATH = "windows.cygwin.bash.path";

    public static String cygwinBashPath() {
        return getProperty(CYGWIN_BASH_PATH, "C:/cygwin/bin/bash.exe");
    }

    public static boolean isWindows() {
        return getProperty("os.name").toLowerCase().contains("win");
    }
}
