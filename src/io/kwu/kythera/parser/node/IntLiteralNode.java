package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.PrimitiveNodeType;

public class IntLiteralNode extends ExpressionNode {
    public final int value;

    public IntLiteralNode(int value) {
        super(NodeKind.LITERAL, PrimitiveNodeType.INT);

        this.value = value;
    }
}
