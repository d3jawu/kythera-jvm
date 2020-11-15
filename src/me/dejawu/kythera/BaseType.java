package me.dejawu.kythera;

/**
 * Enum for core types.
 * For non-scalar types, additional information is needed
 * to fully describe and distinguish the type.
 */
public enum BaseType {
    UNIT,
    BOOL,
    // BYTE,
    // SHORT,
    INT,
    // LONG,
    // FLOAT,
    DOUBLE,
    // CHAR,
    STRUCT,
    // TUPLE,
    // MAP,
    LIST,
    // TUPLE,
    STR,
    FN,
    TYPE;
}