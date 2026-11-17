package by.gstu.project.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

/**
 * Loads {@link AppConfig} from a {@code .properties} file with graceful fallback
 * to default values when the file or individual keys are missing.
 */
public class ConfigLoader {

    /**
     * Default configuration file name in the working directory.
     */
    public static final String DEFAULT_CONFIG_PATH = "app.properties";

    /**
     * Property key specifying the directory to scan.
     */
    public static final String KEY_SOURCE_DIRECTORY = "scan.source.directory";

    /**
     * Property key specifying comma-separated file extensions.
     */
    public static final String KEY_FILE_EXTENSIONS = "scan.file.extensions";

    /**
     * Property key specifying comma-separated search keywords.
     */
    public static final String KEY_KEYWORDS = "scan.keywords";

    /**
     * Loads configuration from the default {@code app.properties} file in the working directory.
     * If the file does not exist, returns {@link AppConfig#defaults()}.
     *
     * @return loaded {@link AppConfig}
     * @throws IOException if the file exists but cannot be read
     */
    public AppConfig load() throws IOException {
        return load(Paths.get(DEFAULT_CONFIG_PATH));
    }

    /**
     * Loads configuration from the specified file path.
     * If the file does not exist, falls back to {@link AppConfig#defaults()}.
     *
     * @param configPath path to the properties file; must not be {@code null}
     * @return loaded {@link AppConfig}
     * @throws IOException if the file exists but cannot be read
     */
    public AppConfig load(Path configPath) throws IOException {
        Objects.requireNonNull(configPath, "configPath must not be null");
        if (!Files.exists(configPath)) {
            return AppConfig.defaults();
        }

        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(configPath), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return load(properties);
    }

    /**
     * Builds an {@link AppConfig} from the given {@link Properties} object,
     * falling back to default values for missing or blank properties.
     *
     * @param properties properties instance; must not be {@code null}
     * @return constructed {@link AppConfig}
     */
    public AppConfig load(Properties properties) {
        Objects.requireNonNull(properties, "properties must not be null");

        String rawDir = properties.getProperty(KEY_SOURCE_DIRECTORY);
        Path sourceDir = (rawDir == null || rawDir.trim().isEmpty())
                ? Paths.get(AppConfig.DEFAULT_SOURCE_DIRECTORY)
                : Paths.get(rawDir.trim());

        List<String> extensions = parseExtensions(properties.getProperty(KEY_FILE_EXTENSIONS));
        if (extensions.isEmpty()) {
            extensions = AppConfig.DEFAULT_FILE_EXTENSIONS;
        }

        List<String> keywords = parseCommaSeparatedList(properties.getProperty(KEY_KEYWORDS));
        if (keywords.isEmpty()) {
            keywords = AppConfig.DEFAULT_KEYWORDS;
        }

        return new AppConfig(sourceDir, extensions, keywords);
    }

    private List<String> parseExtensions(String rawExtensions) {
        List<String> parsed = parseCommaSeparatedList(rawExtensions);
        List<String> normalized = new ArrayList<>(parsed.size());
        for (String ext : parsed) {
            String lower = ext.toLowerCase(Locale.ROOT);
            normalized.add(lower.startsWith(".") ? lower : "." + lower);
        }
        return normalized;
    }

    private List<String> parseCommaSeparatedList(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return List.of();
        }
        String[] parts = rawValue.split(",");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
