package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;

public class StructTypeLiteralNode extends TypeLiteralNode {

    public StructTypeLiteralNode() {
        super(BaseType.STRUCT);
    }
}
