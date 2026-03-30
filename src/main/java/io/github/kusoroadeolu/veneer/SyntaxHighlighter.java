package io.github.kusoroadeolu.veneer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A syntax highlighter that styles Java source code with ANSI color codes.
 */
public interface SyntaxHighlighter {

    /**
     * Highlights the given Java source string.
     *
     * @param s the source code to highlight
     * @return the styled string with ANSI color codes
     */
    String highlight(String s);

    /**
     * Highlights the Java source file at the given path.
     *
     * @param path the path to the source file
     * @return the styled string with ANSI color codes
     * @throws IOException if the file cannot be read
     */
    default String highlight(Path path) throws IOException{
        Objects.requireNonNull(path, "Path cannot be null");
        return highlight(Files.readString(path));
    }

    /**
     * Highlights the given source string and prints it to the specified stream.
     *
     * @param s      the source code to highlight
     * @param stream the stream to print to
     */
    default void print(String s, PrintStream stream){
        stream.println(highlight(s));
    }

    /**
     * Highlights the given source string and prints it to {@link System#out}.
     *
     * @param s the source code to highlight
     */
    default void print(String s){
        print(s, System.out);
    }

    /**
     * Highlights the source file at the given path and prints it to the specified stream.
     *
     * @param path   the path to the source file
     * @param stream the stream to print to
     * @throws IOException if the file cannot be read
     */
    default void print(Path path, PrintStream stream) throws IOException{
        stream.println(highlight(path));
    }

    /**
     * Highlights the source file at the given path and prints it to {@link System#out}.
     *
     * @param path the path to the source file
     * @throws IOException if the file cannot be read
     */
    default void print(Path path) throws IOException{
        print(path, System.out);
    }
}