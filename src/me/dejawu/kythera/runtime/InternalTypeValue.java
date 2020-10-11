package me.dejawu.kythera.runtime;

import me.dejawu.kythera.BaseType;

import java.util.HashMap;

public class InternalTypeValue {
    public final BaseType baseType;
    // additional data needed to describe type, e.g. function parameters/return type, list member type
    // don't confuse this with user-accessible/exposed fields! These go in KytheraValue.fields.
    public final HashMap<String, Object> typeMeta;

    // fields that instance of this type will have
    public final HashMap<String, KytheraValue<?>> instanceFields;

    public InternalTypeValue(BaseType bt, HashMap<String, KytheraValue<?>> instanceFields) {
        this.baseType = bt;
        this.instanceFields = instanceFields;
        this.typeMeta = new HashMap<>();
    }

    public InternalTypeValue(BaseType bt, HashMap<String, KytheraValue<?>> instanceFields, HashMap<String, Object> typeMeta) {
        this.baseType = bt;
        this.instanceFields = instanceFields;
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

    // the root type is a type value that specifies no fields for its instances.
    // TODO make this unnecessary
    public static InternalTypeValue ROOT_TYPE = new InternalTypeValue(BaseType.TYPE, new HashMap<>());

    // type values themselves have operations (and therefore fields), defined here
    public static InternalTypeValue TYPE = new InternalTypeValue(BaseType.TYPE, new HashMap<>() {{
        final KytheraValue<InternalTypeValue> TypeToBoolFn = KytheraValue.getFnTypeValue(new KytheraValue[]{KytheraValue.ROOT_TYPE}, KytheraValue.BOOL);

        // isSubtypeOf
        put("<:", TypeToBoolFn);

        // isSupertypeOf
        put(">:", TypeToBoolFn);
    }});

    // instances of unit have no fields.
    public static InternalTypeValue UNIT = new InternalTypeValue(BaseType.UNIT, new HashMap<>());

    public static InternalTypeValue BOOL = new InternalTypeValue(BaseType.BOOL, new HashMap<>() {

    });

    public static InternalTypeValue INT = new InternalTypeValue(BaseType.INT, new HashMap<>() {
        {
            // TODO get these fields from type literal instead of redefining them here

            final KytheraValue<InternalTypeValue> IntIntToIntFnType = KytheraValue.getFnTypeValue(
                new KytheraValue[]{KytheraValue.INT, KytheraValue.INT}, KytheraValue.INT
            );

            final KytheraValue<InternalTypeValue> IntIntToBoolFnType = KytheraValue.getFnTypeValue(
                new KytheraValue[]{KytheraValue.INT, KytheraValue.INT}, KytheraValue.BOOL
            );

            // no deep equivalence operations for INT
            put("==", IntIntToBoolFnType);
            put("!=", IntIntToBoolFnType);

            // <= is implemented as x < y || x == y, likewise for >=
            put("<", IntIntToBoolFnType);
            put(">", IntIntToBoolFnType);

            // arithmetic assignment (eg +=) is also handled by the operation given here
            put("+", IntIntToIntFnType);
            put("-", IntIntToIntFnType);
            put("*", IntIntToIntFnType);
            put("/", IntIntToIntFnType);
            put("%", IntIntToIntFnType);
        }
    });

    public static InternalTypeValue FLOAT = new InternalTypeValue(BaseType.FLOAT, new HashMap<>() {

    });
    public static InternalTypeValue CHAR = new InternalTypeValue(BaseType.CHAR, new HashMap<>() {

    });
}
