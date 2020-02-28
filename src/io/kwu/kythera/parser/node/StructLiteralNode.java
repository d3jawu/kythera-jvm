package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.StructNodeType;

import java.util.HashMap;
import java.util.Map;

public class StructLiteralNode extends LiteralNode {
    public final Map<String, ExpressionNode> values;

    // TODO constructor(s) TBD as parser is built
    public StructLiteralNode(StructNodeType type) {
        super(type);
        this.values = new HashMap<String, ExpressionNode>();
    }
}
