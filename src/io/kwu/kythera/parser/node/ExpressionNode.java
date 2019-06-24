package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.NodeType;

/**
 *  ExpressionNodes are statements that evaluate to a value and therefore also have a type.
 */
public abstract class ExpressionNode extends StatementNode {
    public NodeType type; // may need to be set after ExpressionNode is instantiated

    ExpressionNode(NodeKind kind, NodeType type) {
        super(kind);
        this.type = type;
    }
}
