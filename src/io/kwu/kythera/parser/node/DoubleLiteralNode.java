package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.PrimitiveNodeType;

public class DoubleLiteralNode extends ExpressionNode {
    public final double value;

    public DoubleLiteralNode(double value) {
        super(NodeKind.LITERAL, PrimitiveNodeType.DOUBLE);
        this.value = value;
    }
}
