package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

public class BooleanLiteral {
    // since there are only two boolean values we just pre-generate their nodes and just reuse them

    public static final BooleanLiteralNode TRUE;
    public static final BooleanLiteralNode FALSE;

    static {
        TRUE = new BooleanLiteralNode(true);
        FALSE = new BooleanLiteralNode(false);
    }

    private static class BooleanLiteralNode extends ExpressionNode {
        public final boolean value;

        private BooleanLiteralNode(boolean value) {
            super(NodeKind.LITERAL, NodeType.BOOL);

            this.value = value;
        }
    }
}
