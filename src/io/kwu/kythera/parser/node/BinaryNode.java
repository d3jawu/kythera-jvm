package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.tokenizer.Operator;
import static io.kwu.kythera.parser.tokenizer.Operator.*;

import java.util.Arrays;

public final class BinaryNode extends ExpressionNode {
    public final Operator op;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public BinaryNode(Operator op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.BINARY, null);

        if (!Arrays.asList(new Operator[] {
            EQUIV,
                NOT_EQUIV,
                LESS_EQUIV,
                GREATER_EQUIV,
                GREATER_THAN,
                LESS_THAN,
                PLUS,
                MINUS,
                TIMES,
                DIVIDE,
                MODULUS,
                OR_LOGICAL,
                AND_LOGICAL,
        }).contains(op)) {
            System.err.println("Invalid operator: " + op.symbol + " cannot be used in a binary expression.");
            System.exit(1);
        }

        this.op = op;
        this.left = left;
        this.right = right;

        // TODO type check left and right
        this.type = left.type;
    }
}
