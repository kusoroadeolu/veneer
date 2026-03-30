package io.github.kusoroadeolu.veneer;
import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import io.github.kusoroadeolu.veneer.theme.SyntaxTheme;
import io.github.kusoroadeolu.veneer.utils.Utils;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import io.github.kusoroadeolu.veneer.JavaScriptLexer;

import static io.github.kusoroadeolu.veneer.utils.Constants.CAPITAL_PATTERN;
import static io.github.kusoroadeolu.veneer.utils.Utils.*;

public class JavaScriptSyntaxHighlighter extends AbstractSyntaxHighlighter{
    public JavaScriptSyntaxHighlighter() { super(); }
    public JavaScriptSyntaxHighlighter(boolean showLineNumbers) { super(showLineNumbers); }
    public JavaScriptSyntaxHighlighter(SyntaxTheme theme) { super(theme); }
    public JavaScriptSyntaxHighlighter(SyntaxTheme theme, boolean showLineNumbers){super(theme, showLineNumbers);}


    @Override
    public String highlight(String s) {
        if (isNullOrBlank(s)) return "";

        StyleBuilder sb = Clique.styleBuilder();
        JavaScriptLexer lexer = new JavaScriptLexer(CharStreams.fromString(s));
        var tokenStream = toTokenStream(lexer);
        int[] lineNumber = new int[]{1};

        Token prev = null;

        if (showLineNumbers) {
            sb.append(Utils.formatNoTo3dp(lineNumber[0]), theme.gutter());
        }

        for (Token token : tokenStream.getTokens()) {
            if (showLineNumbers && isMultiLineToken(token)) {
                styleMultiLineToken(token, lineNumber, sb, theme.gutter(), prev ,this::applyStyles);
            } else if (showLineNumbers && isLineTerminator(token)) {
                sb.append(token.getText());
                sb.append(Utils.formatNoTo3dp(++lineNumber[0]), theme.gutter());
            } else {
                applyStyles(token, sb, prev);
            }

            if (isValidPrevToken(token)){
                prev = token;
            }
        }



        return sb.get();
    }



    void applyStyles(Token token, StyleBuilder sb, Token prev){
        if (isKeyword(token)){
            sb.append(token.getText(), theme.keyword());
        }else if (isStringLiteral(token)){
            sb.append(token.getText(), theme.stringLiteral());
        } else if (isNumberLiteral(token)) {
            sb.append(token.getText(), theme.numberLiteral());
        }else if (isComment(token)) {
            sb.append(token.getText(), theme.comment());
        }else if(isFunctionName(token, prev)){
                sb.append(token.getText(), theme.method());
        }else if(isConstant(token)) {
            sb.append(token.getText(), theme.constants());
        }else if (!isEOF(token)) {
            sb.append(token.getText());
        }
    }

    boolean isLineTerminator(Token token) {
        int t = token.getType();
        return t == JavaScriptLexer.LineTerminator
                || t == JavaScriptLexer.JsxOpeningElementLineTerminator
                || t == JavaScriptLexer.JsxClosingElementLineTerminator;
    }

    boolean isKeyword(Token token) {
        int t = token.getType();
        return t >= JavaScriptLexer.Break && t <= JavaScriptLexer.Yield
                || t == JavaScriptLexer.NullLiteral
                || t == JavaScriptLexer.BooleanLiteral;
    }

    boolean isValidPrevToken(Token token){
        return !isLineTerminator(token)
                && token.getType() != JavaScriptLexer.WhiteSpaces
                && token.getType() != Token.EOF;
    }

    boolean isNumberLiteral(Token token) {
        int t = token.getType();
        return t >= JavaScriptLexer.DecimalLiteral && t <= JavaScriptLexer.BigDecimalIntegerLiteral;
    }

    boolean isStringLiteral(Token token) {
        int t = token.getType();
        return t == JavaScriptLexer.StringLiteral
                || t == JavaScriptLexer.LinkLiteral
                || t == JavaScriptLexer.BackTick
                || t == JavaScriptLexer.TemplateStringAtom;
    }

    boolean isComment(Token token) {
        int t = token.getType();
        return t == JavaScriptLexer.SingleLineComment
                || t == JavaScriptLexer.MultiLineComment
                || t == JavaScriptLexer.JsxComment
                || t == JavaScriptLexer.HtmlComment
                || t == JavaScriptLexer.CDataComment;
    }


    boolean isMultiLineToken(Token token) {
        int t = token.getType();
        return t == JavaScriptLexer.MultiLineComment
                || t == JavaScriptLexer.JsxComment
                || t == JavaScriptLexer.CDataComment
                || t == JavaScriptLexer.TemplateStringAtom;
    }

    boolean isEOF(Token token){
        return token.getType() == Token.EOF;
    }

    boolean isConstant(Token token) {
        return token.getType() == JavaScriptLexer.Identifier
                && token.getText().matches(CAPITAL_PATTERN.pattern());
    }

    boolean isFunctionName(Token token, Token prev) {
        return token.getType() == JavaScriptLexer.Identifier
                && prev != null
                && prev.getType() == JavaScriptLexer.Function_;
    }
}
