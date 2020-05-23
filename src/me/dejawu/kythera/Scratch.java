package me.dejawu.kythera;

import me.dejawu.kythera.runtime.KytheraValue;

import java.util.function.Function;

// this class is only used for examining its generated assembly with ASMifier
public class Scratch {
    public static void main(String[] args) {
        Function<KytheraValue[], KytheraValue> noncapturingLambda = (input) -> {
            return new KytheraValue(2, KytheraValue.INT);
        };

        KytheraValue capturedValue = new KytheraValue(3, KytheraValue.INT);

        int capturedInt = 99;

        Function<KytheraValue[], KytheraValue> capturingLambda = (input) -> {
            System.out.println(capturedInt);
            return capturedValue;
        };

        // call
        noncapturingLambda.apply(null);
        capturingLambda.apply(null);
    }
}
