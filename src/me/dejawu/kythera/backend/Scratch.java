package me.dejawu.kythera.backend;

import me.dejawu.kythera.runtime.KytheraValue;

// this class is only used for examining its generated assembly with ASMifier
public class Scratch {
    public static void main(String[] args) {
        KytheraValue x = new KytheraValue(3.3f, KytheraValue.FLOAT);
    }
}
