package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import io.github.kusoroadeolu.veneer.theme.SyntaxTheme;
import io.github.kusoroadeolu.veneer.utils.Utils;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import io.github.kusoroadeolu.veneer.LuaLexer;

import static io.github.kusoroadeolu.veneer.LuaLexer.*;
import static io.github.kusoroadeolu.veneer.utils.Utils.isNullOrBlank;
import static io.github.kusoroadeolu.veneer.utils.Utils.styleMultiLineToken;

public class LuaSyntaxHighlighter extends AbstractSyntaxHighlighter{

    public LuaSyntaxHighlighter() { super(); }
    public LuaSyntaxHighlighter(boolean showLineNumbers) { super(showLineNumbers); }
    public LuaSyntaxHighlighter(SyntaxTheme theme) { super(theme); }
    public LuaSyntaxHighlighter(SyntaxTheme theme, boolean showLineNumbers){super(theme, showLineNumbers);}

    @Override
    public String highlight(String s) {
        if (isNullOrBlank(s)) return "";

        StyleBuilder sb = Clique.styleBuilder();
        LuaLexer lexer = new LuaLexer(CharStreams.fromString(s));
        var tokenStream = Utils.toTokenStream(lexer);
        if (showLineNumbers) applyWithLines(sb, tokenStream);
        else applyWithoutLines(sb, tokenStream);
        return sb.get();
    }


    public void applyWithoutLines(StyleBuilder sb, BufferedTokenStream tokenStream){
        for (Token token : tokenStream.getTokens()){
            applyStyles(token, sb);
        }
    }

    public void applyWithLines(StyleBuilder sb, BufferedTokenStream tokenStream){
        int[] lineNumber = new int[1];
        sb.append(Utils.formatNoTo3dp(++lineNumber[0]), theme.gutter());

        for (Token token : tokenStream.getTokens()){
            if (isMultiLineToken(token)){
                styleMultiLineToken(token, lineNumber, sb, theme.gutter(), this::applyStyles);
            }else if (token.getType() == NL){
                applyStyles(token, sb);
                sb.append(Utils.formatNoTo3dp(++lineNumber[0]), theme.gutter());
            }else applyStyles(token, sb);
        }
    }


    void applyStyles(Token token, StyleBuilder sb){
        if (isKeyword(token)){
            sb.append(token.getText(), theme.keyword());
        }else if (isStringLiteral(token)){
            sb.append(token.getText(), theme.stringLiteral());
        } else if (isNumberLiteral(token)) {
            sb.append(token.getText(), theme.numberLiteral());
        }else if (isComment(token)) {
            sb.append(token.getText(), theme.comment());
        } else if (!isEOF(token)) {
            sb.append(token.getText());
        }
    }


    boolean isKeyword(Token token){
        int type = token.getType();
        return type >= BREAK && type <= FOR
                || type >= IN && type <= LOCAL
                || type == RETURN
                || (type >= NIL && type <= TRUE);
    }

    boolean isNumberLiteral(Token token){
        return token.getType() == INT || token.getType() == HEX || token.getType() == FLOAT || token.getType() == HEX_FLOAT;
    }

    boolean isStringLiteral(Token token){
        return token.getType() == NORMALSTRING || token.getType() == CHARSTRING || token.getType() == LONGSTRING;
    }

    boolean isComment(Token token){
        return token.getType() ==  COMMENT;
    }


    //The lua lexer doesn't give us a long comment token to work with so we're stuck using normal comments unlike how a long string token is given
    boolean isMultiLineToken(Token token){
        return isComment(token) || token.getType() == LONGSTRING;
    }

    boolean isEOF(Token token){
        return token.getType() == Token.EOF;
    }
}
