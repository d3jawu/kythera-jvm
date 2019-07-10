package io.kwu.kythera.parser.node;

public enum NodeKind {
    UNARY,
    BINARY,
    ASSIGN,
    LITERAL,
    IDENTIFIER,
    NEW,
    LET,
    IF,
    WHILE,
    RETURN,
    // AS,
    // TYPEOF,
    CALL,
    ACCESS,
    THIS,
    BLOCK,
}
