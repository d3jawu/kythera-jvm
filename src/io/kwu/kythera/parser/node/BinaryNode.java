package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.tokenizer.Operator;

import static io.kwu.kythera.parser.tokenizer.Operator.*;

public final class BinaryNode extends ExpressionNode {
    public final Operator op;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public BinaryNode(Operator op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.BINARY);

        if (!(op.kind == OperatorKind.ARITHMETIC || op.kind == OperatorKind.LOGICAL || op.kind == OperatorKind.COMPARE)) {
            System.err.println("Invalid operator: " + op.symbol + " cannot be used in a binary expression.");
            System.exit(1);
        }

        this.op = op;
        this.left = left;
        this.right = right;

        // type comparison is simply (typeof LHS).equals(typeof RHS)
        // where typeof is Kythera's typeof implementation, not Java's
        if(! left.typeExp.equals(right.typeExp)) {
            System.err.println("LHS and RHS types do not match.");
            System.err.println("left:");
            left.typeExp.print(0);
            System.err.println("right:");
            right.typeExp.print(0);
            System.exit(1);
        }

        this.typeExp = left.typeExp;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("BinaryNode {", indent);
        Main.printlnWithIndent("\top: " + op.symbol, indent);
        Main.printlnWithIndent("\tleft:", indent);
        left.print(indent + 1);
        Main.printlnWithIndent("\tright:", indent);
        right.print(indent + 1);
        Main.printlnWithIndent("} BinaryNode", indent);
    }
}
