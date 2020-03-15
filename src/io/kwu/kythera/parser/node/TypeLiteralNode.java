package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;
import io.kwu.kythera.parser.type.TypeNodeType;

public class TypeLiteralNode extends LiteralNode{
    NodeType typeValue;

    public TypeLiteralNode(NodeType typeValue) {
        super(new TypeNodeType());

        this.typeValue = typeValue;
    }
}
