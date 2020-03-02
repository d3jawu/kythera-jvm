package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;
import io.kwu.kythera.parser.ParserException;

public class BooleanLiteralNode extends ExpressionNode {
    public static final ScalarLiteralNode<Boolean> TRUE;
    public static final ScalarLiteralNode<Boolean> FALSE;

    BooleanLiteralNode(NodeKind kind, NodeType type) {
        super(kind, type);
    }

    static {
        ScalarLiteralNode<Boolean> t;
        ScalarLiteralNode<Boolean> f;

        try {
            t = new ScalarLiteralNode<>(NodeType.BOOL, true);
            f = new ScalarLiteralNode<>(NodeType.BOOL, false);
        } catch(ParserException e) {
            System.err.println("Internal error: Could not initialize static boolean primitive:");
            System.err.println(e);
            t = null;
            f = null;
        }

        TRUE = t;
        FALSE = f;
    }
}
