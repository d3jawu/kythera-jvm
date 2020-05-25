package me.dejawu.kythera.runtime;

import me.dejawu.kythera.BaseType;

import java.util.ArrayList;
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
        return intern(KytheraValue.getTypeValue(
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
    public static KytheraValue<InternalTypeValue> getListType(KytheraValue<InternalTypeValue> memberType) {
        return intern(KytheraValue.getTypeValue(
            new InternalTypeValue(
                BaseType.LIST,
                new HashMap<>() {{
                    put("memberType", memberType);
                }}
            )
        ));
    }

    public static KytheraValue<InternalTypeValue> getFnType(
        KytheraValue<InternalTypeValue>[] paramTypes, // maybe take a proper KytheraValue list?
        KytheraValue<InternalTypeValue> returnType
    ) {
        return intern(KytheraValue.getTypeValue(
            new InternalTypeValue(
                BaseType.FN,
                new HashMap<>() {{
                    put("paramTypes", KytheraValue.getListValue(
                        new ArrayList() {{
                            for (KytheraValue<InternalTypeValue> paramType : paramTypes) {
                                add(paramType);
                            }
                        }},
                            TypeValueStore.getListType(KytheraValue.TYPE).value
                    ));
                    put("returnType", returnType);
                }}
            )
        ));
    }
}
