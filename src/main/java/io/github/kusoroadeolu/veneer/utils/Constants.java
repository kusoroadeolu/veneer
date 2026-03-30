package io.github.kusoroadeolu.veneer.utils;

import java.util.regex.Pattern;

public class Constants {
    private Constants(){}

   public final static Pattern CAPITAL_PATTERN =  Pattern.compile("[A-Z][A-Z0-9_]*");
}
