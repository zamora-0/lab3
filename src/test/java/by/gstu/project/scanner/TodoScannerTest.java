package by.gstu.project.scanner;

import by.gstu.project.config.AppConfig;
import by.gstu.project.model.ScanReport;
import by.gstu.project.model.TodoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoScannerTest {

    @Test
    void shouldRecursivelyScanFilterExtensionsAndMatchCaseInsensitively(@TempDir Path tempDir) throws IOException {
        Path nestedDir = Files.createDirectories(tempDir.resolve("sub/pkg"));
        Path javaFile = nestedDir.resolve("MyClass.java");
        Files.writeString(javaFile, ""
                + "public class MyClass {\n"
                + "    // todo: Need to refactor this\n"
                + "    String ignored = \"// TODO: not a comment\";\n"
                + "    /* FixMe: This is slow */\n"
                + "    public void prefixMethod() { /* PREFIX should not match */ }\n"
                + "}\n", StandardCharsets.UTF_8);

        Path mdFile = tempDir.resolve("README.md");
        Files.writeString(mdFile, ""
                + "# Title\n"
                + "TODO: Remember to update screenshots\n", StandardCharsets.UTF_8);

        Path ignoredFile = tempDir.resolve("ignored.txt");
        Files.writeString(ignoredFile, "TODO: Must be ignored due to extension\n", StandardCharsets.UTF_8);

        AppConfig config = new AppConfig(
                tempDir,
                List.of(".java", ".md"),
                List.of("TODO", "FIXME", "FIX")
        );
        TodoScanner scanner = new TodoScanner(config);

        ScanReport report = scanner.scan();

        assertEquals(3, report.getTotalIssues());
        assertEquals(2, report.getAffectedFilesCount());

        List<TodoItem> mdItems = report.getIssuesByFile().get("README.md");
        assertEquals(1, mdItems.size());
        assertEquals(2, mdItems.get(0).getLineNumber());
        assertEquals("TODO: Remember to update screenshots", mdItems.get(0).getText());

        List<TodoItem> javaItems = report.getIssuesByFile().get("sub/pkg/MyClass.java");
        assertEquals(2, javaItems.size());
        assertEquals(2, javaItems.get(0).getLineNumber());
        assertEquals("todo: Need to refactor this", javaItems.get(0).getText());
        assertEquals(4, javaItems.get(1).getLineNumber());
        assertEquals("FixMe: This is slow", javaItems.get(1).getText());
    }

    @Test
    void shouldReturnEmptyReportWhenNoMatchesFound(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("Clean.java"), "public class Clean {}\n", StandardCharsets.UTF_8);

        TodoScanner scanner = new TodoScanner(new AppConfig(tempDir, List.of(".java"), List.of("TODO")));
        ScanReport report = scanner.scan();

        assertTrue(report.isEmpty());
        assertEquals(0, report.getTotalIssues());
    }

    @Test
    void shouldThrowExceptionWhenDirectoryDoesNotExist(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("does-not-exist");
        TodoScanner scanner = new TodoScanner(new AppConfig(nonExistent, List.of(".java"), List.of("TODO")));

        assertThrows(IllegalArgumentException.class, scanner::scan);
    }
}
