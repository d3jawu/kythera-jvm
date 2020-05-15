package me.dejawu.kythera.runtime;

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
    // a reference to the value that represents this value's type
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
    public KytheraValue(T value) {
        if (!(value instanceof InternalTypeValue)) {
            System.err.println("Invalid use of self-referencing value constructor on: " + value.toString());
            System.exit(1);
        }

        this.value = value;
        this.typeValue = this;
        this.fields = null;
    }

    // TODO hardcode common function types?

    // unit literal
    public static KytheraValue<Void> UNIT_VAL = new KytheraValue<>(null, TypeValueStore.UNIT);

    // boolean literals
    public static KytheraValue<Boolean> TRUE = new KytheraValue<>(true, TypeValueStore.BOOL);
    public static KytheraValue<Boolean> FALSE = new KytheraValue<>(false, TypeValueStore.BOOL);
}
