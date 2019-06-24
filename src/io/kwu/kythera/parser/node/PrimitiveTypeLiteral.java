package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.BaseType;

/**
 * For scalar type literals (e.g. int, bool)
 */
public final class PrimitiveTypeLiteral {
    public final static class PrimitiveTypeLiteralNode extends TypeLiteralNode {
        PrimitiveTypeLiteralNode(BaseType type) {
            super(type);
        }
    }

    public static PrimitiveTypeLiteralNode INT = new PrimitiveTypeLiteralNode(BaseType.INT);
    public static PrimitiveTypeLiteralNode TYPE = new PrimitiveTypeLiteralNode(BaseType.TYPE);
}
