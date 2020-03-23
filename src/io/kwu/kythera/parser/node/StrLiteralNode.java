package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.PrimitiveNodeType;

public class StrLiteralNode extends LiteralNode {
    public final String value;

    public StrLiteralNode(String value) {
        super(PrimitiveNodeType.STR);
        this.value = value;
    }
}
