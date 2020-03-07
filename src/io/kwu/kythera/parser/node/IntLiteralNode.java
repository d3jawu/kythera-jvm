package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

public class IntLiteralNode extends ExpressionNode {
    public final int value;

    public IntLiteralNode(int value) {
        super(NodeKind.LITERAL, NodeType.INT);

        this.value = value;
    }
}
