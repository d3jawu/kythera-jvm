package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.tokenizer.Operator;

import static io.kwu.kythera.parser.tokenizer.Operator.*;

import java.util.Arrays;

/**
 * Node for any assignment symbol
 */
public class AssignNode extends ExpressionNode {
    public final ExpressionNode left;
    public final ExpressionNode right;

    public AssignNode(Operator op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.ASSIGN, right.type);

        // TODO ensure that left node is an identifier or object member

        if (!Arrays.asList(new Operator[]{
                EQUALS,
                PLUS_EQUALS,
                MINUS_EQUALS,
                TIMES_EQUALS,
                DIV_EQUALS,
                MOD_EQUALS,
        }).contains(op)) {
            System.err.println("Invalid operator: '" + op.symbol + "' is not a valid assignment operator.");
            System.exit(1);
        }

        this.left = left;
        this.right = right;
    }
}
