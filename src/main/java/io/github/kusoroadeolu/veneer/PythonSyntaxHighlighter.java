package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.core.utils.Constants;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import io.github.kusoroadeolu.veneer.theme.SyntaxTheme;
import io.github.kusoroadeolu.veneer.utils.Utils;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import io.github.kusoroadeolu.veneer.PythonLexer;



import static io.github.kusoroadeolu.veneer.utils.Constants.CAPITAL_PATTERN;
import static io.github.kusoroadeolu.veneer.utils.Utils.*;
import static io.github.kusoroadeolu.veneer.utils.Utils.styleMultiLineToken;

public class PythonSyntaxHighlighter extends AbstractSyntaxHighlighter {

    public PythonSyntaxHighlighter() { super(); }
    public PythonSyntaxHighlighter(boolean showLineNumbers) { super(showLineNumbers); }
    public PythonSyntaxHighlighter(SyntaxTheme theme) { super(theme); }
    public PythonSyntaxHighlighter(SyntaxTheme theme, boolean showLineNumbers){super(theme, showLineNumbers);}

    @Override
    public String highlight(String s) {
        if (isNullOrBlank(s)) return "";

        StyleBuilder sb = Clique.styleBuilder();
        PythonLexer lexer = new PythonLexer(CharStreams.fromString(s));
        var tokenStream = Utils.toTokenStream(lexer);
        var tokens = tokenStream.getTokens();

        int[] lineNumber = new int[1];
        sb.append(formatNoTo3dp(++lineNumber[0]), theme.gutter());

        Token prev = null;

        for (Token token : tokens) {
            if (token.getType() == PythonLexer.NEWLINE && showLineNumbers) {
                sb.append(Constants.NEWLINE);
                sb.append(formatNoTo3dp(++lineNumber[0]), theme.gutter());
            } else if (isMultiLineToken(token)) {
                styleMultiLineToken(token, lineNumber, sb, theme.gutter(), prev ,this::applyStyles);
            } else {
                applyStyles(token, sb, prev);
            }

            if (isValidPrevToken(token)) {
                prev = token;
            }
        }
        return sb.get();
    }


    void applyStyles(Token token, StyleBuilder sb, Token prev) {
        if (token.getType() == Token.EOF) return;
        else if (token.getType() == PythonLexer.INDENT || token.getType() == PythonLexer.DEDENT || token.getType() == PythonLexer.ERRORTOKEN) return;
        else if (token.getChannel() == Token.HIDDEN_CHANNEL && token.getType() != PythonLexer.WS) return;


        String text = token.getText();
        if (isComment(token)) {
            sb.append(text, theme.comment());
        } else if (isString(token)) {
            sb.append(text, theme.stringLiteral());
        } else if (isNumber(token)) {
            sb.append(text, theme.numberLiteral());
        } else if (isAnnotation(token)) {
            sb.append(text, theme.annotation());
        } else if (isFunctionName(token, prev)) {
            sb.append(text, theme.method());
        } else if (isConstant(token)) {
            sb.append(text, theme.constants());
         } else if (isArrow(token)) {
            sb.append(text, theme.annotation());
        } else if (isReturnType(token, prev)) {
            if (isKeyword(text)) sb.append(text, theme.keyword());
            else sb.append(text, theme.types());
        } else if (isKeyword(token)) {
            sb.append(text, theme.keyword());
        } else {
            sb.append(text);
        }
    }

    boolean isValidPrevToken(Token token){
        return  !isWhitespace(token) && token.getChannel() == Token.DEFAULT_CHANNEL;
    }

    boolean isWhitespace(Token token) {
        int t = token.getType();
        return t == PythonLexer.WS
                || t == PythonLexer.NEWLINE
                || t == PythonLexer.INDENT
                || t == PythonLexer.DEDENT
                || t == PythonLexer.EXPLICIT_LINE_JOINING;
    }

    boolean isKeyword(Token token) {
        int t = token.getType();
        return t >= PythonLexer.FALSE && t <= PythonLexer.YIELD
                || t == PythonLexer.NAME_OR_TYPE
                || t == PythonLexer.NAME_OR_MATCH
                || t == PythonLexer.NAME_OR_CASE
                || t == PythonLexer.NAME_OR_WILDCARD;
    }

    boolean isString(Token token) {
        int t = token.getType();
        return t == PythonLexer.STRING
                || t == PythonLexer.FSTRING_START
                || t == PythonLexer.FSTRING_MIDDLE
                || t == PythonLexer.FSTRING_END;
    }

    boolean isMultiLineToken(Token token){
        int t = token.getType();
        return t == PythonLexer.FSTRING_MIDDLE || isComment(token) || t == PythonLexer.STRING;
    }

    boolean isNumber(Token token) {
        return token.getType() == PythonLexer.NUMBER;
    }

    boolean isComment(Token token) {
        return token.getType() == PythonLexer.COMMENT;
    }

    boolean isAnnotation(Token token) {
        return token.getType() == PythonLexer.AT;
    }

    boolean isKeyword(String text) {
        return switch (text) {
            case "None", "True", "False" -> true;
            default -> false;
        };
    }

    boolean isFunctionName(Token token, Token prev) {
        return token.getType() == PythonLexer.NAME
                && prev != null
                && prev.getType() == PythonLexer.DEF;
    }

    boolean isConstant(Token token) {
        return token.getType() == PythonLexer.NAME
                && token.getText().matches(CAPITAL_PATTERN.pattern());
    }

    boolean isArrow(Token token) {
        return token.getType() == PythonLexer.RARROW;
    }

    boolean isReturnType(Token token, Token prev) {
        if (prev == null || prev.getType() != PythonLexer.RARROW) return false;
        return token.getType() == PythonLexer.NAME
                || isKeyword(token);
    }
}