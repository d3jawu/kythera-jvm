package io.kwu.kythera.parser.tokenizer;

/**
 * operators are named after their symbols, not their function (e.g. PLUS instead of ADD)
 * because they could later be overloaded to do anything
 */
public enum Symbol {
    // assignments, see here: https://introcs.cs.princeton.edu/java/11precedence/
    EQUALS("=", 1, SymbolKind.ASSIGN),
    PLUS_EQUALS("+=", 1, SymbolKind.ASSIGN),
    MINUS_EQUALS("-=", 1, SymbolKind.ASSIGN),
    TIMES_EQUALS("*=", 1, SymbolKind.ASSIGN),
    DIV_EQUALS("/=", 1, SymbolKind.ASSIGN),
    MOD_EQUALS("%=", 1, SymbolKind.ASSIGN),

    // boolean logical
    OR_LOGICAL("||", 3, SymbolKind.LOGICAL),
    AND_LOGICAL("&&", 4, SymbolKind.LOGICAL),

    // TODO bitwise boolean operators

    // comparison
    EQUIV("==", 8, SymbolKind.COMPARE),
    DEEP_EQUIV("===", 8, SymbolKind.COMPARE),
    NOT_EQUIV("!=", 8, SymbolKind.COMPARE),
    DEEP_NOT_EQUIV("!==", 8, SymbolKind.COMPARE),

    LESS_THAN("<", 9, SymbolKind.COMPARE),
    GREATER_THAN(">", 9, SymbolKind.COMPARE),
    LESS_EQUIV("<=", 9, SymbolKind.COMPARE),
    GREATER_EQUIV(">=", 9, SymbolKind.COMPARE),

    // arithmetic
    PLUS("+", 11, SymbolKind.ARITHMETIC),
    MINUS("-", 11, SymbolKind.ARITHMETIC),

    TIMES("*", 12, SymbolKind.ARITHMETIC),
    DIVIDE("/", 12, SymbolKind.ARITHMETIC),
    MODULUS("%", 12, SymbolKind.ARITHMETIC),

    BANG("!", 14, SymbolKind.LOGICAL),

    // access
    DOT(".", 16, SymbolKind.ACCESS),
    OPEN_PAREN("(", 16, SymbolKind.ACCESS),
    CLOSE_PAREN(")", 16, SymbolKind.ACCESS),
    OPEN_BRACKET("[", 16, SymbolKind.ACCESS),
    CLOSE_BRACKET("]", 16, SymbolKind.ACCESS),
    // TODO maybe the braces should have precedence 13 since they are used for object creation, not access?
    OPEN_BRACE("{", 16, SymbolKind.ACCESS),
    CLOSE_BRACE("}", 16, SymbolKind.ACCESS),

    // punctuation (non-operator symbols)
    COMMA(",", -1, SymbolKind.PUNC),
    SEMICOLON(";", -1, SymbolKind.PUNC),
    COLON(":", -1, SymbolKind.PUNC);

    public final String symbol;
    public final int precedence; // higher precedence evaluated first
    public final SymbolKind kind;

    Symbol(String symbol, int precedence, SymbolKind kind) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.kind = kind;
    }

    public static Symbol symbolOf(String symbol) {
        for (Symbol o : values()) {
            if (o.symbol.equals(symbol)) {
                return o;
            }
        }

        return null;
    }

    public enum SymbolKind {
        ASSIGN,
        LOGICAL,
        COMPARE,
        ARITHMETIC,
        ACCESS,
        PUNC
    }
}
