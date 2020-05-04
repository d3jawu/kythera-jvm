package me.dejawu.kythera.ast;

import java.io.PrintStream;

/**
 * All nodes extend StatementNode. Statements do not evaluate to a value, so
 * they have no type.
 * Most StatementNodes extend ExpressionNode. Exceptions include "let" and
 * "return".
 */
// TODO this could be a good place to send line/col numbers for debugging
public abstract class StatementNode {
    public final NodeKind kind;

    public StatementNode(NodeKind kind) {
        this.kind = kind;
    }

    public abstract void print(int indent, PrintStream stream);
}
