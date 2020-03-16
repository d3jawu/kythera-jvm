package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

public class IdentifierNode extends ExpressionNode {
    public final String name;

    public IdentifierNode(String name, NodeType type) {
        super(NodeKind.IDENTIFIER, type);
        this.name = name;
    }
}
