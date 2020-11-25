package me.dejawu.kythera.stages.tokenizer;

public enum Keyword {
    CONST(),
    LET(),
    IF(),
    ELSE(),
    WHILE(),
    EACH(),
    WHEN(),
    BREAK(),
    RETURN(),
    CONTINUE(),
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
