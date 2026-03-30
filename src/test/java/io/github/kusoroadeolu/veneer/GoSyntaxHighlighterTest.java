package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.parser.AnsiStringParser;
import io.github.kusoroadeolu.veneer.theme.SyntaxThemes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoSyntaxHighlighterTest {
    private SyntaxHighlighter highlighter;

    @BeforeEach
    void setUp() {
        highlighter = new GoSyntaxHighlighter(false);
    }

    @Test
    void highlight_keywords_shouldBeStyled() {
        String snippet = """
            func main() {
                return
            }
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.keyword().toString()));
    }

    @Test
    void highlight_stringLiteral_shouldBeStyled() {
        String snippet = """
            var msg = "hello world"
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_rawStringLiteral_shouldBeStyled() {
        String snippet = """
            var msg = `hello world`
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_numberLiterals_shouldBeStyled() {
        String snippet = """
            var a = 42
            var b = 0xFF
            var c = 3.14
            var d = 0b1010
            var e = 0o77
            var f = 1i
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.numberLiteral().toString()));
    }

    @Test
    void highlight_singleLineComment_shouldBeStyled() {
        String snippet = """
            // this is a comment
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.comment().toString()));
    }

    @Test
    void highlight_multiLineComment_shouldBeStyled() {
        String snippet = """
            /* this is
               a multi line
               comment */
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.comment().toString()));
    }

    @Test
    void highlight_nullOrBlank_shouldReturnEmpty() {
        String s = null;
        assertTrue(highlighter.highlight(s).isEmpty());
        assertTrue(highlighter.highlight("").isEmpty());
        assertTrue(highlighter.highlight("   ").isEmpty());
    }

    @Test
    void highlight_withLineNumbers_shouldFormatCorrectly() {
        String snippet = """
            var a = 1
            var b = 2
            var c = 3
            """;
        var lineHighlighter = new GoSyntaxHighlighter();
        String styled = AnsiStringParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
    }

    @Test
    void highlight_multilineRawString_withLineNumbers_shouldFormatCorrectly() {
        String snippet = """
            var msg = `line one
            line two
            line three`
            var x = 1
            """;
        var lineHighlighter = new GoSyntaxHighlighter();
        String styled = AnsiStringParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
        assertTrue(lines.get(3).contains("4"));
        // ensure the raw string content is still styled
        assertTrue(lineHighlighter.highlight(snippet).contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_multilineComment_withLineNumbers_shouldFormatCorrectly() {
        String snippet = """
            /* line one
               line two
               line three */
            var x = 1
            """;
        var lineHighlighter = new GoSyntaxHighlighter();
        String styled = AnsiStringParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
        assertTrue(lines.get(3).contains("4"));
        // ensure the comment content is still styled
        assertTrue(lineHighlighter.highlight(snippet).contains(SyntaxThemes.DEFAULT.comment().toString()));
    }
}