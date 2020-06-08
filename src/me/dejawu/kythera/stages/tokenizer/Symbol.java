package me.dejawu.kythera.stages.tokenizer;

/**
 * operators are named after their symbols, not their function (e.g. PLUS
 * instead of ADD)
 * because they could later be overloaded to do anything
 */
public enum Symbol {
    // assignments, see here: https://introcs.cs.princeton
    // .edu/java/11precedence/
    EQUALS("=", 1, SymbolKind.ASSIGN, TokenType.OP),
    PLUS_EQUALS("+=", 1, SymbolKind.ASSIGN, TokenType.OP),
    MINUS_EQUALS("-=", 1, SymbolKind.ASSIGN, TokenType.OP),
    TIMES_EQUALS("*=", 1, SymbolKind.ASSIGN, TokenType.OP),
    DIV_EQUALS("/=", 1, SymbolKind.ASSIGN, TokenType.OP),
    MOD_EQUALS("%=", 1, SymbolKind.ASSIGN, TokenType.OP),

    // boolean logical
    OR_LOGICAL("||", 3, SymbolKind.LOGICAL, TokenType.OP),
    AND_LOGICAL("&&", 4, SymbolKind.LOGICAL, TokenType.OP),

    // TODO bitwise boolean operators

    // comparison
    EQUIV("==", 8, SymbolKind.COMPARE, TokenType.OP),
    DEEP_EQUIV("===", 8, SymbolKind.COMPARE, TokenType.OP),
    NOT_EQUIV("!=", 8, SymbolKind.COMPARE, TokenType.OP),
    DEEP_NOT_EQUIV("!==", 8, SymbolKind.COMPARE, TokenType.OP),

    LESS_THAN("<", 9, SymbolKind.COMPARE, TokenType.OP),
    GREATER_THAN(">", 9, SymbolKind.COMPARE, TokenType.OP),
    LESS_EQUIV("<=", 9, SymbolKind.COMPARE, TokenType.OP),
    GREATER_EQUIV(">=", 9, SymbolKind.COMPARE, TokenType.OP),

    // arithmetic
    PLUS("+", 11, SymbolKind.ARITHMETIC, TokenType.OP),
    MINUS("-", 11, SymbolKind.ARITHMETIC, TokenType.OP),

    TIMES("*", 12, SymbolKind.ARITHMETIC, TokenType.OP),
    DIVIDE("/", 12, SymbolKind.ARITHMETIC, TokenType.OP),
    MODULUS("%", 12, SymbolKind.ARITHMETIC, TokenType.OP),

    BANG("!", 14, SymbolKind.LOGICAL, TokenType.OP),

    // access
    DOT(".", 16, SymbolKind.ACCESS, TokenType.PUNC),
    OPEN_PAREN("(", 16, SymbolKind.ACCESS, TokenType.PUNC),
    CLOSE_PAREN(")", 16, SymbolKind.ACCESS, TokenType.PUNC),
    OPEN_BRACKET("[", 16, SymbolKind.ACCESS, TokenType.PUNC),
    CLOSE_BRACKET("]", 16, SymbolKind.ACCESS, TokenType.PUNC), // TODO maybe the braces should have precedence 13 since they are used
    //  for object creation, not access?
    OPEN_BRACE("{", 16, SymbolKind.ACCESS, TokenType.PUNC),
    CLOSE_BRACE("}", 16, SymbolKind.ACCESS, TokenType.PUNC),

    // punctuation (non-operator symbols)
    COMMA(",", -1, SymbolKind.PUNC, TokenType.PUNC),
    SEMICOLON(";", -1, SymbolKind.PUNC, TokenType.PUNC),
    COLON(":", -1, SymbolKind.PUNC, TokenType.PUNC);

    public final String symbol;
    public final int precedence; // higher precedence evaluated first
    public final SymbolKind kind;
    public final Token token;

    Symbol(String symbol, int precedence, SymbolKind kind, TokenType tokenType) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.kind = kind;
        this.token = new Token(symbol, tokenType);
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
