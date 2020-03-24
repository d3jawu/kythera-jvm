package io.kwu.kythera.parser.node;

import java.util.HashMap;

public class StructLiteralNode extends LiteralNode {
    public final HashMap<String, ExpressionNode> values;

    // TODO constructor(s) TBD as parser is built
    public StructLiteralNode(StructTypeLiteralNode typeExp) {
        super(typeExp);
        this.values = new HashMap<String, ExpressionNode>();
    }

    @Override
    public void print(int indent) {

    }
}
