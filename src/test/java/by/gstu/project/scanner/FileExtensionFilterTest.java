package by.gstu.project.scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileExtensionFilterTest {

    @Test
    void shouldMatchAllowedExtensionsCaseInsensitivelyAndIgnoreDirectories(@TempDir Path tempDir) throws IOException {
        Path javaFile = Files.createFile(tempDir.resolve("Main.java"));
        Path upperMdFile = Files.createFile(tempDir.resolve("README.MD"));
        Path txtFile = Files.createFile(tempDir.resolve("notes.txt"));
        Path subDir = Files.createDirectory(tempDir.resolve("dir.java"));

        FileExtensionFilter filter = new FileExtensionFilter(List.of(".java", "md"));

        assertTrue(filter.matches(javaFile));
        assertTrue(filter.matches(upperMdFile));
        assertFalse(filter.matches(txtFile));
        assertFalse(filter.matches(subDir));
        assertEquals(List.of(".java", ".md"), filter.getAllowedExtensions());
    }

    @Test
    void shouldRejectEmptyExtensionsList() {
        assertThrows(IllegalArgumentException.class, () -> new FileExtensionFilter(List.of()));
    }
}
