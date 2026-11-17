package by.gstu.project.model;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScanReportAndTodoItemTest {

    @Test
    void todoItemShouldValidateArguments() {
        assertThrows(IllegalArgumentException.class, () -> new TodoItem("", 1, "TODO", "text"));
        assertThrows(IllegalArgumentException.class, () -> new TodoItem("a.java", 0, "TODO", "text"));
        assertThrows(IllegalArgumentException.class, () -> new TodoItem("a.java", 1, " ", "text"));
    }

    @Test
    void scanReportShouldGroupBySortedFileAndLineNumber() {
        TodoItem item1 = new TodoItem("src/B.java", 10, "TODO", "TODO: second line");
        TodoItem item2 = new TodoItem("src/B.java", 3, "FIXME", "FIXME: first line");
        TodoItem item3 = new TodoItem("README.md", 5, "TODO", "TODO: update docs");

        ScanReport report = new ScanReport(Paths.get("."), List.of(item1, item2, item3));

        assertFalse(report.isEmpty());
        assertEquals(3, report.getTotalIssues());
        assertEquals(2, report.getAffectedFilesCount());
        assertEquals(List.of("README.md", "src/B.java"), List.copyOf(report.getIssuesByFile().keySet()));
        assertEquals(List.of(item2, item1), report.getIssuesByFile().get("src/B.java"));
        assertThrows(UnsupportedOperationException.class, () -> report.getIssuesByFile().clear());
    }

    @Test
    void emptyScanReportShouldHaveZeroCounts() {
        ScanReport report = new ScanReport(Paths.get("."), List.of());

        assertTrue(report.isEmpty());
        assertEquals(0, report.getTotalIssues());
        assertEquals(0, report.getAffectedFilesCount());
    }
}
