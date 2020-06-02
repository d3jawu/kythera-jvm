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

    // root type: a type that specifies not fields. All values are valid instances of the root type.
    // used to bootstrap TYPE
    public static KytheraValue<InternalTypeValue> ROOT_TYPE = new KytheraValue<>(InternalTypeValue.ROOT_TYPE);

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
    public static KytheraValue<InternalTypeValue> INT = getTypeValue(InternalTypeValue.INT);
    public static KytheraValue<InternalTypeValue> BOOL = getTypeValue(InternalTypeValue.BOOL);

    // unit literal
    public static KytheraValue<Void> UNIT_VAL = new KytheraValue<>(null, ROOT_TYPE);

    // boolean literals
    public static KytheraValue<Boolean> TRUE = new KytheraValue<>(true, BOOL);
    public static KytheraValue<Boolean> FALSE = new KytheraValue<>(false, BOOL);

    // generates int literals with function implementations attached
    public static KytheraValue<Integer> getIntValue(int val) {
        return new KytheraValue<>(
            val,
            INT,
            new HashMap<>() {
                {
                    put("+", getFnValue((KytheraValue<?>[] args) -> getIntValue((Integer) args[0].value + (Integer) args[1].value), (InternalTypeValue) INT.value.instanceFields.get("+").value));
                    put("-", getFnValue((KytheraValue<?>[] args) -> getIntValue((Integer) args[0].value - (Integer) args[1].value), (InternalTypeValue) INT.value.instanceFields.get("-").value));
                    put("*", getFnValue((KytheraValue<?>[] args) -> getIntValue((Integer) args[0].value * (Integer) args[1].value), (InternalTypeValue) INT.value.instanceFields.get("*").value));
                    put("/", getFnValue((KytheraValue<?>[] args) -> getIntValue((Integer) args[0].value / (Integer) args[1].value), (InternalTypeValue) INT.value.instanceFields.get("/").value));
                    put("%", getFnValue((KytheraValue<?>[] args) -> getIntValue((Integer) args[0].value - (Integer) args[1].value), (InternalTypeValue) INT.value.instanceFields.get("%").value));

                    //        // temporary method to help with debugging, not in spec
                    put("print", getFnValue((KytheraValue<?>[] args) -> {
                        System.out.println(args[0]);
                        return UNIT_VAL;
                    }, new InternalTypeValue(
                        BaseType.FN,
                        new HashMap<>() {{
                            put("paramTypes", getListValue(
                                new ArrayList<>() {{
                                }},
                                TypeValueStore.getListType(KytheraValue.TYPE).value
                            ));
                            put("returnType", ROOT_TYPE);
                        }}
                    )));
                }
            }
        );
    }

    public static KytheraValue<ArrayList<KytheraValue<?>>> getListValue(ArrayList<KytheraValue<?>> list, InternalTypeValue listType) {
        return new KytheraValue<ArrayList<KytheraValue<?>>>(
            list,
            getTypeValue(listType),
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
            getTypeValue(fnType),
            null
        );
    }

    public static KytheraValue<InternalTypeValue> getTypeValue(InternalTypeValue typeValue) {
        return new KytheraValue<>(
            typeValue,
            TYPE,
            new HashMap<>() {{
                put(">:", null);
                put("<:", null);
            }}
        );
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
