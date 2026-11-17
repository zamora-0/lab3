package by.gstu.project.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigLoaderTest {

    private final ConfigLoader loader = new ConfigLoader();

    @Test
    void shouldFallbackToDefaultsWhenFileDoesNotExist(@TempDir Path tempDir) throws IOException {
        Path missingFile = tempDir.resolve("missing.properties");

        AppConfig config = loader.load(missingFile);

        assertEquals(Paths.get(AppConfig.DEFAULT_SOURCE_DIRECTORY), config.getSourceDirectory());
        assertEquals(AppConfig.DEFAULT_FILE_EXTENSIONS, config.getFileExtensions());
        assertEquals(AppConfig.DEFAULT_KEYWORDS, config.getKeywords());
    }

    @Test
    void shouldLoadAndNormalizePropertiesFromFile(@TempDir Path tempDir) throws IOException {
        Path configFile = tempDir.resolve("app.properties");
        String content = ""
                + "scan.source.directory = ./custom-src\n"
                + "scan.file.extensions = java, .KT , xml , \n"
                + "scan.keywords = TODO , FIXME , bug \n";
        Files.writeString(configFile, content, StandardCharsets.UTF_8);

        AppConfig config = loader.load(configFile);

        assertEquals(Paths.get("./custom-src"), config.getSourceDirectory());
        assertEquals(List.of(".java", ".kt", ".xml"), config.getFileExtensions());
        assertEquals(List.of("TODO", "FIXME", "bug"), config.getKeywords());
    }

    @Test
    void shouldFallbackForBlankKeysInProperties() {
        Properties props = new Properties();
        props.setProperty(ConfigLoader.KEY_SOURCE_DIRECTORY, "   ");
        props.setProperty(ConfigLoader.KEY_FILE_EXTENSIONS, " , ");
        props.setProperty(ConfigLoader.KEY_KEYWORDS, "");

        AppConfig config = loader.load(props);

        assertEquals(Paths.get(AppConfig.DEFAULT_SOURCE_DIRECTORY), config.getSourceDirectory());
        assertEquals(AppConfig.DEFAULT_FILE_EXTENSIONS, config.getFileExtensions());
        assertEquals(AppConfig.DEFAULT_KEYWORDS, config.getKeywords());
    }
}
