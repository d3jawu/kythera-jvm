package me.dejawu.kythera.frontend.node;

import me.dejawu.kythera.Main;
import me.dejawu.kythera.frontend.BaseType;

import java.io.PrintStream;

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
        public void print(int indent, PrintStream stream) {
            Main.printlnWithIndent("UnitLiteralNode {}", indent, stream);
        }
    }
}
