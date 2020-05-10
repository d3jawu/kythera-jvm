package me.dejawu.kythera.runtime;

import me.dejawu.kythera.BaseType;

import java.util.ArrayList;
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
    fn: InternalFnValue, which contains param list and implementation
    type: InternalTypeValue
     */
    public final T value;
    // fields stores the concrete members of this value
    public final HashMap<String, KytheraValue<?>> fields;
    public final KytheraValue<?> typeValue;

    public KytheraValue(T value, KytheraValue<?> typeValue) {
        this.value = value;
        this.typeValue = typeValue;
        this.fields = new HashMap<>();
    }

    // initialize with fields
    public KytheraValue(T value, KytheraValue<?> typeValue, HashMap<String, KytheraValue<?>> fields) {
        this.value = value;
        this.typeValue = typeValue;
        this.fields = fields;
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

    // literals for primitive types (i.e. only one type value is needed to describe all instances of that type)
    // currently all types point to the raw TYPE for their typeValue.
    // in the future, this may become more elaborate.

    // root type is the simplest possible type: a type with no fields
    public static KytheraValue<InternalTypeValue> TYPE = new KytheraValue<>(InternalTypeValue.TYPE);
    // Unit has no fields
    public static KytheraValue<InternalTypeValue> UNIT = new KytheraValue<>(InternalTypeValue.UNIT, TYPE);
    public static KytheraValue<InternalTypeValue> BOOL = new KytheraValue<>(InternalTypeValue.BOOL, TYPE, new HashMap<>());
    public static KytheraValue<InternalTypeValue> INT = new KytheraValue<>(InternalTypeValue.INT, TYPE);
    // public static KytheraValue<BaseType> DOUBLE = new KytheraValue<>(BaseType.DOUBLE, TYPE);
    public static KytheraValue<BaseType> FLOAT = new KytheraValue<>(BaseType.FLOAT, TYPE);
    public static KytheraValue<BaseType> CHAR = new KytheraValue<>(BaseType.CHAR, TYPE);


    // unit literal
    public static KytheraValue<Void> UNIT_VAL = new KytheraValue<>(null, UNIT);

    // boolean literals
    public static KytheraValue<Boolean> TRUE = new KytheraValue<>(true, BOOL);
    public static KytheraValue<Boolean> FALSE = new KytheraValue<>(false, BOOL);
}
