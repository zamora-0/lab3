package by.gstu.project.report;

import by.gstu.project.model.ScanReport;

/**
 * Defines a contract for outputting a {@link ScanReport}.
 */
public interface ReportPrinter {

    /**
     * Outputs the given scan report to the target destination.
     *
     * @param report scan report to print; must not be {@code null}
     */
    void print(ScanReport report);
}
