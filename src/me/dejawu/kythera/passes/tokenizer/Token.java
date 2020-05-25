package me.dejawu.kythera.passes.tokenizer;

public final class Token {
    public final TokenType tokentype;
    public final String value;

    public Token(String value, TokenType tokentype) {
        this.tokentype = tokentype;
        this.value = value;
    }

    @Override
    public String toString() {
        return tokentype.name() + ": " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token)) {
            return false;
        }

        Token t = (Token) o;

        return this.tokentype == t.tokentype && this.value.equals(t.value);
    }
}
