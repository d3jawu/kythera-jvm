package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.BaseType;

/**
 * Static expression nodes for type literals of scalar types (e.g. int, bool, etc)
 */
public final class PrimitiveTypeLiteral {
    private final static class PrimitiveTypeLiteralNode extends TypeLiteralNode {
        PrimitiveTypeLiteralNode(BaseType type) {
            super(type);
        }
    }

    public static PrimitiveTypeLiteralNode UNIT = new PrimitiveTypeLiteralNode(BaseType.UNIT);
    public static PrimitiveTypeLiteralNode BOOL = new PrimitiveTypeLiteralNode(BaseType.BOOL);
//    public static PrimitiveTypeLiteralNode BYTE = new PrimitiveTypeLiteralNode(BaseType.BYTE);
//    public static PrimitiveTypeLiteralNode SHORT = new PrimitiveTypeLiteralNode(BaseType.SHORT);
    public static PrimitiveTypeLiteralNode INT = new PrimitiveTypeLiteralNode(BaseType.INT);
//    public static PrimitiveTypeLiteralNode LONG = new PrimitiveTypeLiteralNode(BaseType.LONG);
//    public static PrimitiveTypeLiteralNode FLOAT = new PrimitiveTypeLiteralNode(BaseType.FLOAT);
    public static PrimitiveTypeLiteralNode DOUBLE = new PrimitiveTypeLiteralNode(BaseType.DOUBLE);
    public static PrimitiveTypeLiteralNode CHAR = new PrimitiveTypeLiteralNode(BaseType.CHAR);
}
