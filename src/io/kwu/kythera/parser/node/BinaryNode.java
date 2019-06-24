package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.Operator;
import static io.kwu.kythera.parser.Operator.*;
import io.kwu.kythera.parser.ParserException;

import java.util.Arrays;

public final class BinaryNode extends ExpressionNode {
    public final Operator op;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public BinaryNode(Operator op, ExpressionNode left, ExpressionNode right) throws ParserException {
        super(NodeKind.BINARY, null);

        if (!Arrays.asList(new Operator[] {
                EQUIVALENT,
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
            throw new ParserException("Invalid operator: " + op.symbol + " cannot be used in a binary expression.");
        }

        this.op = op;
        this.left = left;
        this.right = right;

        // TODO type check left and right
        this.type = left.type;
    }
}
