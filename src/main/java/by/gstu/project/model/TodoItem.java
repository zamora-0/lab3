package by.gstu.project.model;

import java.util.Objects;

/**
 * Immutable model representing a single matched keyword occurrence in a scanned file.
 */
public final class TodoItem {

    private final String filePath;
    private final int lineNumber;
    private final String keyword;
    private final String text;

    /**
     * Constructs a {@code TodoItem}.
     *
     * @param filePath   normalized relative path to the file containing the match
     * @param lineNumber 1-based line number where the match was found (must be {@code >= 1})
     * @param keyword    configured keyword that matched the line
     * @param text       extracted line/comment text starting at the matched keyword
     * @throws IllegalArgumentException if {@code lineNumber < 1} or {@code filePath}/{@code keyword} is blank
     */
    public TodoItem(String filePath, int lineNumber, String keyword, String text) {
        Objects.requireNonNull(filePath, "filePath must not be null");
        Objects.requireNonNull(keyword, "keyword must not be null");
        Objects.requireNonNull(text, "text must not be null");

        if (filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("filePath must not be blank");
        }
        if (lineNumber < 1) {
            throw new IllegalArgumentException("lineNumber must be >= 1, got: " + lineNumber);
        }
        if (keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("keyword must not be blank");
        }

        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.keyword = keyword;
        this.text = text;
    }

    /**
     * Returns the normalized file path where the item was found.
     *
     * @return relative file path using {@code /} separators
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the 1-based line number of the match.
     *
     * @return line number ({@code >= 1})
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the keyword that triggered this match.
     *
     * @return matched keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the extracted comment or line text.
     *
     * @return extracted text
     */
    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TodoItem)) {
            return false;
        }
        TodoItem todoItem = (TodoItem) o;
        return lineNumber == todoItem.lineNumber
                && filePath.equals(todoItem.filePath)
                && keyword.equals(todoItem.keyword)
                && text.equals(todoItem.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, lineNumber, keyword, text);
    }

    @Override
    public String toString() {
        return "TodoItem{"
                + "filePath='" + filePath + '\''
                + ", lineNumber=" + lineNumber
                + ", keyword='" + keyword + '\''
                + ", text='" + text + '\''
                + '}';
    }
}
