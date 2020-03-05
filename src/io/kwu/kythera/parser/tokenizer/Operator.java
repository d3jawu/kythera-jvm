package io.kwu.kythera.parser.tokenizer;

/** operators are named after their symbols, not their function (e.g. PLUS instead of ADD)
 * because they could later be overloaded to do anything
 */
public enum Operator {
    // assignments, see here: https://introcs.cs.princeton.edu/java/11precedence/
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
    EQUIV("==", 8),
    DEEP_EQUIV("===", 8),
    NOT_EQUIV("!=", 8),
    DEEP_NOT_EQUIV("!==", 8),

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

    BANG("!", 14),

    // access
    DOT(".", 16),
    OPEN_PAREN("(", 16),
    CLOSE_PAREN(")", 16),
    OPEN_BRACKET("[", 16),
    CLOSE_BRACKET("]", 16),
    // maybe the braces should have precedence 13 since they are used for object creation, not access?
    OPEN_BRACE("{", 16),
    CLOSE_BRACE("}", 16);

    public final String symbol;
    public final int precedence; // higher precedence evaluated first
    Operator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }
}
