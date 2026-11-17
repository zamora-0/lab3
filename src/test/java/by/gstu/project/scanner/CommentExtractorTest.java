package by.gstu.project.scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentExtractorTest {

    @Test
    void shouldExtractSingleAndBlockCommentsFromJavaAndIgnoreStrings() {
        CommentExtractor extractor = new CommentExtractor();

        assertEquals("", extractor.extractSearchableText("App.java", "String s = \"// TODO: inside string\";"));
        assertEquals("TODO: refactor", extractor.extractSearchableText("App.java", "int x = 1; // TODO: refactor"));
        assertEquals("FIXME: block start", extractor.extractSearchableText("App.java", "/* FIXME: block start"));
        assertEquals("* TODO: middle line", extractor.extractSearchableText("App.java", " * TODO: middle line"));
        assertEquals("block end", extractor.extractSearchableText("App.java", "block end */"));
        assertEquals("", extractor.extractSearchableText("App.java", "int afterBlock = 2;"));
        assertEquals("", extractor.extractSearchableText("App.java", "/** TODO: Javadoc prose */"));
    }

    @Test
    void shouldExtractXmlComments() {
        CommentExtractor extractor = new CommentExtractor();

        assertEquals("TODO: update dependency",
                extractor.extractSearchableText("pom.xml", "<!-- TODO: update dependency -->"));
        assertEquals("",
                extractor.extractSearchableText("pom.xml", "<name>TODO</name>"));
    }

    @Test
    void shouldReturnWholeLineForMarkdown() {
        CommentExtractor extractor = new CommentExtractor();

        assertEquals("TODO: Remember to update screenshots",
                extractor.extractSearchableText("README.md", "TODO: Remember to update screenshots"));
    }
}
