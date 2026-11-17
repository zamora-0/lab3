package by.gstu.project.report;

import by.gstu.project.model.ScanReport;

import java.io.PrintStream;
import java.util.Objects;

/**
 * {@link ReportPrinter} implementation that formats a {@link ScanReport} using
 * {@link ReportFormatter} and writes it to a {@link PrintStream} (defaults to {@code System.out}).
 */
public class ConsoleReportPrinter implements ReportPrinter {

    private final PrintStream output;
    private final ReportFormatter formatter;

    /**
     * Creates a {@code ConsoleReportPrinter} writing to {@code System.out}.
     */
    public ConsoleReportPrinter() {
        this(System.out, new ReportFormatter());
    }

    /**
     * Creates a {@code ConsoleReportPrinter} with a custom output stream and formatter.
     *
     * @param output    destination stream; must not be {@code null}
     * @param formatter report formatter; must not be {@code null}
     */
    public ConsoleReportPrinter(PrintStream output, ReportFormatter formatter) {
        this.output = Objects.requireNonNull(output, "output must not be null");
        this.formatter = Objects.requireNonNull(formatter, "formatter must not be null");
    }

    /**
     * Formats and prints the scan report to the configured {@link PrintStream}.
     *
     * @param report scan report to print; must not be {@code null}
     */
    @Override
    public void print(ScanReport report) {
        Objects.requireNonNull(report, "report must not be null");
        output.print(formatter.format(report));
    }
}
