package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.NodeType;

public abstract class LiteralNode extends ExpressionNode {
    public LiteralNode(NodeType type) {
        super(NodeKind.LITERAL, type);
    }
}
