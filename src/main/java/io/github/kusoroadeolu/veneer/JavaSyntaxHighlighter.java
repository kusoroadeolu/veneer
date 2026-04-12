package io.github.kusoroadeolu.veneer;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.style.StyleBuilder;
import io.github.kusoroadeolu.veneer.utils.FragmentJavaToken;
import io.github.kusoroadeolu.veneer.utils.PositionalJavaToken;
import io.github.kusoroadeolu.veneer.theme.SyntaxTheme;
import io.github.kusoroadeolu.veneer.theme.SyntaxThemes;

import java.util.*;

import static com.github.javaparser.GeneratedJavaParserConstants.*;
import static io.github.kusoroadeolu.veneer.utils.Constants.NEWLINE;
import static io.github.kusoroadeolu.veneer.utils.Utils.formatNoTo3dp;
import static io.github.kusoroadeolu.veneer.utils.Utils.isNullOrBlank;

public class JavaSyntaxHighlighter extends AbstractSyntaxHighlighter{
    private final JavaParser parser;
    private final SyntaxTheme theme;
    private final boolean showLineNumbers;
    private static final String VAR = "var";

    public JavaSyntaxHighlighter() {
        this(true);
    }

    public JavaSyntaxHighlighter(SyntaxTheme theme)  {
        this(theme, true);
    }

    public JavaSyntaxHighlighter(boolean showLineNumbers)  {
        this(SyntaxThemes.DEFAULT, showLineNumbers);
    }

    public JavaSyntaxHighlighter(SyntaxTheme theme, boolean showLineNumbers) {
        var config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        this.parser = new JavaParser(config);
        this.theme = theme;
        this.showLineNumbers = showLineNumbers;
    }

    @Override
    public String highlight(String s) {
        if (isNullOrBlank(s)) return "";

        ParseResult<CompilationUnit> result = parser.parse(s);
        Optional<CompilationUnit> opUnit = result.getResult();
        if (opUnit.isEmpty()) return s;

        CompilationUnit unit = opUnit.get();
        Optional<TokenRange> opTokenRange = unit.getTokenRange();
        if (opTokenRange.isEmpty()) return s;

        TokenRange tokenRange = opTokenRange.get();
        return tryStyleString(unit,tokenRange);
    }

    String tryStyleString(CompilationUnit unit, TokenRange tokenRange){
        Set<PositionalJavaToken> methodNames = findMethodAndConstructorIdentifiers(unit);
        Set<PositionalJavaToken> typeTokens = findTypeDefinitions(unit);
        Set<PositionalJavaToken> constants = findConstantDefinitions(unit);
        var bundle = new AstBundle(methodNames, typeTokens, constants);
        StyleBuilder sb = Clique.styleBuilder();

        if (showLineNumbers) styleWithLines(sb, tokenRange, bundle);
        else styleWithoutLines(sb, tokenRange, bundle);

        return sb.toString();
    }


    void styleWithLines(StyleBuilder sb, TokenRange tokenRange, AstBundle astBundle){
        var lineNo = new int[1];
        for (JavaToken token : tokenRange){
            if (isMultiLineToken(token)){
                styleMultiLineContent(token, lineNo, sb, astBundle);
                continue;
            }

            //If we're the first token otherwise, if the prev token was an EOL
            if (startsOnNewLine(token)){
                appendLineNo(++lineNo[0], sb);
            }

            applyStyle(token, sb, astBundle);
        }
    }

    void styleWithoutLines(StyleBuilder sb, TokenRange tokenRange, AstBundle astBundle){
        for (JavaToken token : tokenRange){
            applyStyle(token, sb, astBundle);
        }
    }

    void applyStyle(JavaToken token, StyleBuilder sb, AstBundle astBundle){
        if (token.getCategory().isWhitespaceButNotEndOfLine()) {
            sb.appendAndReset(token.getText());
            return;
        }

        String text = token.getText();
        if(isConstant(token, astBundle.constants())){
            sb.appendAndReset(text, theme.constants());
        }else if (isStringOrJavadoc(token)){
            sb.appendAndReset(text, theme.stringLiteral());
        } else if (isNumberLiteral(token)) {
            sb.appendAndReset(text, theme.numberLiteral());
        }else if (isComment(token)){
            sb.appendAndReset(text, theme.comment());
        }else if(isEOL(token)){
            sb.appendAndReset(text);
        } else if (isAnnotation(token)) {
            sb.appendAndReset(text, theme.annotation());
        }else if(isTypeToken(token, astBundle.typeTokens())) {
                sb.appendAndReset(text, theme.types());
        }else if(isMethodOrConstructorIdentifier(token, astBundle.methodNames())) {
            sb.appendAndReset(text, theme.method());
        }else if (isKeyword(token) || isUnicodeEscape(token)) { //Moved this to the bottom to prevent "var" from clashing with valid identifiers
                sb.appendAndReset(text, theme.keyword());
        }else sb.appendAndReset(text);
    }

    void styleMultiLineContent(JavaToken token, int[] lineNo, StyleBuilder sb, AstBundle astBundle){
        List<FragmentJavaToken> tokens = token.getText()
                .lines()
                .map(text -> new FragmentJavaToken(token.getKind(), text))
                .toList();

        int size = tokens.size();

        for (int i = 0; i < size; ++i) {
            var custom = tokens.get(i);
            if (i == 0 && startsOnNewLine(token)) {
                appendLineNo(++lineNo[0], sb);
            } else if (i > 0) {
                sb.appendAndReset(NEWLINE);
                appendLineNo(++lineNo[0], sb);
            }

            applyStyle(custom, sb, astBundle);
        }
    }

    //Finds type defs
    Set<PositionalJavaToken> findTypeDefinitions(CompilationUnit unit){
        var typeTokens = new HashSet<PositionalJavaToken>();
        unit.findAll(ClassOrInterfaceType.class).forEach(t ->
                t.getTokenRange().ifPresent(r -> {
            for (JavaToken token : r) {
                if (token.getKind() == IDENTIFIER)
                    typeTokens.add(PositionalJavaToken.of(token));
            }
        }));
        return typeTokens;
    }

    //Finds static final fields or enum vars
    Set<PositionalJavaToken> findConstantDefinitions(CompilationUnit unit){
        var constantTokens = new HashSet<PositionalJavaToken>();
        unit.findAll(FieldDeclaration.class).stream()
                .filter(f -> f.isStatic() && f.isFinal())
                .forEach(f -> f.getVariables().forEach(v ->
                        v.getName().getTokenRange()
                                .ifPresent(r -> {
                    for (JavaToken token : r) {
                        if (token.getKind() == IDENTIFIER)
                            constantTokens.add(PositionalJavaToken.of(token));
                    }
                })));

        unit.findAll(EnumConstantDeclaration.class).forEach(e ->
                e.getName().getTokenRange().ifPresent(r -> {
                    for (JavaToken token : r)
                        if (token.getKind() == IDENTIFIER)
                            constantTokens.add(PositionalJavaToken.of(token));
                }));

        unit.findAll(FieldAccessExpr.class)
                .forEach(f -> f.getName().getTokenRange().ifPresent(r -> {
                    for (JavaToken token : r){
                        String name = token.getText();
                        if (name.equals(name.toUpperCase())){
                            constantTokens.add(PositionalJavaToken.of(token));
                        }
                    }
                }));

        return constantTokens;
    }

    //Find valid method identifiers
    Set<PositionalJavaToken> findMethodAndConstructorIdentifiers(CompilationUnit unit){
        var methodTokens = new HashSet<PositionalJavaToken>();
        unit.findAll(MethodDeclaration.class).forEach(m ->
                m.getName().getTokenRange().ifPresent(r -> {
                    for (JavaToken token : r) {
                        if (token.getKind() == IDENTIFIER)
                            methodTokens.add(PositionalJavaToken.of(token));
                    }
                }));

        unit.findAll(ConstructorDeclaration.class).forEach(c ->
                c.getName().getTokenRange().ifPresent(r -> {
                    for (JavaToken token : r) {
                        if (token.getKind() == IDENTIFIER)
                            methodTokens.add(PositionalJavaToken.of(token));
                    }
                }));

        unit.findAll(MethodCallExpr.class).forEach(m -> m.getTokenRange().ifPresent(r -> {
            for (JavaToken token : r) {
                var nextToken = token.getNextToken();
                if (token.getKind() == IDENTIFIER && nextToken.isPresent() && Objects.equals(nextToken.get().getKind(), LPAREN))
                    methodTokens.add(PositionalJavaToken.of(token));
            }
        }));

        return methodTokens;
    }

    boolean isConstant(JavaToken token, Set<PositionalJavaToken> constants){
        return tokenAndRangeEqual(token, constants);
    }

    boolean isTypeToken(JavaToken token, Set<PositionalJavaToken> typeTokens) {
        return tokenAndRangeEqual(token, typeTokens);
    }

    void appendLineNo(int lineNo, StyleBuilder sb){
        sb.appendAndReset(formatNoTo3dp(lineNo), theme.gutter());
    }

    boolean isMethodOrConstructorIdentifier(JavaToken token, Set<PositionalJavaToken> methodTokens){
        return tokenAndRangeEqual(token, methodTokens);
    }

    boolean isKeyword(JavaToken token){
        return token.getCategory().isKeyword()
                || (token.getCategory().isIdentifier() && token.getText().equals(VAR));
    }

    //Text blocks, string literals and java docs will have the same color
    boolean isStringOrJavadoc(JavaToken token){
        return token.getKind() >= STRING_LITERAL && token.getKind() <= TEXT_BLOCK_LITERAL || token.getKind() == ENTER_JAVADOC_COMMENT || token.getKind() == JAVADOC_COMMENT;
    }

    //These will probably have blue colors
    boolean isNumberLiteral(JavaToken token){
        return token.getKind() >= LONG_LITERAL && token.getKind() <= HEXADECIMAL_FLOATING_POINT_LITERAL;
    }

    //Same colors as keywords
    boolean isUnicodeEscape(JavaToken token){
        return token.getKind() == UNICODE_ESCAPE;
    }

    boolean isComment(JavaToken token){
        return token.getCategory().isComment() || token.getKind() == ENTER_MULTILINE_COMMENT || token.getKind() == MULTI_LINE_COMMENT;
    }

    //Helper for line counting
    boolean isMultiLineToken(JavaToken token){
        return token.getKind() == ENTER_MULTILINE_COMMENT || token.getKind() == ENTER_JAVADOC_COMMENT ||  token.getKind() == MULTI_LINE_COMMENT || token.getKind() == JAVADOC_COMMENT || token.getKind() == TEXT_BLOCK_LITERAL || token.getKind() == TEXT_BLOCK_CONTENT;
    }

    boolean startsOnNewLine(JavaToken token){
        return token.getPreviousToken().isEmpty() || isEOL(token.getPreviousToken().get());
    }

    boolean isEOL(JavaToken token){
        return token.getCategory().isEndOfLine();
    }

    boolean isAnnotation(JavaToken token){
        return token.getKind() == JavaToken.Kind.AT.getKind();
    }

    boolean tokenAndRangeEqual(JavaToken token, Set<PositionalJavaToken> tokens){
        return tokens.contains(PositionalJavaToken.of(token));
    }

    //Since idk what to name this tbh, this is just a wrapper class to hold tokens/strings gotten from walking the ast, that we couldnt have normally styled from the token range
    private record AstBundle(Set<PositionalJavaToken> methodNames, Set<PositionalJavaToken> typeTokens, Set<PositionalJavaToken> constants){

    }

}