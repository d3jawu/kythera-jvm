package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.Operator;

public class BinaryNode extends ExpressionNode {
    public final Operator op;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public BinaryNode(Operator op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.BINARY, null);

        this.op = op;
        this.left = left;
        this.right = right;

        // TODO type check left and right?
        this.type = left.type;
    }
}
