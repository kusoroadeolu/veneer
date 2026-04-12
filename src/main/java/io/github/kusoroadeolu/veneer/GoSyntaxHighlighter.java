package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import io.github.kusoroadeolu.veneer.theme.SyntaxTheme;
import io.github.kusoroadeolu.veneer.utils.Constants;
import io.github.kusoroadeolu.veneer.utils.Utils;
import org.antlr.v4.runtime.*;

import java.util.List;
import io.github.kusoroadeolu.veneer.GoLexer;

import static io.github.kusoroadeolu.veneer.utils.Constants.NEWLINE;
import static io.github.kusoroadeolu.veneer.utils.Utils.*;

public class GoSyntaxHighlighter extends AbstractSyntaxHighlighter {

    public GoSyntaxHighlighter() { super(); }
    public GoSyntaxHighlighter(boolean showLineNumbers) { super(showLineNumbers); }
    public GoSyntaxHighlighter(SyntaxTheme theme) { super(theme); }
    public GoSyntaxHighlighter(SyntaxTheme theme, boolean showLineNumbers){super(theme, showLineNumbers);}

    private static final int KEYWORD_START = 1;
    private static final int KEYWORD_END   = 26;

    private static final int NUM_START = GoLexer.DECIMAL_LIT;   // 65
    private static final int NUM_END   = GoLexer.IMAGINARY_LIT; // 72


    @Override
    public String highlight(String s) {
        if (isNullOrBlank(s)) return "";

        StyleBuilder sb = Clique.styleBuilder();
        GoLexer lexer = new GoLexer(CharStreams.fromString(s));
        var tokenStream = toTokenStream(lexer);

        List<Token> tokens = tokenStream.getTokens();
        int size = tokens.size();
        int[] lineNumber = new int[1];

        if (showLineNumbers) {
            sb.appendAndReset(Utils.formatNoTo3dp(++lineNumber[0]), theme.gutter());
        }

        for (int i = 0; i < size; i++) {
            Token token = tokens.get(i);
            if (showLineNumbers && isMultiLineToken(token)) {
                styleMultiLineToken(token, lineNumber, sb, theme.gutter(), this::applyStyles);
            }else if (showLineNumbers && isLineEnding(token)) {
                String text = token.getText();
                long newlineCount = text.chars().filter(c -> c == '\n').count();
                for (int j = 0; j < newlineCount; j++) {
                    sb.appendAndReset(NEWLINE);
                    sb.appendAndReset(Utils.formatNoTo3dp(++lineNumber[0]), theme.gutter());
                }
            } else {
                applyStyles(token, sb);
            }
        }

        return sb.toString();
    }

    void applyStyles(Token token, StyleBuilder sb) {
        int type = token.getType();
        String text = token.getText();

        if (token.getChannel() == Token.HIDDEN_CHANNEL
                && type != GoLexer.WS
                && type != GoLexer.WS_NLSEMI
                && !isComment(type)) {
            return;
        }

        if (isWhitespace(type)) {
            sb.appendAndReset(text);
        } else if (isComment(type)) {
            sb.appendAndReset(text, theme.comment());
        } else if (isString(type)) {
            sb.appendAndReset(text, theme.stringLiteral());
        } else if (isNumber(type)) {
            sb.appendAndReset(text, theme.numberLiteral());
        } else if (isKeyword(type)) {
            sb.appendAndReset(text, theme.keyword());
        } else if (!isEOF(type)){
            sb.appendAndReset(text);
        }
    }

    boolean isMultiLineToken(Token token) {
        return token.getType() == GoLexer.RAW_STRING_LIT && token.getText()
                .contains(Constants.NEWLINE);
    }

    // A line ending is any non-string token whose text contains a newline.
    boolean isLineEnding(Token token) {
        return token.getText().contains(Constants.NEWLINE);
    }

    boolean isKeyword(int type) {
        return type >= KEYWORD_START && type <= KEYWORD_END;
    }

    boolean isString(int type) {
        return type == GoLexer.RAW_STRING_LIT
                || type == GoLexer.INTERPRETED_STRING_LIT
                || type == GoLexer.RUNE_LIT;
    }

    boolean isNumber(int type) {
        return type >= NUM_START && type <= NUM_END;
    }

    boolean isComment(int type) {
        return type == GoLexer.COMMENT
                || type == GoLexer.LINE_COMMENT
                || type == GoLexer.COMMENT_NLSEMI
                || type == GoLexer.LINE_COMMENT_NLSEMI;
    }

    boolean isWhitespace(int type) {
        return type == GoLexer.WS || type == GoLexer.WS_NLSEMI;
    }

    boolean isEOF(int type){
        return type == GoLexer.EOF;
    }



}