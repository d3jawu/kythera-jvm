package me.dejawu.kythera.runtime;

import me.dejawu.kythera.frontend.BaseType;

import java.util.HashMap;

public class InternalTypeValue {
    // create internal type value with no fields
    private InternalTypeValue(BaseType bt) {

    }

    // create internal type value with field and type mappings
    private InternalTypeValue(BaseType bt, HashMap<String, KytheraValue> types) {

    }

//    private static HashMap<String, KytheraValue> numberFields = new HashMap<>() {
//        {
//            put("+")
//        }
//    };

    public static InternalTypeValue INT = new InternalTypeValue(BaseType.INT);
}
