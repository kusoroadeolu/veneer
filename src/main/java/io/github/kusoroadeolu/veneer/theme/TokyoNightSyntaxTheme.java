package io.github.kusoroadeolu.veneer.theme;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

class TokyoNightSyntaxTheme implements SyntaxTheme {

    private static final AnsiCode KEYWORD        = Clique.rgb(187, 154, 247); // magenta
    private static final AnsiCode STRING         = Clique.rgb(158, 206, 106); // green
    private static final AnsiCode NUMBER_LITERAL = Clique.rgb(125, 207, 255); // cyan
    private static final AnsiCode COMMENT        = Clique.rgb(86, 95, 137);   // comment
    private static final AnsiCode ANNOTATION     = Clique.rgb(224, 175, 104); // yellow
    private static final AnsiCode METHOD         = Clique.rgb(122, 162, 247); // blue
    private static final AnsiCode GUTTER         = Clique.rgb(59, 66, 97);    // fgGutter
    private static final AnsiCode TYPES         = Clique.rgb(42, 195, 222); // teal
    private static final AnsiCode CONSTANTS    = Clique.rgb(255, 158, 100); // orange


    @Override public AnsiCode keyword()       { return KEYWORD; }
    @Override public AnsiCode stringLiteral()        { return STRING; }
    @Override public AnsiCode numberLiteral() { return NUMBER_LITERAL; }
    @Override public AnsiCode comment()       { return COMMENT; }
    @Override public AnsiCode annotation()    { return ANNOTATION; }
    @Override public AnsiCode method()        { return METHOD; }
    @Override public AnsiCode gutter()        { return GUTTER; }

    @Override
    public AnsiCode types() {
        return TYPES;
    }

    @Override
    public AnsiCode constants() {
        return CONSTANTS;
    }
}