package by.gstu.project.scanner;

import java.util.Locale;

/**
 * Extracts searchable text from source lines depending on file type.
 *
 * <p>For code files ({@code .java}, {@code .kt}) and markup files ({@code .xml}),
 * only comment segments are extracted so string literals and code identifiers do not
 * trigger false positives. For plain-text and documentation files (e.g. {@code .md}),
 * the entire line is returned.</p>
 */
public class CommentExtractor {

    private boolean inBlockComment;

    /**
     * Resets multi-line comment state before processing a new file.
     */
    public void reset() {
        this.inBlockComment = false;
    }

    /**
     * Extracts searchable comment text from a single line based on the file name's extension.
     *
     * @param fileName file name used to determine comment syntax
     * @param line     raw source line
     * @return extracted comment/searchable text, or an empty string if the line has no searchable comment
     */
    public String extractSearchableText(String fileName, String line) {
        if (line == null || line.isEmpty()) {
            return "";
        }
        String lowerName = fileName.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".java") || lowerName.endsWith(".kt")) {
            return extractCStyleComments(line);
        }
        if (lowerName.endsWith(".xml") || lowerName.endsWith(".html")) {
            return extractXmlComments(line);
        }
        return line;
    }

    private String extractCStyleComments(String line) {
        StringBuilder comment = new StringBuilder();
        boolean inString = false;
        boolean inChar = false;
        int len = line.length();

        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            char next = (i + 1 < len) ? line.charAt(i + 1) : '\0';

            if (inBlockComment) {
                if (ch == '*' && next == '/') {
                    inBlockComment = false;
                    i++;
                } else {
                    comment.append(ch);
                }
                continue;
            }

            if (inString) {
                if (ch == '\\' && i + 1 < len) {
                    i++;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }

            if (inChar) {
                if (ch == '\\' && i + 1 < len) {
                    i++;
                } else if (ch == '\'') {
                    inChar = false;
                }
                continue;
            }

            if (ch == '"') {
                inString = true;
            } else if (ch == '\'') {
                inChar = true;
            } else if (ch == '/' && next == '/') {
                comment.append(line, i + 2, len);
                break;
            } else if (ch == '/' && next == '*') {
                inBlockComment = true;
                i++;
            }
        }
        return comment.toString().trim();
    }

    private String extractXmlComments(String line) {
        StringBuilder comment = new StringBuilder();
        int len = line.length();

        for (int i = 0; i < len; i++) {
            if (inBlockComment) {
                if (line.startsWith("-->", i)) {
                    inBlockComment = false;
                    i += 2;
                } else {
                    comment.append(line.charAt(i));
                }
            } else if (line.startsWith("<!--", i)) {
                inBlockComment = true;
                i += 3;
            }
        }
        return comment.toString().trim();
    }
}
