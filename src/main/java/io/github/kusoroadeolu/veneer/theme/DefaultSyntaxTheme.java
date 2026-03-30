package io.github.kusoroadeolu.veneer.theme;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

class DefaultSyntaxTheme implements SyntaxTheme {

    private static final AnsiCode KEYWORD       = Clique.rgb(207, 131, 109);
    private static final AnsiCode STRING        = Clique.rgb(106, 171, 115);
    private static final AnsiCode NUMBER_LITERAL = Clique.rgb(42, 172, 184);
    private static final AnsiCode COMMENT        = Clique.rgb(128, 128, 128);
    private static final AnsiCode ANNOTATION     = Clique.rgb(187, 181, 41);
    private static final AnsiCode METHOD        = Clique.rgb(86, 168, 245);
    private static final AnsiCode TYPES          = Clique.rgb(169, 183, 198);
    private static final AnsiCode CONSTANTS     = Clique.rgb(199, 125, 187);
    @Override public AnsiCode keyword()       { return KEYWORD; }
    @Override public AnsiCode stringLiteral()        { return STRING; }
    @Override public AnsiCode numberLiteral() { return NUMBER_LITERAL; }
    @Override public AnsiCode comment()       { return COMMENT; }
    @Override public AnsiCode annotation()    { return ANNOTATION; }
    @Override public AnsiCode method()        { return METHOD; }
    @Override public AnsiCode gutter()        { return COMMENT; }

    @Override
    public AnsiCode types() {
        return TYPES;
    }

    @Override
    public AnsiCode constants() {
        return CONSTANTS;
    }
}