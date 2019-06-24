package io.kwu.kythera.parser;

/**
 * Enum for core type categories.
 */
public enum BaseType {
    INT("int", true),
    FLOAT("float", true),
    DOUBLE("double", true),
    BOOL("bool", true),
    TYPE("type", true), // type may not be scalar in the future
    STRUCT("struct", false),
    TUPLE("tuple", false);
    // LIST
    // MAP

    public final String name;
    public final boolean scalar;
    BaseType(String name, boolean scalar) {
        this.name = name;
        this.scalar = scalar;
    }
}
