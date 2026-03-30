package io.github.kusoroadeolu.veneer.utils;

import io.github.kusoroadeolu.clique.core.utils.Constants;
import io.github.kusoroadeolu.clique.spi.AnsiCode;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.function.BiConsumer;

public class Utils {


    private Utils(){
    }

    public static String formatNoTo3dp(int lineNo){
      return String.format("%3d | ", lineNo);
    }

    public static BufferedTokenStream toTokenStream(Lexer lexer){
        lexer.removeErrorListeners();
        BufferedTokenStream tokenStream = new BufferedTokenStream(lexer);
        tokenStream.fill();
        return tokenStream;
    }

    public static boolean isNullOrBlank(String s){
        return s == null || s.isBlank();
    }

       public static void styleMultiLineToken(Token token, int[] lineNumber, StyleBuilder sb,
                                    AnsiCode gutter, BiConsumer<Token, StyleBuilder> applyStyle) {
        List<String> lines = token.getText().lines().toList();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append(Constants.NEWLINE);
                sb.append(Utils.formatNoTo3dp(++lineNumber[0]), gutter);
            }
            applyStyle.accept(new FragmentToken(token, lines.get(i)), sb);
        }
    }

    public static void styleMultiLineToken(Token token, int[] lineNumber, StyleBuilder sb,
                                           AnsiCode gutter, Token prev , TriConsumer<Token, StyleBuilder, Token> applyStyle) {
        List<String> lines = token.getText().lines().toList();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append(Constants.NEWLINE);
                sb.append(Utils.formatNoTo3dp(++lineNumber[0]), gutter);
            }
            applyStyle.accept(new FragmentToken(token, lines.get(i)), sb, prev);
        }
    }


}
