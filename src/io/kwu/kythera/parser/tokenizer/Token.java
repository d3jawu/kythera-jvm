package io.kwu.kythera.parser.tokenizer;

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
}
