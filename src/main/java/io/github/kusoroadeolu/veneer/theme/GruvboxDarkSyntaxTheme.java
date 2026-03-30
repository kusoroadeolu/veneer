package io.github.kusoroadeolu.veneer.theme;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

class GruvboxDarkSyntaxTheme implements SyntaxTheme {

    private static final AnsiCode KEYWORD        = Clique.rgb(251, 73, 52);   // *gb_red
    private static final AnsiCode STRING         = Clique.rgb(184, 187, 38);  // *gb_green
    private static final AnsiCode NUMBER_LITERAL = Clique.rgb(211, 134, 155); // *gb_purple
    private static final AnsiCode COMMENT        = Clique.rgb(146, 131, 116); // *gb_gray
    private static final AnsiCode ANNOTATION     = Clique.rgb(250, 189, 47);  // *gb_yellow
    private static final AnsiCode METHOD         = Clique.rgb(142, 192, 124); // *gb_aqua
    private static final AnsiCode GUTTER         = Clique.rgb(102, 92, 84);   // gb_bg3
    private static final AnsiCode TYPES          = Clique.rgb(254, 128, 25); // gb_orange
    private static final AnsiCode CONSTANTS      = Clique.rgb(131, 165, 152); // gb_blue

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