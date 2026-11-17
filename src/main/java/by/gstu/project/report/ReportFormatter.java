package by.gstu.project.report;

import by.gstu.project.model.ScanReport;
import by.gstu.project.model.TodoItem;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Formats a {@link ScanReport} into a human-readable multi-line text report
 * grouped by file path with {@code L<line>} prefixes.
 */
public class ReportFormatter {

    /**
     * Formats the given {@link ScanReport} into a printable string.
     *
     * <p>Example output:</p>
     * <pre>
     * Found 2 issues:
     *
     * [src/main/java/MyClass.java]
     *   L3: TODO: Need to refactor this
     *   L5: FIXME: This is slow
     * </pre>
     *
     * @param report scan report to format; must not be {@code null}
     * @return formatted text report
     */
    public String format(ScanReport report) {
        Objects.requireNonNull(report, "report must not be null");
        String nl = System.lineSeparator();
        int total = report.getTotalIssues();

        if (total == 0) {
            return "Found 0 issues." + nl;
        }

        // FIXME: Support configurable output formats (e.g., JSON or Markdown) in addition to plain text
        StringBuilder sb = new StringBuilder();
        sb.append("Found ")
                .append(total)
                .append(total == 1 ? " issue:" : " issues:")
                .append(nl);

        for (Map.Entry<String, List<TodoItem>> entry : report.getIssuesByFile().entrySet()) {
            sb.append(nl)
                    .append('[').append(entry.getKey()).append(']')
                    .append(nl);
            for (TodoItem item : entry.getValue()) {
                sb.append("  L")
                        .append(item.getLineNumber())
                        .append(": ")
                        .append(item.getText())
                        .append(nl);
            }
        }

        return sb.toString();
    }
}
