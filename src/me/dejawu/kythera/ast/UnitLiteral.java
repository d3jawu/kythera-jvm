package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

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
