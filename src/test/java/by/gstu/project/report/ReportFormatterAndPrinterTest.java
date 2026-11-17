package by.gstu.project.report;

import by.gstu.project.model.ScanReport;
import by.gstu.project.model.TodoItem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportFormatterAndPrinterTest {

    @Test
    void shouldFormatReportGroupedByFileWithLineNumbers() {
        String nl = System.lineSeparator();
        ScanReport report = new ScanReport(Paths.get("."), List.of(
                new TodoItem("src/main/java/MyClass.java", 3, "TODO", "TODO: Need to refactor this"),
                new TodoItem("src/main/java/MyClass.java", 5, "FIXME", "FIXME: This is slow"),
                new TodoItem("README.md", 10, "TODO", "TODO: Remember to update screenshots")
        ));

        ReportFormatter formatter = new ReportFormatter();
        String expected = ""
                + "Found 3 issues:" + nl
                + nl
                + "[README.md]" + nl
                + "  L10: TODO: Remember to update screenshots" + nl
                + nl
                + "[src/main/java/MyClass.java]" + nl
                + "  L3: TODO: Need to refactor this" + nl
                + "  L5: FIXME: This is slow" + nl;

        assertEquals(expected, formatter.format(report));
    }

    @Test
    void shouldFormatEmptyReportAndPrintToStream() {
        String nl = System.lineSeparator();
        ScanReport emptyReport = new ScanReport(Paths.get("."), List.of());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(baos, true, StandardCharsets.UTF_8);

        ConsoleReportPrinter printer = new ConsoleReportPrinter(stream, new ReportFormatter());
        printer.print(emptyReport);

        assertEquals("Found 0 issues." + nl, baos.toString(StandardCharsets.UTF_8));
    }
}
