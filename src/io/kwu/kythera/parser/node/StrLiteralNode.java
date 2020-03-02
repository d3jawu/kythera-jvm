package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

public class StrLiteralNode extends LiteralNode {
    public final String value;

    public StrLiteralNode(String value) {
        super(NodeType.STR);
        this.value = value;
    }
}
