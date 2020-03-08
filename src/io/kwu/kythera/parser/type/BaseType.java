package io.kwu.kythera.parser.type;

/**
 * Enum for core types.
 * For non-scalar types, additional information is needed
 * to fully describe and distinguish the type.
 */
public enum BaseType {
    UNIT("unit", true),
    BOOL("bool", true),
//    BYTE("byte", true),
//    SHORT("short", true),
    INT("int", true),
//    LONG("long", true),
//    FLOAT("float", true),
    DOUBLE("double", true),
    CHAR("char", true),
    STRUCT("struct", false),
    TUPLE("tuple", false),
    MAP("map", false),
    LIST("list", false),
    STR("str", false),
    FN("fn", false),
    TYPE("type", false);

    public final String name;
    public final boolean scalar;
    public final NodeType nt;
    BaseType(String name, boolean scalar) {
        this.name = name;
        this.scalar = scalar;
        // am I even allowed to do this???
        if(this.scalar) {
            this.nt = new PrimitiveNodeType(this);
        } else {
            this.nt = null;
        }
    }
}