package me.dejawu.kythera.runtime;

import me.dejawu.kythera.BaseType;

import java.util.HashMap;

public class KytheraValue<T> {
    /*
    value stores the actual value this KytheraValue is supposed to represent/expose.

    Kythera values are internally represented by the following Java types:
    unit: null
    bool: Boolean
    int: Integer
    float: Float
    char: Char
    struct: reference to fields (value's own fields are the exposed value)
    fn: InternalFnValue
    type: InternalTypeValue
     */
    public final T value;
    // fields stores the concrete members of the value
    public final HashMap<String, KytheraValue<?>> fields;
    public final KytheraValue<?> typeValue;

    public KytheraValue(T value, KytheraValue<?> typeValue) {
        this.value = value;
        this.typeValue = typeValue;
        this.fields = new HashMap<>();
    }

    // constructor for structs, which use their fields as their value
    public KytheraValue(KytheraValue<?> typeValue) {
        this.typeValue = typeValue;
        this.fields = new HashMap<>();
        this.value = (T) this.fields;
    }

    // self-referencing value, used only by TYPE root literal
    private KytheraValue(T value) {
        if (!(value instanceof BaseType)) {
            System.err.println("Invalid use of self-referencing value constructor on: " + value.toString());
            System.exit(1);
        }

        this.value = value;
        this.typeValue = this;
        this.fields = null;
    }

    // type literals
    public static KytheraValue<BaseType> TYPE = new KytheraValue<>(BaseType.TYPE);
    public static KytheraValue<BaseType> UNIT = new KytheraValue<>(BaseType.UNIT, TYPE);
    public static KytheraValue<BaseType> BOOL = new KytheraValue<>(BaseType.BOOL, TYPE);
    public static KytheraValue<BaseType> INT = new KytheraValue<>(BaseType.INT, TYPE);
//    public static KytheraValue<BaseType> DOUBLE = new KytheraValue<>(BaseType.DOUBLE, TYPE);
    public static KytheraValue<BaseType> FLOAT = new KytheraValue<>(BaseType.FLOAT, TYPE);
    public static KytheraValue<BaseType> CHAR = new KytheraValue<>(BaseType.CHAR, TYPE);
//     public static KytheraValue<BaseType> STRUCT = new KytheraValue<>
//     (BaseType.STRUCT, TYPE);
//    public static KytheraValue<BaseType> FN = new KytheraValue<>(BaseType
//    .FN, TYPE);


    // unit literal
    public static KytheraValue<Void> UNIT_VAL = new KytheraValue<>(null, UNIT);

    // boolean literals
    public static KytheraValue<Boolean> TRUE = new KytheraValue<>(true, BOOL);
    public static KytheraValue<Boolean> FALSE = new KytheraValue<>(false, BOOL);
}
