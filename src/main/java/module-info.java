module veneer {
    requires com.github.javaparser.core;
    requires org.antlr.antlr4.runtime;
    requires clique.core;
    requires transitive clique.spi;

    exports io.github.kusoroadeolu.veneer;
    exports io.github.kusoroadeolu.veneer.theme;
    exports io.github.kusoroadeolu.veneer.utils;
}
