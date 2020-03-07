package io.kwu.kythera.parser.type;

/**
 * Enum for core type categories.
 * For non-scalar types, additional information is needed
 * to fully describe and distinguish the type.
 */
public enum BaseType {
    UNIT("unit", true),
    BOOL("bool", true),
    BYTE("byte", true),
    SHORT("short", true),
    INT("int", true),
    LONG("long", true),
    FLOAT("float", true),
    DOUBLE("double", true),
    CHAR("char", true),
    LIST("list", false),
    TUPLE("tuple", false),
    MAP("map", false),
    STR("str", false),
    STRUCT("struct", false),
    FN("fn", false),
    TYPE("type", true); // type may not be scalar in the future


    public final String name;
    public final boolean scalar;
    public final NodeType nt;
    BaseType(String name, boolean scalar) {
        this.name = name;
        this.scalar = scalar;
        // am I even allowed to do this???
        if(this.scalar) {
            this.nt = new NodeType(this);
        } else {
            this.nt = null;
        }
    }
}