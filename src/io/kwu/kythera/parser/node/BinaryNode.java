package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.tokenizer.Symbol;

import java.io.PrintStream;

import static io.kwu.kythera.parser.tokenizer.Symbol.SymbolKind;

public final class BinaryNode extends ExpressionNode {
    public final Symbol op;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public BinaryNode(Symbol op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.BINARY);

        if (!(op.kind == SymbolKind.ARITHMETIC || op.kind == SymbolKind.LOGICAL || op.kind == SymbolKind.COMPARE)) {
            System.err.println("Invalid operator: " + op.symbol + " cannot be used in a binary expression.");
            System.exit(1);
        }

        this.op = op;
        this.left = left;
        this.right = right;

        // type comparison is simply (typeof LHS).equals(typeof RHS)
        // where typeof is Kythera's typeof implementation, not Java's
        if (!left.typeExp.equals(right.typeExp)) {
            System.err.println("LHS and RHS types do not match.");
            System.err.println("left:");
            left.typeExp.print(0, System.err);
            System.err.println("right:");
            right.typeExp.print(0, System.err);
            System.exit(1);
        }

        this.typeExp = left.typeExp;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("BinaryNode {", indent, stream);
        Main.printlnWithIndent("\top: " + op.symbol, indent, stream);
        Main.printlnWithIndent("\tleft:", indent, stream);
        left.print(indent + 1, stream);
        Main.printlnWithIndent("\tright:", indent, stream);
        right.print(indent + 1, stream);
        Main.printlnWithIndent("} BinaryNode", indent, stream);
    }
}
