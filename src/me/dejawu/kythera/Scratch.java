package me.dejawu.kythera;

import me.dejawu.kythera.runtime.KytheraValue;

// this class is only used for examining its generated assembly with ASMifier
public class Scratch {
    public static void main(String[] args) {
        KytheraValue x = new KytheraValue(3, KytheraValue.INT);
        KytheraValue y = new KytheraValue(4, KytheraValue.INT);

        // type check, later made optional by static type checking
//        if(!x.typeValue.value.internalFields.containsKey("+")) {
//            System.exit(1);
//        }

//        x.fields
    }
}
