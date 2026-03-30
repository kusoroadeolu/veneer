package io.github.kusoroadeolu.veneer.utils;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public record FragmentToken(Token origin, String text) implements Token {

        @Override
        public String getText() {
            return text;
        }

        @Override
        public int getType() {
            return origin.getType();
        }

        @Override
        public int getChannel() {
            return origin.getChannel();
        }

        @Override
        public int getLine() {
            return origin.getLine();
        }

        @Override
        public int getCharPositionInLine() {
            return origin.getCharPositionInLine();
        }

        @Override
        public int getTokenIndex() {
            return origin.getTokenIndex();
        }

        @Override
        public int getStartIndex() {
            return origin.getStartIndex();
        }

        @Override
        public int getStopIndex() {
            return origin.getStopIndex();
        }

        @Override
        public TokenSource getTokenSource() {
            return origin.getTokenSource();
        }

        @Override
        public CharStream getInputStream() {
            return origin.getInputStream();
        }
        }