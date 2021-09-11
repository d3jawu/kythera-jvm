package me.dejawu.kythera.stages.lexer;

public enum Symbol {
    // assignments, see here: https://introcs.cs.princeton.edu/java/11precedence/
    EQUAL("=", 1),
    PLUS_EQUAL("+=", 1),
    MINUS_EQUAL("-=", 1),
    STAR_EQUAL("*=", 1),
    SLASH_EQUAL("/=", 1),
    PERCENT_EQUAL("%=", 1),

    BAR("|", 3),
    AND("&", 4),

    // boolean logical
    BAR_BAR("||", 3),
    AND_AND("&&", 4),

    // TODO bitwise boolean operators

    // comparison
    EQUAL_EQUAL("==", 8),
//    EQUAL_EQUAL_EQUAL("===", 8),
    BANG_EQUAL("!=", 8),
//    BANG_EQUAL_EQUAL("!==", 8),

    LESS("<", 9),
    GREATER(">", 9),
    LESS_EQUAL("<=", 9),
    GREATER_EQUAL(">=", 9),

    // arithmetic
    PLUS("+", 11),
    MINUS("-", 11),

    STAR("*", 12),
    SLASH("/", 12),
    PERCENT("%", 12),

    BANG("!", 14),

    // access
    DOT(".", 16),
    LEFT_PAREN("(", 16),
    RIGHT_PAREN(")", 16),
    LEFT_BRACKET("[", 16),
    RIGHT_BRACKET("]", 16),
    // TODO maybe the braces should have precedence 13 since they are used for object creation, not access?
    LEFT_BRACE("{", 16),
    RIGHT_BRACE("}", 16),

    // punctuation (non-operator symbols)
    COMMA(",", 0),
    SEMICOLON(";", 0),
    COLON(":", 0),
    EQUAL_GREATER("=>", 0);

    public final String symbol;
    public final int precedence; // higher precedence evaluated first
    public final Token token;

    Symbol(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.token = new Token(symbol, TokenType.SYM);
    }

    public static Symbol from(String symbol) {
        for (Symbol o : values()) {
            if (o.symbol.equals(symbol)) {
                return o;
            }
        }

        return null;
    }
}
