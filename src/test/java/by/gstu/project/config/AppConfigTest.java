package by.gstu.project.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppConfigTest {

    @Test
    void defaultsShouldProvideExpectedValues() {
        AppConfig defaults = AppConfig.defaults();

        assertEquals(Paths.get("./src"), defaults.getSourceDirectory());
        assertEquals(List.of(".java", ".kt", ".xml", ".md"), defaults.getFileExtensions());
        assertEquals(List.of("TODO", "FIXME"), defaults.getKeywords());
    }

    @Test
    void constructorShouldMakeDefensiveUnmodifiableCopies() {
        List<String> extensions = new ArrayList<>(List.of(".java"));
        List<String> keywords = new ArrayList<>(List.of("TODO"));

        AppConfig config = new AppConfig(Paths.get("./src"), extensions, keywords);
        extensions.add(".kt");
        keywords.add("FIXME");

        assertEquals(List.of(".java"), config.getFileExtensions());
        assertEquals(List.of("TODO"), config.getKeywords());
        assertThrows(UnsupportedOperationException.class, () -> config.getFileExtensions().add(".xml"));
        assertThrows(UnsupportedOperationException.class, () -> config.getKeywords().add("BUG"));
    }

    @Test
    void constructorShouldRejectEmptyExtensionsOrKeywords() {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig(Paths.get("./src"), List.of(), List.of("TODO")));
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig(Paths.get("./src"), List.of(".java"), List.of()));
    }
}
