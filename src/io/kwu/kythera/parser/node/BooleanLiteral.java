package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class BooleanLiteral {
    // since there are only two boolean values we just pre-generate their nodes and just reuse them

    public static final BooleanLiteralNode TRUE;
    public static final BooleanLiteralNode FALSE;

    static {
        TRUE = new BooleanLiteralNode(true);
        FALSE = new BooleanLiteralNode(false);
    }

    private static class BooleanLiteralNode extends LiteralNode{
        public final boolean value;

        private BooleanLiteralNode(boolean value) {
            super(BaseType.BOOL.typeLiteral);

            this.value = value;
        }

        @Override
        public void print(int indent) {
            Main.printlnWithIndent("BooleanLiteralNode { " + this.value + " }", indent);
        }
    }
}
