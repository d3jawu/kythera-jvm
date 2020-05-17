package me.dejawu.kythera.runtime;

import me.dejawu.kythera.BaseType;

import java.util.HashMap;
import java.util.List;

// builds and reuses type values
public class TypeValueStore {
    // TODO do we want to store whole KytheraValues or just InternalTypeValues?
    private static final HashMap<KytheraValue<InternalTypeValue>, KytheraValue<InternalTypeValue>> store = new HashMap<>();

    // get reference to existing type value, or create and store if one does not exist.
    private static KytheraValue<InternalTypeValue> intern(KytheraValue<InternalTypeValue> val) {
        final KytheraValue<InternalTypeValue> existingVal = store.get(val);

        if (existingVal != null) {
            return existingVal;
        } else {
            store.put(val, val);
            return val;
        }
    }

    // returns a struct type
    public static KytheraValue<InternalTypeValue> getStructType(HashMap<String, KytheraValue<?>> fields) {
        return intern(new KytheraValue<>(
            new InternalTypeValue(
                BaseType.STRUCT,
                fields,
                new HashMap<>() {{
                    put("fields", fields);
                }}
            ),
            TYPE
        ));
    }

    // returns a list type containing the specified member type
    public static KytheraValue<InternalTypeValue> getListType(KytheraValue<InternalTypeValue> memberType) {
        return intern(new KytheraValue<>(
            new InternalTypeValue(
                BaseType.LIST,
                new HashMap<>() {{
                    put("memberType", memberType);
                }}
            ),
            TYPE
        ));
    }

    public static KytheraValue<InternalTypeValue> getFnType(
        List<KytheraValue<InternalTypeValue>> paramTypes, // maybe take a proper KytheraValue list?
        KytheraValue<InternalTypeValue> returnType
    ) {
        return intern(new KytheraValue<>(
            new InternalTypeValue(
                BaseType.FN,
                new HashMap<>() {{
                    put("paramTypes", new KytheraValue<>(
                        new InternalListValue() {{
                            for (KytheraValue<InternalTypeValue> paramType : paramTypes) {
                                add(paramType);
                            }
                        }},
                        getListType(TYPE)
                    ));
                    put("returnType", returnType);
                }}
            ),
            TYPE
        ));
    }

    // literals for primitive types (i.e. only one type value is needed to describe all instances of that type)

    // root type is the simplest possible type: a type with no fields
    public static KytheraValue<InternalTypeValue> TYPE = new KytheraValue<>(InternalTypeValue.ROOT_TYPE);
    // Unit has no fields
    public static KytheraValue<InternalTypeValue> UNIT = new KytheraValue<>(InternalTypeValue.UNIT, TYPE);
    public static KytheraValue<InternalTypeValue> BOOL = new KytheraValue<>(InternalTypeValue.BOOL, TYPE, new HashMap<>());
    public static KytheraValue<InternalTypeValue> INT = new KytheraValue<>(InternalTypeValue.INT, TYPE, new HashMap<>());

    // public static KytheraValue<BaseType> DOUBLE = new KytheraValue<>(BaseType.DOUBLE, TYPE);
    public static KytheraValue<InternalTypeValue> FLOAT = new KytheraValue<>(InternalTypeValue.FLOAT, TYPE);
    public static KytheraValue<InternalTypeValue> CHAR = new KytheraValue<>(InternalTypeValue.CHAR, TYPE);

}
