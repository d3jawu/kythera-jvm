package io.kwu.kythera.parser;

// TODO are these all named after their symbols, or what they do?
public enum Operator {
    // assignments
    EQUALS("=", 1),
    PLUS_EQUALS("+=", 1),
    MINUS_EQUALS("-=", 1),
    TIMES_EQUALS("*=", 1),
    DIV_EQUALS("/=", 1),
    MOD_EQUALS("%=", 1),

    // boolean logical
    OR_LOGICAL("||", 3),
    AND_LOGICAL("&&", 4),

    // TODO bitwise boolean operators

    // comparison
    EQUIVALENT("==", 8),
    NOT_EQUIV("!=", 8),

    LESS_THAN("<", 9),
    GREATER_THAN(">", 9),
    LESS_EQUIV("<=", 9),
    GREATER_EQUIV(">=", 9),

    // TODO bit shift operators

    // arithmetic
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
