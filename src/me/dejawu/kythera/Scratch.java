package me.dejawu.kythera;

import me.dejawu.kythera.runtime.KytheraValue;

import java.util.function.Function;

// this class is only used for examining its generated assembly with ASMifier
public class Scratch {
    public static void main(String[] args) {
        KytheraValue<Integer> x = KytheraValue.getIntValue(2);
        KytheraValue<Integer> y = KytheraValue.getIntValue(3);

        System.out.println(((Function) x.fields.get("+").value).apply(
                new KytheraValue[] { x, y }
        ));
    }
}
