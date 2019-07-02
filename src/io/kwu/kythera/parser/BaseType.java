package io.kwu.kythera.parser;

/**
 * Enum for core type categories.
 * For non-scalar types, additional information is needed
 * to fully describe and distinguish the type.
 */
public enum BaseType {
    INT("int", true),
    FLOAT("float", true),
    DOUBLE("double", true),
    BOOL("bool", true),
    TYPE("type", true), // type may not be scalar in the future
    LIST("list", false),
    TUPLE("tuple", false),
    MAP("map", false),
    STRUCT("struct", false),
    FN("fn", false);

    public final String name;
    public final boolean scalar;
    BaseType(String name, boolean scalar) {
        this.name = name;
        this.scalar = scalar;
    }
}
