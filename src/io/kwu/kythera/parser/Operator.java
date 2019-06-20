package io.kwu.kythera.parser;

public enum Operator {
    EQUALS("=", 1),
    PLUS_EQUALS("+=", 1),
    MINUS_EQUALS("-=", 1),
    TIMES_EQUALS("*=", 1),
    DIV_EQUALS("/=", 1),
    MOD_EQUALS("%=", 1),

    OR_LOGICAL("||", 3),
    AND_LOGICAL("&&", 4),

    // TODO bitwise boolean operators

    EQUIVALENT("==", 8),
    NOT_EQUIV("!=", 8),

    LESS_THAN("<", 9),
    GREATER_THAN(">", 9),
    LESS_EQUALS("<=", 9),
    GREATER_EQUALS(">=", 9),

    // TODO bit shift operators

    PLUS("+", 11),
    MINUS("-", 11),

    TIMES("*", 12),
    DIVIDE("/", 12),
    MODULUS("%", 12),

    NOT("!", 14);

    public final String symbol;
    public final int precedence; // higher precedence evaluated first
    Operator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }
}
