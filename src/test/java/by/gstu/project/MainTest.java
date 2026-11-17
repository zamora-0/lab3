package by.gstu.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void shouldRunEndToEndWithConfigArgument(@TempDir Path tempDir) throws IOException {
        Path srcDir = Files.createDirectory(tempDir.resolve("src"));
        Files.writeString(srcDir.resolve("Demo.kt"), "// TODO: Implement feature\n", StandardCharsets.UTF_8);

        Path configFile = tempDir.resolve("custom.properties");
        Files.writeString(configFile,
                "scan.source.directory=" + srcDir.toAbsolutePath().toString().replace("\\", "/") + "\n"
                        + "scan.file.extensions=.kt\n"
                        + "scan.keywords=TODO\n",
                StandardCharsets.UTF_8);

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errBytes = new ByteArrayOutputStream();

        int exitCode = Main.run(
                new String[]{configFile.toString()},
                new PrintStream(outBytes, true, StandardCharsets.UTF_8),
                new PrintStream(errBytes, true, StandardCharsets.UTF_8)
        );

        String output = outBytes.toString(StandardCharsets.UTF_8);
        assertEquals(0, exitCode);
        assertTrue(errBytes.toString(StandardCharsets.UTF_8).isEmpty());
        assertTrue(output.contains("Found 1 issue:"));
        assertTrue(output.contains("[Demo.kt]"));
        assertTrue(output.contains("L1: TODO: Implement feature"));
    }

    @Test
    void shouldReturnNonZeroExitCodeWhenScanDirectoryDoesNotExist(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("bad.properties");
        Files.writeString(configFile,
                "scan.source.directory=" + tempDir.resolve("missing").toString().replace("\\", "/") + "\n",
                StandardCharsets.UTF_8);

        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errBytes = new ByteArrayOutputStream();

        int exitCode = Main.run(
                new String[]{configFile.toString()},
                new PrintStream(outBytes, true, StandardCharsets.UTF_8),
                new PrintStream(errBytes, true, StandardCharsets.UTF_8)
        );

        assertEquals(1, exitCode);
        assertTrue(errBytes.toString(StandardCharsets.UTF_8).contains("Error scanning source directory"));
    }
}
