package io.kwu.kythera.backend;

import io.kwu.kythera.frontend.BaseType;

public class KytheraValue<T> {
    public final T value;
    public final KytheraValue typeValue;

    public KytheraValue(T value, KytheraValue typeValue) {
        this.value = value;
        this.typeValue = typeValue;
    }

    // self-referencing value, used only by TYPE literal
    private KytheraValue(T value) {
        if (!(value instanceof BaseType)) {
            System.err.println("Invalid use of self-referencing value " +
                "constructor.");
            System.exit(1);
        }

        this.value = value;
        this.typeValue = this;
    }

    public static KytheraValue<BaseType> TYPE = new KytheraValue<>(BaseType.TYPE);
    public static KytheraValue<BaseType> UNIT = new KytheraValue<>(BaseType.UNIT, TYPE);
    public static KytheraValue<BaseType> BOOL = new KytheraValue<>(BaseType.BOOL, TYPE);
    public static KytheraValue<BaseType> INT = new KytheraValue<>(BaseType.INT, TYPE);
    public static KytheraValue<BaseType> DOUBLE = new KytheraValue<>(BaseType.DOUBLE, TYPE);
    public static KytheraValue<BaseType> CHAR = new KytheraValue<>(BaseType.CHAR, TYPE);
//     public static KytheraValue<BaseType> STRUCT = new KytheraValue<>
//     (BaseType.STRUCT, TYPE);
//    public static KytheraValue<BaseType> FN = new KytheraValue<>(BaseType
//    .FN, TYPE);

}
