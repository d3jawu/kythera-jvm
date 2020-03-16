package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

/**
 * ExpressionNodes are statements that evaluate to a value and therefore also have a type.
 */
public abstract class ExpressionNode extends StatementNode {
    public NodeType type; // in some cases, may need to be set after ExpressionNode is instantiated

    ExpressionNode(NodeKind kind) {
        super(kind);
        this.type = null;
    }

    ExpressionNode(NodeKind kind, NodeType type) {
        super(kind);
        this.type = type;
    }
}
