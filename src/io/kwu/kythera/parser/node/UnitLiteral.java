package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class UnitLiteral {
    public static UnitLiteralNode UNIT;

    static {
        UNIT = new UnitLiteralNode();
    }

    private static class UnitLiteralNode extends LiteralNode {
        private UnitLiteralNode() {
            super(BaseType.UNIT.typeLiteral);
        }

        @Override
        public void print(int indent) {
            Main.printlnWithIndent("UnitLiteralNode {}", indent);
        }
    }
}
