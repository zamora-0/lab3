package by.gstu.project.scanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Filters filesystem paths by checking whether they are regular files
 * matching one of the configured file extensions (case-insensitively).
 */
public class FileExtensionFilter {

    private final List<String> allowedExtensions;

    /**
     * Creates a new {@code FileExtensionFilter} for the specified list of extensions.
     *
     * @param allowedExtensions list of extensions (e.g. {@code .java}, {@code .md}); must not be {@code null} or empty
     * @throws IllegalArgumentException if {@code allowedExtensions} is empty
     */
    public FileExtensionFilter(List<String> allowedExtensions) {
        Objects.requireNonNull(allowedExtensions, "allowedExtensions must not be null");
        if (allowedExtensions.isEmpty()) {
            throw new IllegalArgumentException("allowedExtensions must not be empty");
        }
        this.allowedExtensions = allowedExtensions.stream()
                .map(ext -> {
                    String lower = ext.trim().toLowerCase(Locale.ROOT);
                    return lower.startsWith(".") ? lower : "." + lower;
                })
                .collect(java.util.stream.Collectors.toUnmodifiableList());
    }

    /**
     * Checks whether the given path is a regular file whose name ends with one of the allowed extensions.
     *
     * @param path path to evaluate; must not be {@code null}
     * @return {@code true} if the path is a regular file with an allowed extension, {@code false} otherwise
     */
    public boolean matches(Path path) {
        Objects.requireNonNull(path, "path must not be null");
        if (!Files.isRegularFile(path)) {
            return false;
        }
        Path fileName = path.getFileName();
        if (fileName == null) {
            return false;
        }
        String lowerName = fileName.toString().toLowerCase(Locale.ROOT);
        for (String ext : allowedExtensions) {
            if (lowerName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an unmodifiable list of normalized allowed extensions.
     *
     * @return normalized extensions in lowercase starting with a dot
     */
    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }
}
