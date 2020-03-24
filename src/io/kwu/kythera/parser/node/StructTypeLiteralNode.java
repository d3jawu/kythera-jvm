package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.BaseType;

import java.util.HashMap;

public class StructTypeLiteralNode extends TypeLiteralNode {
    public final HashMap<String, ExpressionNode> entries;

    public StructTypeLiteralNode() {
        super(BaseType.STRUCT);
        this.entries = new HashMap<>();
    }

    public StructTypeLiteralNode(HashMap<String, ExpressionNode> entries) {
        super(BaseType.STRUCT);
        this.entries = entries;
    }
}
