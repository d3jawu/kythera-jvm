package me.dejawu.kythera.runtime;

import me.dejawu.kythera.BaseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

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
    list: ArrayList<KytheraValue<?>>
    fn: Function<KytheraValue<?>[], KytheraValue<?>>, which contains implementation (param list is part of the type)
    type: InternalTypeValue
     */
    public final T value;
    // fields stores the concrete members of this value
    public final HashMap<String, KytheraValue<?>> fields;
    // a reference to the value that represents this value's type
    public final KytheraValue<?> typeValue;

    private KytheraValue(T value, KytheraValue<?> typeValue) {
        this.value = value;
        this.typeValue = typeValue;
        this.fields = new HashMap<>();
    }

    // initialize with fields
    private KytheraValue(T value, KytheraValue<?> typeValue, HashMap<String, KytheraValue<?>> fields) {
        this.value = value;
        this.typeValue = typeValue;
        this.fields = fields;
    }


    // constructor for structs, which use their fields as their value
    /*private KytheraValue(KytheraValue<?> typeValue, HashMap<String, KytheraValue<?>> fields) {
        this.typeValue = typeValue;
        this.fields = new HashMap<>();
        this.value = (T) this.fields;
    }*/

    // self-referencing value, used only by TYPE root literal
    private KytheraValue(T value) {
        if (!(value instanceof InternalTypeValue)) {
            System.err.println("Invalid use of self-referencing value constructor on: " + value.toString());
            System.exit(1);
        }

        this.value = value;
        this.typeValue = this;
        this.fields = new HashMap<>();
    }

    // === reusable literals and factories below ===

    // types are interned and reused
    private static final HashMap<KytheraValue<InternalTypeValue>, KytheraValue<InternalTypeValue>> typeValueStore = new HashMap<>();

    // root type: a type that specifies not fields. All values are valid instances of the root type.
    // used to bootstrap TYPE
    public static KytheraValue<InternalTypeValue> ROOT_TYPE = internType(new KytheraValue<>(InternalTypeValue.ROOT_TYPE));

    // type that describes types and their operations
    public static KytheraValue<InternalTypeValue> TYPE = new KytheraValue<>(
        InternalTypeValue.TYPE,
        ROOT_TYPE,
        new HashMap<>() {{
            put("<:", null);
            put(">:", null);
        }}
    );

    // literals for scalar types (any type for which one type value can describe all instances of that type)
    public static KytheraValue<InternalTypeValue> NUM = wrapInternalTypeValue(InternalTypeValue.NUM);
    public static KytheraValue<InternalTypeValue> BOOL = wrapInternalTypeValue(InternalTypeValue.BOOL);

    // unit literal
    public static KytheraValue<Void> UNIT_VAL = new KytheraValue<>(null, ROOT_TYPE);

    // boolean literals
    public static KytheraValue<Boolean> TRUE = new KytheraValue<>(true, BOOL);
    public static KytheraValue<Boolean> FALSE = new KytheraValue<>(false, BOOL);

    public static KytheraValue<ArrayList<KytheraValue<?>>> getListValue(ArrayList<KytheraValue<?>> list, InternalTypeValue listType) {
        return new KytheraValue<ArrayList<KytheraValue<?>>>(
            list,
            wrapInternalTypeValue(listType),
            new HashMap<>() {{
                put("size", null);
            }}
        );
    }

    public static KytheraValue<Function<KytheraValue<?>[], KytheraValue<?>>> getFnValue(
        Function<KytheraValue<?>[], KytheraValue<?>> fn,
        InternalTypeValue fnType
    ) {
        return new KytheraValue<>(
            fn,
            wrapInternalTypeValue(fnType),
            null
        );
    }

    // TODO make this unnecessary
    // wraps an internal type value into a proper KytheraValue
    private static KytheraValue<InternalTypeValue> wrapInternalTypeValue(InternalTypeValue typeValue) {
        return new KytheraValue<>(
            typeValue,
            TYPE,
            new HashMap<>() {{
                put(">:", null);
                put("<:", null);
            }}
        );
    }

    // get reference to existing type value, or create and store if one does not exist.
    private static KytheraValue<InternalTypeValue> internType(KytheraValue<InternalTypeValue> val) {
        KytheraValue<InternalTypeValue> existingVal = typeValueStore.get(val);

        if (existingVal != null) {
            return existingVal;
        } else {
            typeValueStore.put(val, val);
            return val;
        }
    }

    // returns a struct type
    public static KytheraValue<InternalTypeValue> getStructTypeValue(HashMap<String, KytheraValue<?>> fields) {
        return internType(KytheraValue.wrapInternalTypeValue(
            new InternalTypeValue(
                BaseType.STRUCT,
                fields,
                new HashMap<>() {{
                    put("fields", fields);
                }}
            )
        ));
    }

    // returns a list type containing the specified member type
    public static KytheraValue<InternalTypeValue> getListTypeValue(KytheraValue<InternalTypeValue> memberType) {
        return internType(KytheraValue.wrapInternalTypeValue(
            new InternalTypeValue(
                BaseType.LIST,
                new HashMap<>() {{
                    put("memberType", memberType);
                }}
            )
        ));
    }

    public static KytheraValue<InternalTypeValue> getFnTypeValue(
        KytheraValue<InternalTypeValue>[] paramTypes,
        KytheraValue<InternalTypeValue> returnType
    ) {
        return internType(KytheraValue.wrapInternalTypeValue(
            new InternalTypeValue(
                BaseType.FN,
                new HashMap<>() {{
                    put("paramTypes", KytheraValue.getListValue(
                        new ArrayList() {{
                            for (KytheraValue<InternalTypeValue> paramType : paramTypes) {
                                add(paramType);
                            }
                        }},
                        KytheraValue.getListTypeValue(KytheraValue.TYPE).value
                    ));
                    put("returnType", returnType);
                }}
            )
        ));
    }

    @Override
    public String toString() {
        String out = "KytheraValue {\n";
        out += "\tValue:\n";
        out += "\t" + value.toString() + "\n";
        out += "} KytheraValue";
        return out;
    }
}
