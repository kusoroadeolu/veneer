package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import io.github.kusoroadeolu.veneer.theme.SyntaxTheme;
import io.github.kusoroadeolu.veneer.utils.Constants;
import io.github.kusoroadeolu.veneer.utils.Utils;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

import static io.github.kusoroadeolu.veneer.BibTeXLexer.*;
import static io.github.kusoroadeolu.veneer.utils.Utils.isNullOrBlank;
import static io.github.kusoroadeolu.veneer.utils.Utils.styleMultiLineToken;

public class BibTeXSyntaxHighlighter extends AbstractSyntaxHighlighter {

    public enum BibTeXTokenCategory {
        KEYWORD,
        STRING,
        NUMBER,
        COMMENT,
        CITE_KEY,
        FIELD_NAME,
        MACRO,
        DEFAULT
    }

    public record BibTeXHighlightRegion(
            int start,
            int end,
            BibTeXTokenCategory category
    ) {}

    public BibTeXSyntaxHighlighter() { super(); }
    public BibTeXSyntaxHighlighter(boolean showLineNumbers) { super(showLineNumbers); }
    public BibTeXSyntaxHighlighter(SyntaxTheme theme) { super(theme); }
    public BibTeXSyntaxHighlighter(SyntaxTheme theme, boolean showLineNumbers) { super(theme, showLineNumbers); }

    @Override
    public String highlight(String s) {
        if (isNullOrBlank(s)) return "";

        StyleBuilder sb = Clique.styleBuilder();
        io.github.kusoroadeolu.veneer.BibTeXLexer lexer = new io.github.kusoroadeolu.veneer.BibTeXLexer(CharStreams.fromString(s));
        BufferedTokenStream tokenStream = Utils.toBufferedTokenStream(lexer);

        if (showLineNumbers) applyWithLines(sb, tokenStream);
        else applyWithoutLines(sb, tokenStream);

        return sb.toString();
    }

    /**
     * Computes highlight regions (start/end offsets + category) without producing
     * ANSI-styled output. Useful for consumers (e.g. GUI text editors) that need
     * to apply their own styling mechanism (CSS classes, text attributes, etc.)
     * instead of ANSI escape codes.
     *
     * @param s the BibTeX source to analyze
     * @return a list of non-overlapping highlight regions covering the source,
     *         ordered by their start offset
     */
    public List<BibTeXHighlightRegion> computeHighlightRegions(String s) {
        List<BibTeXHighlightRegion> regions = new ArrayList<>();
        if (isNullOrBlank(s)) return regions;

        io.github.kusoroadeolu.veneer.BibTeXLexer lexer = new io.github.kusoroadeolu.veneer.BibTeXLexer(CharStreams.fromString(s));
        BufferedTokenStream tokenStream = Utils.toBufferedTokenStream(lexer);
        List<Token> tokens = tokenStream.getTokens();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() == Token.EOF) continue;

            int start = token.getStartIndex();
            int end = token.getStopIndex() + 1;
            if (start < 0 || end <= start) continue;

            BibTeXTokenCategory category = classify(token, tokens, i);
            if (category != BibTeXTokenCategory.DEFAULT) {
                regions.add(new BibTeXHighlightRegion(start, end, category));
            }
        }

        return regions;
    }

    private BibTeXTokenCategory classify(Token token, List<Token> tokens, int index) {
        if (isComment(token)) return BibTeXTokenCategory.COMMENT;
        if (isKeyword(token)) return BibTeXTokenCategory.KEYWORD;
        if (isBraceString(token) || isString(token)) return BibTeXTokenCategory.STRING;
        if (isNumber(token)) return BibTeXTokenCategory.NUMBER;
        if (isCiteKey(token, tokens, index)) return BibTeXTokenCategory.CITE_KEY;
        if (isFieldName(token, tokens, index)) return BibTeXTokenCategory.FIELD_NAME;
        if (isName(token)) return BibTeXTokenCategory.MACRO;
        return BibTeXTokenCategory.DEFAULT;
    }

    private void applyWithLines(StyleBuilder sb, BufferedTokenStream tokenStream) {
        List<Token> tokens = tokenStream.getTokens();
        int[] lineNumber = new int[1];
        sb.appendAndReset(Utils.formatNoTo3dp(++lineNumber[0]), theme.gutter());

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            final int tokenIndex = i;
            if (token.getType() == Token.EOF) {
                continue;
            }

            if (isMultiLineToken(token)) {
                styleMultiLineToken(token, lineNumber, sb, theme.gutter(), (fragment, builder) ->
                        applyStyles(fragment, builder, tokens, tokenIndex));
            } else {
                applyStyles(token, sb, tokens, tokenIndex);
            }
        }
    }

    private void applyWithoutLines(StyleBuilder sb, BufferedTokenStream tokenStream) {
        List<Token> tokens = tokenStream.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            applyStyles(tokens.get(i), sb, tokens, i);
        }
    }

    private void applyStyles(Token token, StyleBuilder sb, List<Token> tokens, int index) {
        if (token.getType() == Token.EOF) return;

        String text = token.getText();

        if (isComment(token)) {
            sb.appendAndReset(text, theme.comment());
        } else if (isKeyword(token)) {
            sb.appendAndReset(text, theme.keyword());
        } else if (isBraceString(token)) {
            appendBraceString(text, sb);
        } else if (isString(token)) {
            sb.appendAndReset(text, theme.stringLiteral());
        } else if (isNumber(token)) {
            sb.appendAndReset(text, theme.numberLiteral());
        } else if (isCiteKey(token, tokens, index)) {
            appendCiteKey(text, sb);
        } else if (isFieldName(token, tokens, index)) {
            sb.appendAndReset(text, theme.method());
        } else if (isName(token)) {
            sb.appendAndReset(text, theme.types());
        } else {
            sb.appendAndReset(text);
        }
    }

    private void appendCiteKey(String text, StyleBuilder sb) {
        int colonIndex = text.indexOf(':');

        if (colonIndex < 0) {
            sb.appendAndReset(text, theme.constants());
            return;
        }

        sb.appendAndReset(
                text.substring(0, colonIndex + 1),
                theme.constants()
        );

        sb.appendAndReset(
                text.substring(colonIndex + 1),
                theme.method()
        );
    }

    private boolean isKeyword(Token token) {
        int type = token.getType();
        return type == AT_STRING || type == AT_PREAMBLE || type == AT_COMMENT || type == AT_ENTRY;
    }

    private boolean isString(Token token) {
        int type = token.getType();
        return type == DQUOTE_STRING;
    }

    private boolean isBraceString(Token token) {
        return token.getType() == BRACE_STRING;
    }

    private boolean isNumber(Token token) {
        return token.getType() == NUMBER;
    }

    private boolean isComment(Token token) {
        return token.getType() == LINE_COMMENT;
    }

    private boolean isName(Token token) {
        return token.getType() == NAME_TOKEN;
    }

    private boolean isCiteKey(Token token, List<Token> tokens, int index) {
        if (!isName(token)) return false;

        Token prev = previousDefaultToken(tokens, index);
        return prev != null && (prev.getType() == LBRACE || prev.getType() == LPAREN);
    }

    private boolean isFieldName(Token token, List<Token> tokens, int index) {
        if (!isName(token)) return false;
        if (isCiteKey(token, tokens, index)) return false;

        Token next = nextDefaultToken(tokens, index);
        return next != null && next.getType() == EQUALS;
    }

    private boolean isMultiLineToken(Token token) {
        return token.getType() != Token.EOF && token.getText().contains(Constants.NEWLINE);
    }

    private void appendBraceString(String text, StyleBuilder sb) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '{' || c == '}') {
                sb.appendAndReset(String.valueOf(c));
            } else {
                sb.appendAndReset(String.valueOf(c), theme.stringLiteral());
            }
        }
    }

    private Token previousDefaultToken(List<Token> tokens, int index) {
        for (int i = index - 1; i >= 0; i--) {
            Token candidate = tokens.get(i);
            if (candidate.getType() == Token.EOF) continue;
            if (candidate.getChannel() == Token.DEFAULT_CHANNEL) return candidate;
        }
        return null;
    }

    private Token nextDefaultToken(List<Token> tokens, int index) {
        for (int i = index + 1; i < tokens.size(); i++) {
            Token candidate = tokens.get(i);
            if (candidate.getType() == Token.EOF) continue;
            if (candidate.getChannel() == Token.DEFAULT_CHANNEL) return candidate;
        }
        return null;
    }
}