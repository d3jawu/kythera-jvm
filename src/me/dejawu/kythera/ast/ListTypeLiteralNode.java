package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;

public class ListTypeLiteralNode extends TypeLiteralNode {
    public final ExpressionNode containedType;

    public ListTypeLiteralNode(ExpressionNode containedType) {
        super(BaseType.LIST);
        this.containedType = containedType;
    }
}
