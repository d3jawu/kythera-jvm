package me.dejawu.kythera.runtime;

import me.dejawu.kythera.BaseType;

import java.util.HashMap;

public class InternalTypeValue {
    public final BaseType baseType;
    // additional data needed to describe type, e.g. function parameters/return type, list member type
    public final HashMap<String, KytheraValue<?>> typeMeta;

    public InternalTypeValue(BaseType bt) {
        this.baseType = bt;
        // empty hashmap
        this.typeMeta = new HashMap<>();
    }

    public InternalTypeValue(BaseType bt, HashMap<String, KytheraValue<?>> typeMeta) {
        this.baseType = bt;
        this.typeMeta = typeMeta;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InternalTypeValue)) {
            return false;
        }

        InternalTypeValue internalTypeValue = (InternalTypeValue) other;

        return this.baseType.equals(internalTypeValue.baseType) && this.typeMeta.equals(internalTypeValue.typeMeta);
    }

    // scalar types (and the root type) have a reusable static primitive
    public static InternalTypeValue TYPE = new InternalTypeValue(BaseType.TYPE);

    public static InternalTypeValue INT = new InternalTypeValue(BaseType.INT, new HashMap<>() {
        {
            KytheraValue<InternalTypeValue> IntToIntFnType = new KytheraValue<>(TYPE, KytheraValue.TYPE, new HashMap<>() {
                {
                    put("params", null);
                    put("returns", KytheraValue.INT);
                }
            });

            // no deep equivalence operations for INT
            put("==", null);
            put("!=", null);

            // <= is implemented as x < y || x == y, likewise for >=
            put("<", null);
            put(">", null);

            // arithmetic assignment (eg +=) is also handled by the operation given here
            put("+", null);
            put("-", null);
            put("*", null);
            put("/", null);
            put("%", null);
        }});
    public static InternalTypeValue UNIT = new InternalTypeValue(BaseType.UNIT);
    public static InternalTypeValue BOOL = new InternalTypeValue(BaseType.BOOL);
    public static InternalTypeValue FLOAT = new InternalTypeValue(BaseType.FLOAT);
    public static InternalTypeValue CHAR = new InternalTypeValue(BaseType.CHAR);
}
