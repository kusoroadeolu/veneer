package io.github.kusoroadeolu.veneer;

import io.github.kusoroadeolu.veneer.theme.SyntaxTheme;
import io.github.kusoroadeolu.veneer.theme.SyntaxThemes;

abstract class AbstractSyntaxHighlighter implements SyntaxHighlighter{
    final SyntaxTheme theme;
    final boolean showLineNumbers;

    public AbstractSyntaxHighlighter(SyntaxTheme theme, boolean showLineNumbers) {
        this.theme = theme;
        this.showLineNumbers = showLineNumbers;
    }

    public AbstractSyntaxHighlighter(){
        this(true);
    }

    public AbstractSyntaxHighlighter(boolean showLineNumbers){
        this(SyntaxThemes.DEFAULT, showLineNumbers);
    }

    public AbstractSyntaxHighlighter(SyntaxTheme theme){
        this(theme, false);
    }
}
