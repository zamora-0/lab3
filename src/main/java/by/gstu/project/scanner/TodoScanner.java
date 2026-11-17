package by.gstu.project.scanner;

import by.gstu.project.config.AppConfig;
import by.gstu.project.model.ScanReport;
import by.gstu.project.model.TodoItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Recursively scans a source directory for files matching configured extensions
 * and searches them case-insensitively for configured keywords (such as {@code TODO} and {@code FIXME}).
 */
public class TodoScanner {

    private final AppConfig config;
    private final FileExtensionFilter extensionFilter;
    private final List<KeywordPattern> keywordPatterns;

    /**
     * Constructs a {@code TodoScanner} using the provided application configuration.
     *
     * @param config scanner configuration; must not be {@code null}
     */
    public TodoScanner(AppConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.extensionFilter = new FileExtensionFilter(config.getFileExtensions());
        this.keywordPatterns = compilePatterns(config.getKeywords());
    }

    /**
     * Scans the directory configured in {@link AppConfig#getSourceDirectory()}.
     *
     * @return aggregated {@link ScanReport}
     * @throws IOException              if an I/O error occurs while traversing or reading files
     * @throws IllegalArgumentException if the configured directory does not exist or is not a directory
     */
    public ScanReport scan() throws IOException {
        return scan(config.getSourceDirectory());
    }

    /**
     * Recursively scans the specified directory for keyword matches.
     *
     * @param rootDirectory directory to scan; must exist and be a directory
     * @return aggregated {@link ScanReport} grouped by file
     * @throws IOException              if an I/O error occurs while traversing or reading files
     * @throws IllegalArgumentException if {@code rootDirectory} does not exist or is not a directory
     */
    public ScanReport scan(Path rootDirectory) throws IOException {
        Objects.requireNonNull(rootDirectory, "rootDirectory must not be null");
        if (!Files.exists(rootDirectory) || !Files.isDirectory(rootDirectory)) {
            throw new IllegalArgumentException(
                    "Scan directory does not exist or is not a directory: " + rootDirectory);
        }

        List<Path> targetFiles;
        try (Stream<Path> walk = Files.walk(rootDirectory)) {
            targetFiles = walk
                    .filter(extensionFilter::matches)
                    .sorted(Comparator.comparing(path -> path.toString().replace('\\', '/')))
                    .collect(Collectors.toList());
        }

        Path displayBase = resolveDisplayBase(rootDirectory);
        List<TodoItem> items = new ArrayList<>();
        CommentExtractor extractor = new CommentExtractor();

        for (Path file : targetFiles) {
            scanFile(file, displayBase, extractor, items);
        }

        return new ScanReport(rootDirectory, items);
    }

    private void scanFile(Path file, Path displayBase, CommentExtractor extractor, List<TodoItem> collector)
            throws IOException {
        extractor.reset();
        String fileName = file.getFileName().toString();
        String relativePath = displayBase.relativize(file.toAbsolutePath().normalize())
                .toString()
                .replace('\\', '/');

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file), decoder))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String searchableText = extractor.extractSearchableText(fileName, line);
                if (searchableText.isEmpty()) {
                    continue;
                }
                TodoItem match = findFirstKeywordMatch(relativePath, lineNumber, searchableText);
                if (match != null) {
                    collector.add(match);
                }
            }
        }
    }

    private TodoItem findFirstKeywordMatch(String relativePath, int lineNumber, String searchableText) {
        KeywordPattern bestPattern = null;
        int bestStart = Integer.MAX_VALUE;

        for (KeywordPattern kp : keywordPatterns) {
            Matcher matcher = kp.pattern.matcher(searchableText);
            if (matcher.find() && matcher.start() < bestStart) {
                bestStart = matcher.start();
                bestPattern = kp;
            }
        }

        if (bestPattern == null) {
            return null;
        }

        String extractedText = searchableText.substring(bestStart).trim();
        return new TodoItem(relativePath, lineNumber, bestPattern.keyword, extractedText);
    }

    private Path resolveDisplayBase(Path rootDirectory) {
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        Path normalizedRoot = rootDirectory.toAbsolutePath().normalize();
        if (normalizedRoot.startsWith(cwd) && !normalizedRoot.equals(cwd)) {
            return cwd;
        }
        return normalizedRoot;
    }

    private List<KeywordPattern> compilePatterns(List<String> keywords) {
        List<KeywordPattern> patterns = new ArrayList<>(keywords.size());
        for (String keyword : keywords) {
            String regex = "(?i)(?<![\\p{Alnum}_])" + Pattern.quote(keyword) + "(?![\\p{Alnum}_])";
            patterns.add(new KeywordPattern(keyword, Pattern.compile(regex)));
        }
        return patterns;
    }

    private static final class KeywordPattern {
        private final String keyword;
        private final Pattern pattern;

        private KeywordPattern(String keyword, Pattern pattern) {
            this.keyword = keyword;
            this.pattern = pattern;
        }
    }
}
