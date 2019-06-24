package io.kwu.kythera.parser.node;

/**
 * All nodes extend StatementNode. Statements do not evaluate to a value, so they have no type.
 * Most StatementNodes extend ExpressionNode. Exceptions include "let" and "return".
 */
// TODO this could be a good place to send line/col numbers for debugging
public abstract class StatementNode {
    public final NodeKind kind;

    public StatementNode(NodeKind kind) {
        this.kind = kind;
    }
}
