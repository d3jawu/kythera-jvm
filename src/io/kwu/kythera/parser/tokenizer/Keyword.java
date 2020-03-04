package io.kwu.kythera.parser.tokenizer;

public enum Keyword {
    LET(),
    IF(),
    ELSE(),
    WHILE(),
    EACH(),
    SWITCH(),
    CASE(),
    FALLTHROUGH(),
    DEFAULT(),
    BREAK(),
    RETURN(),
    CONTINUE(),
    TYPE(),
    TYPEOF(),
    AS(),
    IMPORT(),
    EXPORT(),
    INCLUDE(),
    LOAD(),
    ;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
