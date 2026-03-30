package io.github.kusoroadeolu.veneer.theme;

import io.github.kusoroadeolu.clique.Clique;
import io.github.kusoroadeolu.clique.spi.AnsiCode;

class NordSyntaxTheme implements SyntaxTheme {

    private static final AnsiCode KEYWORD        = Clique.rgb(129, 161, 193); // nord_frost2
    private static final AnsiCode STRING         = Clique.rgb(163, 190, 140); // nord_green
    private static final AnsiCode NUMBER_LITERAL = Clique.rgb(136, 192, 208); // nord_frost1
    private static final AnsiCode COMMENT        = Clique.rgb(76, 86, 106);   // nord_polar3
    private static final AnsiCode ANNOTATION     = Clique.rgb(235, 203, 139); // nord_yellow
    private static final AnsiCode METHOD         = Clique.rgb(94, 129, 172);  // nord_frost3
    private static final AnsiCode TYPES = Clique.rgb(143, 188, 187); // nord_frost0
    private static final AnsiCode CONSTANTS = Clique.rgb(180, 142, 173); // nord_aurora_purple


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