package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.Operator;
import io.kwu.kythera.parser.ParserException;

import static io.kwu.kythera.parser.Operator.*;

import java.util.Arrays;

/**
 * Node for any assignment symbol
 */
public class AssignNode extends ExpressionNode {
    public final ExpressionNode left;
    public final ExpressionNode right;

    public AssignNode(Operator op, ExpressionNode left, ExpressionNode right) throws ParserException {
        super(NodeKind.ASSIGN, right.type);

        if (!Arrays.asList(new Operator[]{
                EQUALS,
                PLUS_EQUALS,
                MINUS_EQUALS,
                TIMES_EQUALS,
                DIV_EQUALS,
                MOD_EQUALS,
        }).contains(op)) {
            throw new ParserException("Invalid operator: " + op.symbol + " is not a valid assignment operator.");
        }

        this.left = left;
        this.right = right;
    }
}
