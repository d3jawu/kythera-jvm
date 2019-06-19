package io.kwu.kythera.parser;

public class Token {
    public final TokenType tokentype;
    public final String value;

    public Token(TokenType tokentype, String value) {
        this.tokentype = tokentype;
        this.value = value;
    }
}
