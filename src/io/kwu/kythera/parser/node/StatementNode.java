package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.ParserException;

// All nodes extend StatementNode. Statements have no type, so neither do StatementNodes.
// Very few StatementNodes are /not/ ParseNodes. Examples include "let" and "return".
// TODO this could be a good place to send line/col numbers for debugging
public abstract class StatementNode {
    public final NodeKind kind;

    public StatementNode(NodeKind kind) throws ParserException {
        this.kind = kind;
    }
}
