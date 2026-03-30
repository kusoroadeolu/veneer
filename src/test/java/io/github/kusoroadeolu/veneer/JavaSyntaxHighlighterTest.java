package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.ansi.StyleCode;
import io.github.kusoroadeolu.clique.parser.AnsiStringParser;
import io.github.kusoroadeolu.veneer.theme.SyntaxThemes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaSyntaxHighlighterTest {

    private JavaSyntaxHighlighter highlighter;

    @BeforeEach
    void setUp() {
        highlighter = new JavaSyntaxHighlighter(false); //No line counting
    }

    @Test
    //Here i'm using the default style so i'll be testing against that
    public void highlight_onCompleteSyntax_shouldBeStyled(){
        String codeSnippet = """
            @SuppressWarnings("unchecked")
            public class Example {
            }
            """;
        String styled = highlighter.highlight(codeSnippet);
        List<String> list = styled.lines().toList();
        assertTrue(list.getFirst().contains(SyntaxThemes.DEFAULT.annotation().toString()));
        assertTrue(list.getFirst().contains(SyntaxThemes.DEFAULT.stringLiteral().toString()));
        assertTrue(list.get(1).contains(SyntaxThemes.DEFAULT.keyword().toString()));
    }

    @Test
    //Here i'm using the default style so i'll be testing against that
    public void highlight_onInvalidSyntax_shouldReturnEqualString(){
        String codeSnippet = """
            Some garbage
            """;
        String styled = highlighter.highlight(codeSnippet);
        styled = styled.replace(StyleCode.RESET.toString(), ""); //Replace all the resets, since resets will be initially applied here
        assertEquals(codeSnippet, styled);
    }

    @Test
    public void highlight_onIncompleteSyntax_shouldBeStyled(){
        String codeSnippet = """
            void main(){
                int a = 1;
            }
            """;

        String styled = highlighter.highlight(codeSnippet);
        List<String> list = styled.lines().toList();
        //This wont render the method name correctly
        assertTrue(list.getFirst().contains(SyntaxThemes.DEFAULT.keyword().toString()));
        assertTrue(list.get(1).contains(SyntaxThemes.DEFAULT.keyword().toString()));
    }

    @Test
    public void highlight_withLinesEnabled_shouldCorrectlyFormatLines(){
        String codeSnippet = """
            void main(){
                int a = 1;
            }
            """;
        var highlighter1 = new JavaSyntaxHighlighter();
        String styled = highlighter1.highlight(codeSnippet);
        styled = AnsiStringParser.DEFAULT.getOriginalString(styled); //Strip ansi codes to prevent false positives
        List<String> list = styled.lines().toList();
        assertTrue(list.getFirst().contains("1"));
        assertTrue(list.get(1).contains("2"));
        assertTrue(list.get(2).contains("3"));
    }

    @Test
    public void highlight_givenVar_shouldHighlightAsKeyword(){
        String codeSnippet = """
            void main(){
                var a = new Object();
            }
            """;

        var highlighter1 = new JavaSyntaxHighlighter();
        String styled = highlighter1.highlight(codeSnippet);
        List<String> list = styled.lines().toList();
        assertTrue(list.get(1).contains(SyntaxThemes.DEFAULT.keyword().toString()));
    }

    @Test
    void highlight_nullOrBlank_shouldReturnEmpty() {
        String s = null;
        assertTrue(highlighter.highlight(s).isEmpty());
        assertTrue(highlighter.highlight("").isEmpty());
        assertTrue(highlighter.highlight("   ").isEmpty());
    }



}