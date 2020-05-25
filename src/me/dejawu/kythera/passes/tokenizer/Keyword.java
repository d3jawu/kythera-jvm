package me.dejawu.kythera.passes.tokenizer;

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

    public final Token token;

    Keyword() {
        this.token = new Token(this.name().toLowerCase(), TokenType.KW);
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
