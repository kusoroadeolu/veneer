package io.github.kusoroadeolu.veneer;

import org.antlr.v4.runtime.*;

import java.util.ArrayDeque;
import java.util.Deque;
import io.github.kusoroadeolu.veneer.JavaScriptLexer;

/**
 * All lexer methods that used in grammar (IsStrictMode)
 * should start with Upper Case Char similar to Lexer rules.
 */
abstract class JavaScriptLexerBase extends Lexer
{
    /**
     * Stores values of nested modes. By default, mode is strict or
     * defined externally (useStrictDefault)
     */
    private final Deque<Boolean> scopeStrictModes = new ArrayDeque<>();

    private Token lastToken = null;
    /**
     * Default value of strict mode
     * Can be defined externally by setUseStrictDefault
     */
    private boolean useStrictDefault = false;
    /**
     * Current value of strict mode
     * Can be defined during parsing, see StringFunctions.js and StringGlobal.js samples
     */
    private boolean useStrictCurrent = false;
    /**
     * Keeps track of the current depth of nested template string backticks.
     * E.g. after the X in:
     * `${a ? `${X
     * templateDepth will be 2. This variable is needed to determine if a `}` is a
     * plain CloseBrace, or one that closes an expression inside a template string.
     */
    private int templateDepth = 0;

    public JavaScriptLexerBase(CharStream input) {
        super(input);
    }

    public boolean IsStartOfFile() {
        return lastToken == null;
    }

    public boolean getStrictDefault() {
        return useStrictDefault;
    }

    public void setUseStrictDefault(boolean value) {
        useStrictDefault = value;
        useStrictCurrent = value;
    }

    public boolean IsStrictMode() {
        return useStrictCurrent;
    }

    public boolean IsInTemplateString() {
        return this.templateDepth > 0;
    }

    /**
     * Return the next token from the character stream and records this last
     * token in case it resides on the default channel. This recorded token
     * is used to determine when the lexer could possibly match a regex
     * literal. Also changes scopeStrictModes stack if tokenize special
     * string 'use strict';
     *
     * @return the next token from the character stream.
     */
    @Override
    public Token nextToken() {
        Token next = super.nextToken();

        if (next.getChannel() == Token.DEFAULT_CHANNEL) {
            // Keep track of the last token on the default channel.
            this.lastToken = next;
        }

        return next;
    }

    protected void ProcessOpenBrace() {
        useStrictCurrent = !scopeStrictModes.isEmpty() && scopeStrictModes.peek() || useStrictDefault;
        scopeStrictModes.push(useStrictCurrent);
    }

    protected void ProcessCloseBrace() {
        useStrictCurrent = !scopeStrictModes.isEmpty() ? scopeStrictModes.pop() : useStrictDefault;
    }

    protected void ProcessStringLiteral() {
        if (lastToken == null || lastToken.getType() == JavaScriptLexer.OpenBrace) {
            String text = getText();
            if (text.equals("\"use strict\"") || text.equals("'use strict'")) {
                if (!scopeStrictModes.isEmpty())
                    scopeStrictModes.pop();
                useStrictCurrent = true;
                scopeStrictModes.push(useStrictCurrent);
            }
        }
    }

    public void IncreaseTemplateDepth() {
        this.templateDepth++;
    }

    public void DecreaseTemplateDepth() {
        this.templateDepth--;
    }

    /**
     * Returns {@code true} if the lexer can match a regex literal.
     */
    protected boolean IsRegexPossible() {
                                       
        if (this.lastToken == null) {
            // No token has been produced yet: at the start of the input,
            // no division is possible, so a regex literal _is_ possible.
            return true;
        }

        return switch (this.lastToken.getType()) {
            case JavaScriptLexer.Identifier, JavaScriptLexer.NullLiteral, JavaScriptLexer.BooleanLiteral,
                 JavaScriptLexer.This, JavaScriptLexer.CloseBracket, JavaScriptLexer.CloseParen,
                 JavaScriptLexer.OctalIntegerLiteral, JavaScriptLexer.DecimalLiteral, JavaScriptLexer.HexIntegerLiteral,
                 JavaScriptLexer.StringLiteral, JavaScriptLexer.PlusPlus, JavaScriptLexer.MinusMinus ->
                // After any of the tokens above, no regex literal can follow.
                    false;
            default -> true;
                // In all other cases, a regex literal _is_ possible.
        };
    }

    /**
     * Returns {@code true} if the lexer can match a JSX opening element.
     */
    protected boolean IsJsxPossible() {
        
        if (this.lastToken == null) {
            return false;
        }

        return switch (this.lastToken.getType()) {
            case JavaScriptLexer.Assign, JavaScriptLexer.Colon, JavaScriptLexer.Comma, JavaScriptLexer.Default,
                 JavaScriptLexer.QuestionMark, JavaScriptLexer.Return, JavaScriptLexer.OpenBrace,
                 JavaScriptLexer.OpenParen, JavaScriptLexer.JsxOpeningElementOpenBrace,
                 JavaScriptLexer.JsxChildrenOpenBrace, JavaScriptLexer.Yield, JavaScriptLexer.ARROW -> true;
            default -> false;
        };
    }
}