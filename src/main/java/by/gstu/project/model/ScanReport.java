package by.gstu.project.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Aggregated scan report containing all {@link TodoItem} matches grouped by file path.
 *
 * <p>Files are ordered lexicographically by path, and items within each file are
 * ordered by ascending line number.</p>
 */
public final class ScanReport {

    private final Path rootDirectory;
    private final Map<String, List<TodoItem>> issuesByFile;
    private final int totalIssues;

    /**
     * Creates a new {@code ScanReport} from a flat list of matched items.
     *
     * @param rootDirectory root directory that was scanned; must not be {@code null}
     * @param items         list of discovered {@link TodoItem} entries; must not be {@code null}
     */
    public ScanReport(Path rootDirectory, List<TodoItem> items) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory, "rootDirectory must not be null");
        Objects.requireNonNull(items, "items must not be null");

        Map<String, List<TodoItem>> grouped = new TreeMap<>();
        for (TodoItem item : items) {
            grouped.computeIfAbsent(item.getFilePath(), ignored -> new java.util.ArrayList<>()).add(item);
        }

        Map<String, List<TodoItem>> immutableMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<TodoItem>> entry : grouped.entrySet()) {
            List<TodoItem> sortedItems = entry.getValue().stream()
                    .sorted(Comparator.comparingInt(TodoItem::getLineNumber))
                    .collect(Collectors.toUnmodifiableList());
            immutableMap.put(entry.getKey(), sortedItems);
        }

        this.issuesByFile = Collections.unmodifiableMap(immutableMap);
        this.totalIssues = items.size();
    }

    /**
     * Returns the root directory that was scanned.
     *
     * @return root directory {@link Path}
     */
    public Path getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Returns an unmodifiable map of file paths to their corresponding list of {@link TodoItem}s.
     *
     * @return map grouped by normalized relative file path
     */
    public Map<String, List<TodoItem>> getIssuesByFile() {
        return issuesByFile;
    }

    /**
     * Returns the total number of matched issues across all scanned files.
     *
     * @return total issue count
     */
    public int getTotalIssues() {
        return totalIssues;
    }

    /**
     * Returns the number of distinct files in which at least one issue was found.
     *
     * @return count of affected files
     */
    public int getAffectedFilesCount() {
        return issuesByFile.size();
    }

    /**
     * Indicates whether no issues were discovered during the scan.
     *
     * @return {@code true} if {@link #getTotalIssues()} is zero, {@code false} otherwise
     */
    public boolean isEmpty() {
        return totalIssues == 0;
    }
}
