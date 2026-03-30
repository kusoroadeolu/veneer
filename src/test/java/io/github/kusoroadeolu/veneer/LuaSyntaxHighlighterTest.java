package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.parser.AnsiStringParser;
import io.github.kusoroadeolu.veneer.theme.SyntaxThemes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LuaSyntaxHighlighterTest {
    private SyntaxHighlighter highlighter;

    @BeforeEach
    void setUp() {
        highlighter = new LuaSyntaxHighlighter(false);
    }

    @Test
    void highlight_keywords_shouldBeStyled() {
        String snippet = """
            local function greet()
                return "hello"
            end
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.keyword().toString()));
    }

    @Test
    void highlight_stringLiteral_shouldBeStyled() {
        String snippet = """
            local msg = "hello world"
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_singleQuoteStringLiteral_shouldBeStyled() {
        String snippet = """
            local msg = 'hello world'
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_longStringLiteral_shouldBeStyled() {
        String snippet = """
            local msg = [[hello world]]
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
    }

    @Test
    void highlight_numberLiterals_shouldBeStyled() {
        String snippet = """
            local a = 42
            local b = 3.14
            local c = 0xFF
            local d = 1.0e5
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.numberLiteral().toString()));
    }

    @Test
    void highlight_singleLineComment_shouldBeStyled() {
        String snippet = """
            -- this is a comment
            """;
        String styled = highlighter.highlight(snippet);
        assertTrue(styled.contains(SyntaxThemes.DEFAULT.comment().toString()));
    }

    @Test
    void highlight_blockComment_shouldBeStyled() {
        String snippet = """
            --[[ this is
                 a block
                 comment ]]
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
            local a = 1
            local b = 2
            local c = 3
            """;
        var lineHighlighter = new LuaSyntaxHighlighter();
        String styled = AnsiStringParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
    }

    @Test
    void highlight_multilineBlockComment_withLineNumbers_shouldFormatCorrectly() {
        String snippet = """
            --[[ line one
                 line two
                 line three ]]
            local x = 1
            """;
        var lineHighlighter = new LuaSyntaxHighlighter();
        String styled = AnsiStringParser.DEFAULT.getOriginalString(lineHighlighter.highlight(snippet));
        List<String> lines = styled.lines().toList();
        assertTrue(lines.getFirst().contains("1"));
        assertTrue(lines.get(1).contains("2"));
        assertTrue(lines.get(2).contains("3"));
        assertTrue(lines.get(3).contains("4"));
        assertTrue(lineHighlighter.highlight(snippet).contains(SyntaxThemes.DEFAULT.comment().toString()));
    }
}