package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.BaseType;
import io.kwu.kythera.parser.type.NodeType;
import io.kwu.kythera.parser.type.PrimitiveNodeType;

/**
 * Static expression nodes for type literals of scalar types (e.g. int, bool, etc)
 */
public final class PrimitiveTypeLiteral {
    private final static class PrimitiveTypeLiteralNode extends TypeLiteralNode {
        PrimitiveTypeLiteralNode(BaseType bt) {
            super(PrimitiveNodeType.fromBaseType(bt));
        }
    }

    public static PrimitiveTypeLiteralNode UNIT = new PrimitiveTypeLiteralNode(BaseType.UNIT);
    public static PrimitiveTypeLiteralNode BOOL = new PrimitiveTypeLiteralNode(BaseType.BOOL);
    public static PrimitiveTypeLiteralNode INT = new PrimitiveTypeLiteralNode(BaseType.INT);
    public static PrimitiveTypeLiteralNode DOUBLE = new PrimitiveTypeLiteralNode(BaseType.DOUBLE);
    public static PrimitiveTypeLiteralNode CHAR = new PrimitiveTypeLiteralNode(BaseType.CHAR);
}
