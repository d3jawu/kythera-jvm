package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;
import io.kwu.kythera.parser.type.TypeNodeType;

public class TypeLiteralNode extends LiteralNode{
    NodeType underlyingType;

    public TypeLiteralNode(NodeType underlyingType) {
        super(new TypeNodeType());

        this.underlyingType = underlyingType;
    }
}
