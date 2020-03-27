package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.tokenizer.Operator;

import static io.kwu.kythera.parser.tokenizer.Operator.*;

/**
 * Node for any assignment symbol
 */
public class AssignNode extends ExpressionNode {
    public final Operator op;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public AssignNode(Operator op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.ASSIGN, right.typeExp);

        // TODO ensure that left node is a valid identifier or object member

        if (op.kind != OperatorKind.ASSIGN) {
            System.err.println("Invalid operator: '" + op.symbol + "' is not a valid assignment operator.");
            System.exit(1);
        }

        this.op = op;

        this.left = left;
        this.right = right;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("AssignNode {", indent);

        Main.printlnWithIndent("\tleft:", indent);
        left.print(indent + 1);
        Main.printlnWithIndent("\tleft:", indent);
        right.print(indent + 1);

        Main.printlnWithIndent("} AssignNode", indent);
    }
}
