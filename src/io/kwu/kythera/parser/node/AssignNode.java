package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.tokenizer.Operator;

import java.util.Arrays;

import static io.kwu.kythera.parser.tokenizer.Operator.*;

/**
 * Node for any assignment symbol
 */
public class AssignNode extends ExpressionNode {
    public final ExpressionNode left;
    public final ExpressionNode right;

    public AssignNode(Operator op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.ASSIGN, right.typeExp);

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

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("AssignNode {", indent);

        Main.printlnWithIndent("\tLHS:", indent);
        left.print(indent + 1);
        Main.printlnWithIndent("\tRHS:", indent);
        right.print(indent + 1);

        Main.printlnWithIndent("} AssignNode", indent);
    }
}
