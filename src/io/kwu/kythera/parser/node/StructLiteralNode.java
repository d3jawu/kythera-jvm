package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.StructNodeType;

import java.util.HashMap;

public class StructLiteralNode extends LiteralNode {
    public final HashMap<String, ExpressionNode> values;

    // TODO constructor(s) TBD as parser is built
    public StructLiteralNode(StructNodeType type) {
        super(type);
        this.values = new HashMap<String, ExpressionNode>();
    }
}
