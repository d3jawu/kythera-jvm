package me.dejawu.kythera.stages.lexer;

public enum Keyword {
    CONST(),
    LET(),
    IF(),
    ELSE(),
    WHILE(),
    WHEN(),
    BREAK(),
    RETURN(),
    CONTINUE(),
    TYPEOF(),
    IMPORT(),
    EXPORT(),
    INCLUDE(),
    LOAD(),
    ;

    public final Token token;

    Keyword() {
        this.token = new Token(this.name().toLowerCase(), TokenType.KW);
    }

    public static Keyword from(String keyword) {
        for (Keyword o : values()) {
            if (o.toString().toLowerCase().equals(keyword)) {
                return o;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
