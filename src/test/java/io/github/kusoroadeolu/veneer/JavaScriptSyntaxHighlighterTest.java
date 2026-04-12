package io.github.kusoroadeolu.veneer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.kusoroadeolu.clique.parser.MarkupParser;
import io.github.kusoroadeolu.veneer.theme.SyntaxThemes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaScriptSyntaxHighlighterTest {

    private JavaScriptSyntaxHighlighter highlighter;

    @BeforeEach
    void setUp() {
        highlighter = new JavaScriptSyntaxHighlighter(false);
    }

    @Test
    void highlight_keywords_shouldBeStyled() {
        String snippet = "const x = 1;";
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.keyword().ansiSequence()));
    }

    @Test
    void highlight_stringLiteral_shouldBeStyled() {
        String snippet = """
            const msg = "hello";
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().ansiSequence()));
    }

    @Test
    void highlight_templateLiteral_shouldBeStyled() {
        String snippet = """
            const msg = `hello world`;
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().ansiSequence()));
    }

    @Test
    void highlight_numberLiterals_shouldBeStyled() {
        String snippet = """
            const a = 42;
            const b = 0xFF;
            const c = 0b1010;
            const d = 0o77;
            const e = 100n;
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.numberLiteral().ansiSequence()));
    }

    @Test
    void highlight_comments_shouldBeStyled() {
        String snippet = """
            // single line
            /* multi
               line */
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.comment().ansiSequence()));
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
            const a = 1;
            const b = 2;
            const c = 3;
            """;
        var lineHighlighter = new JavaScriptSyntaxHighlighter();
        String styled = MarkupParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
    }

    @Test
    void highlight_multilineComment_withLineNumbers_shouldFormatCorrectly() {
        String snippet = """
            /* line one
               line two
               line three */
            const x = 1;
            """;
        var lineHighlighter = new JavaScriptSyntaxHighlighter();
        String styled = MarkupParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
        assertTrue(lines.get(3).contains("4"));
    }
}