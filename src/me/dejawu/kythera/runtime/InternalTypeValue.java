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

    // the root type is a type value whose instances have no fields.
    public static InternalTypeValue ROOT_TYPE = new InternalTypeValue(BaseType.TYPE, new HashMap<>());

    // instances of unit have no fields.
    public static InternalTypeValue UNIT = new InternalTypeValue(BaseType.UNIT, new HashMap<>());

    public static InternalTypeValue BOOL = new InternalTypeValue(BaseType.BOOL, new HashMap<>() {

    });

    public static InternalTypeValue INT = new InternalTypeValue(BaseType.INT, new HashMap<>() {
        {
            final KytheraValue<InternalTypeValue> IntToIntFnType = TypeValueStore.getListType(TYPE);

            final KytheraValue<InternalTypeValue> IntToBoolFnType = new KytheraValue<>(
                new InternalTypeValue(BaseType.FN, new HashMap<>() {
                    {
                        // list containing type values
                        put("params", new KytheraValue<InternalListValue>(
                            // list value containing entries
                            new InternalListValue() {
                                {
                                    add(TypeValueStore.INT);
                                }
                            },
                            // type value for this list
                            new KytheraValue<>(
                                new InternalTypeValue(BaseType.LIST, new HashMap<>() {
                                    {
                                        put("memberType", TypeValueStore.TYPE);
                                    }
                                }),
                                null
                            ),
                            // fields attached to this list
                            // TODO list methods
                            new HashMap<>()
                        ));
                        put("return", TypeValueStore.BOOL);
                    }
                }),
                TypeValueStore.TYPE,
                null
            );

            // no deep equivalence operations for INT
            put("==", IntToBoolFnType);
            put("!=", IntToBoolFnType);

            // <= is implemented as x < y || x == y, likewise for >=
            put("<", IntToBoolFnType);
            put(">", IntToBoolFnType);

            // arithmetic assignment (eg +=) is also handled by the operation given here
            put("+", IntToIntFnType);
            put("-", IntToIntFnType);
            put("*", IntToIntFnType);
            put("/", IntToIntFnType);
            put("%", IntToIntFnType);
        }
    });
    public static InternalTypeValue FLOAT = new InternalTypeValue(BaseType.FLOAT, new HashMap<>() {

    });
    public static InternalTypeValue CHAR = new InternalTypeValue(BaseType.CHAR, new HashMap<>() {

    });

    // TODO compound type factories (with reuse)
}
