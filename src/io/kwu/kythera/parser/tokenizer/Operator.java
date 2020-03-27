package io.kwu.kythera.parser.tokenizer;

/**
 * operators are named after their symbols, not their function (e.g. PLUS instead of ADD)
 * because they could later be overloaded to do anything
 */
public enum Operator {
    // assignments, see here: https://introcs.cs.princeton.edu/java/11precedence/
    EQUALS("=", 1, OperatorKind.ASSIGN),
    PLUS_EQUALS("+=", 1, OperatorKind.ASSIGN),
    MINUS_EQUALS("-=", 1, OperatorKind.ASSIGN),
    TIMES_EQUALS("*=", 1, OperatorKind.ASSIGN),
    DIV_EQUALS("/=", 1, OperatorKind.ASSIGN),
    MOD_EQUALS("%=", 1, OperatorKind.ASSIGN),

    // boolean logical
    OR_LOGICAL("||", 3, OperatorKind.LOGICAL),
    AND_LOGICAL("&&", 4, OperatorKind.LOGICAL),

    // TODO bitwise boolean operators

    // comparison
    EQUIV("==", 8, OperatorKind.COMPARE),
    DEEP_EQUIV("===", 8, OperatorKind.COMPARE),
    NOT_EQUIV("!=", 8, OperatorKind.COMPARE),
    DEEP_NOT_EQUIV("!==", 8, OperatorKind.COMPARE),

    LESS_THAN("<", 9, OperatorKind.COMPARE),
    GREATER_THAN(">", 9, OperatorKind.COMPARE),
    LESS_EQUIV("<=", 9, OperatorKind.COMPARE),
    GREATER_EQUIV(">=", 9, OperatorKind.COMPARE),

    // arithmetic
    PLUS("+", 11, OperatorKind.ARITHMETIC),
    MINUS("-", 11, OperatorKind.ARITHMETIC),

    TIMES("*", 12, OperatorKind.ARITHMETIC),
    DIVIDE("/", 12, OperatorKind.ARITHMETIC),
    MODULUS("%", 12, OperatorKind.ARITHMETIC),

    BANG("!", 14, OperatorKind.LOGICAL),

    // access
    DOT(".", 16, OperatorKind.ACCESS),
    OPEN_PAREN("(", 16, OperatorKind.ACCESS),
    CLOSE_PAREN(")", 16, OperatorKind.ACCESS),
    OPEN_BRACKET("[", 16, OperatorKind.ACCESS),
    CLOSE_BRACKET("]", 16, OperatorKind.ACCESS),
    // TODO maybe the braces should have precedence 13 since they are used for object creation, not access?
    OPEN_BRACE("{", 16, OperatorKind.ACCESS),
    CLOSE_BRACE("}", 16, OperatorKind.ACCESS);

    public final String symbol;
    public final int precedence; // higher precedence evaluated first
    public final OperatorKind kind;

    Operator(String symbol, int precedence, OperatorKind kind) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.kind = kind;
    }

    public static Operator symbolOf(String symbol) {
        for(Operator o : values()) {
            if(o.symbol.equals(symbol)) {
                return o;
            }
        }

        return null;
    }

    public enum OperatorKind {
        ASSIGN,
        LOGICAL,
        COMPARE,
        ARITHMETIC,
        ACCESS,
    }
}
