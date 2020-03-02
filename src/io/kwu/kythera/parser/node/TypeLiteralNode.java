package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.BaseType;
import io.kwu.kythera.parser.type.NodeType;

/**
 * Comes from a type literal in syntax.
 */
public abstract class TypeLiteralNode extends LiteralNode {
    public final BaseType baseType;
    public TypeLiteralNode(BaseType baseType) {
        super(NodeType.fromBaseType.get(baseType));
        this.baseType = baseType;
    }
}
