package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.parser.AnsiStringParser;
import io.github.kusoroadeolu.veneer.theme.SyntaxThemes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PythonSyntaxHighlighterTest {
    private SyntaxHighlighter highlighter;

    @BeforeEach
    void setUp() {
        highlighter = new PythonSyntaxHighlighter(false);
    }

    @Test
    void highlight_keywords_shouldBeStyled() {
        String snippet = """
            def greet():
                return "hello"
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.keyword().toString()));
    }

    @Test
    void highlight_stringLiteral_shouldBeStyled() {
        String snippet = """
            msg = "hello world"
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_fString_shouldBeStyled() {
        String snippet = """
            name = "world"
            msg = f"hello {name}"
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_numberLiterals_shouldBeStyled() {
        String snippet = """
            a = 42
            b = 3.14
            c = 0xFF
            d = 0b1010
            e = 0o77
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.numberLiteral().toString()));
    }

    @Test
    void highlight_singleLineComment_shouldBeStyled() {
        String snippet = """
            # this is a comment
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.comment().toString()));
    }

    @Test
    void highlight_annotation_shouldBeStyled() {
        String snippet = """
            @staticmethod
            def greet():
                pass
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.annotation().toString()));
    }

    @Test
    void highlight_nullOrBlank_shouldReturnEmpty() {
        assertTrue(highlighter.highlight((String) null).isEmpty());
        assertTrue(highlighter.highlight("").isEmpty());
        assertTrue(highlighter.highlight("   ").isEmpty());
    }

    @Test
    void highlight_withLineNumbers_shouldFormatCorrectly() {
        String snippet = """
            a = 1
            b = 2
            c = 3
            """;
        var lineHighlighter = new PythonSyntaxHighlighter();
        String styled = AnsiStringParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
    }
}