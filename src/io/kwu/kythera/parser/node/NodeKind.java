package io.kwu.kythera.parser.node;

enum NodeKind {
    UNARY,
    BINARY,
    ASSIGN,
    LITERAL,
    IDENTIFIER,
    LET,
    IF,
    WHILE,
    RETURN,
    AS,
    TYPEOF,
    CALL,
    ACCESS,
    THIS,
    BLOCK,
}
