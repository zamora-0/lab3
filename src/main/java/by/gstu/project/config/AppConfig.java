package by.gstu.project.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Immutable configuration model for the TODO/FIXME source code scanner.
 *
 * <p>Holds the root directory to scan, the list of allowed file extensions,
 * and the list of keywords to search for during scanning.</p>
 */
public final class AppConfig {

    /**
     * Default directory path to scan when {@code scan.source.directory} is not specified.
     */
    public static final String DEFAULT_SOURCE_DIRECTORY = "./src";

    /**
     * Default file extensions included in the scan when {@code scan.file.extensions} is not specified.
     */
    public static final List<String> DEFAULT_FILE_EXTENSIONS = List.of(".java", ".kt", ".xml", ".md");

    /**
     * Default keywords to look up when {@code scan.keywords} is not specified.
     */
    public static final List<String> DEFAULT_KEYWORDS = List.of("TODO", "FIXME");

    private final Path sourceDirectory;
    private final List<String> fileExtensions;
    private final List<String> keywords;

    /**
     * Creates a new immutable {@code AppConfig} instance.
     *
     * @param sourceDirectory root directory to scan recursively; must not be {@code null}
     * @param fileExtensions  list of file extensions (e.g. {@code .java}, {@code .md}); must not be {@code null} or empty
     * @param keywords        list of keywords to search for; must not be {@code null} or empty
     * @throws IllegalArgumentException if {@code fileExtensions} or {@code keywords} is empty
     */
    public AppConfig(Path sourceDirectory, List<String> fileExtensions, List<String> keywords) {
        this.sourceDirectory = Objects.requireNonNull(sourceDirectory, "sourceDirectory must not be null");
        Objects.requireNonNull(fileExtensions, "fileExtensions must not be null");
        Objects.requireNonNull(keywords, "keywords must not be null");

        if (fileExtensions.isEmpty()) {
            throw new IllegalArgumentException("fileExtensions must not be empty");
        }
        if (keywords.isEmpty()) {
            throw new IllegalArgumentException("keywords must not be empty");
        }

        this.fileExtensions = List.copyOf(fileExtensions);
        this.keywords = List.copyOf(keywords);
    }

    /**
     * Returns an {@code AppConfig} populated with the default application settings.
     *
     * @return default {@code AppConfig} instance
     */
    public static AppConfig defaults() {
        return new AppConfig(
                Paths.get(DEFAULT_SOURCE_DIRECTORY),
                DEFAULT_FILE_EXTENSIONS,
                DEFAULT_KEYWORDS
        );
    }

    /**
     * Returns the root directory configured for recursive scanning.
     *
     * @return source directory {@link Path}
     */
    public Path getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * Returns an unmodifiable list of normalized file extensions to scan.
     *
     * @return list of file extensions starting with a dot in lowercase
     */
    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    /**
     * Returns an unmodifiable list of keywords to search for.
     *
     * @return list of search keywords
     */
    public List<String> getKeywords() {
        return keywords;
    }
}
