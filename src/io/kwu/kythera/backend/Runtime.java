package io.kwu.kythera.backend;

import java.util.ArrayList;

public class Runtime {
    public static class ExampleValue<T> {
        public final T value;
        public final ExampleValue typeValue;

        public ExampleValue(T value, ExampleValue typeValue) {
            this.value = value;
            this.typeValue = typeValue;
        }
    }

    public static void main() {
        ExampleValue x = new ExampleValue<Integer>(2, null);
    }
}
