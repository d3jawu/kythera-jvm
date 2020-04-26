package io.kwu.kythera.frontend;

import io.kwu.kythera.frontend.node.TypeLiteralNode;

/**
 * Enum for core types.
 * For non-scalar types, additional information is needed
 * to fully describe and distinguish the type.
 */
public enum BaseType {
    UNIT("unit", true),
    BOOL("bool", true), //    BYTE("byte", true),
    //    SHORT("short", true),
    INT("int", true), //    LONG("long", true),
    //    FLOAT("float", true),
    DOUBLE("double", true),
    CHAR("char", true),
    STRUCT("struct", false), //    TUPLE("tuple", false),
    //    MAP("map", false),
//    LIST("list", false),
//    STR("str", false),
    FN("fn", false),
    TYPE("type", false);

    public final String name;
    public final boolean scalar;
    public final TypeLiteralNode typeLiteral;

    BaseType(String name, boolean scalar) {
        this.name = name;
        this.scalar = scalar;

        // all types have a base type literal for their primitive case, even
        // non-scalar types
        this.typeLiteral = new TypeLiteralNode(this);
    }

    public static TypeLiteralNode typeLiteralOf(String name) {
        for (BaseType bt : values()) {
            if (bt.name.equals(name)) {
                return bt.typeLiteral;
            }
        }

        return null;
    }
}