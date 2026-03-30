package io.github.kusoroadeolu.veneer.utils;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;

import java.util.Objects;
import java.util.Optional;

//Pos token for java parser
public record PositionalJavaToken(JavaToken token, Optional<Range> range) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionalJavaToken(JavaToken token1, Optional<Range> range1))) return false;
        return token.equals(token1) && range.equals(range1);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(token, range);
    }

    public static PositionalJavaToken of(JavaToken token){
        return new PositionalJavaToken(token, token.getRange());
    }
}