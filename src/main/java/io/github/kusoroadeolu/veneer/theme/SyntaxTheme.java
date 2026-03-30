package io.github.kusoroadeolu.veneer.theme;

import io.github.kusoroadeolu.clique.spi.AnsiCode;

/**
 * Defines the color scheme used by a {@link io.github.kusoroadeolu.veneer.SyntaxHighlighter}.
 * Implement this interface to create a custom theme.
 */
public interface SyntaxTheme {

    /** @return the color for keywords e.g. {@code public}, {@code void}, {@code return} */
    AnsiCode keyword();

    /** @return the color for string literals, text blocks, and Javadoc */
    AnsiCode stringLiteral();

    /** @return the color for numeric literals e.g. {@code 42}, {@code 3.14f}, {@code 0xFF} */
    AnsiCode numberLiteral();

    /** @return the color for comments */
    AnsiCode comment();

    /** @return the color for annotations e.g. {@code @Override} */
    AnsiCode annotation();

    /** @return the color for declared method and constructor names */
    AnsiCode method();

    /** @return the color for line number gutter text */
    AnsiCode gutter();

    /** @return the color for type references e.g. {@code String}, {@code List}, {@code MyClass} */
    AnsiCode types();

    /** @return the color for {@code static final} fields and enum constants */
    AnsiCode constants();
}