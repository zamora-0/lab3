package by.gstu.project;

import by.gstu.project.config.AppConfig;
import by.gstu.project.config.ConfigLoader;
import by.gstu.project.model.ScanReport;
import by.gstu.project.report.ConsoleReportPrinter;
import by.gstu.project.report.ReportFormatter;
import by.gstu.project.report.ReportPrinter;
import by.gstu.project.scanner.TodoScanner;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Application entry point for the TODO/FIXME source code scanner.
 *
 * <p>Reads configuration from {@code app.properties} (or a custom file path passed as the
 * first command-line argument or via {@code -Dconfig.file=...}), recursively scans the
 * configured source directory, and prints the grouped report to {@code stdout}.</p>
 */
public final class Main {

    private static final String CONFIG_SYSTEM_PROPERTY = "config.file";

    private Main() {
    }

    /**
     * Starts the application from the command line.
     *
     * @param args optional command-line arguments; {@code args[0]} may specify the path to {@code app.properties}
     */
    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Executes the scanner workflow and returns a process exit code without terminating the JVM.
     *
     * @param args command-line arguments
     * @param out  standard output stream for the report
     * @param err  error output stream for diagnostic messages
     * @return {@code 0} on success, {@code 1} on configuration or I/O error
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        // TODO: Add CLI options (--dir, --keywords, --ext) to override app.properties parameters
        Path configPath = resolveConfigPath(args);
        ConfigLoader configLoader = new ConfigLoader();

        try {
            AppConfig config = configLoader.load(configPath);
            TodoScanner scanner = new TodoScanner(config);
            ScanReport report = scanner.scan();

            ReportPrinter printer = new ConsoleReportPrinter(out, new ReportFormatter());
            printer.print(report);
            return 0;
        } catch (IllegalArgumentException | IOException ex) {
            err.println("Error scanning source directory: " + ex.getMessage());
            return 1;
        }
    }

    private static Path resolveConfigPath(String[] args) {
        if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            return Paths.get(args[0].trim());
        }
        String sysProp = System.getProperty(CONFIG_SYSTEM_PROPERTY);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return Paths.get(sysProp.trim());
        }
        return Paths.get(ConfigLoader.DEFAULT_CONFIG_PATH);
    }
}
