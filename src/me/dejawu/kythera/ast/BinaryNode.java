package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;
import me.dejawu.kythera.frontend.tokenizer.Symbol;

import java.io.PrintStream;

import static me.dejawu.kythera.frontend.tokenizer.Symbol.SymbolKind;

public final class BinaryNode extends ExpressionNode {
    public final Symbol operator;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public BinaryNode(Symbol operator, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.BINARY);

        if (!(operator.kind == SymbolKind.ARITHMETIC || operator.kind == SymbolKind.LOGICAL || operator.kind == SymbolKind.COMPARE)) {
            System.err.println("Invalid operator: " + operator.symbol + " cannot be" + " used in a binary expression.");
            System.exit(1);
        }

        this.operator = operator;
        this.left = left;
        this.right = right;

        // type-checking is done later
        this.typeExp = left.typeExp;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("BinaryNode {", indent, stream);
        Main.printlnWithIndent("\top: " + operator.symbol, indent, stream);
        Main.printlnWithIndent("\tleft:", indent, stream);
        left.print(indent + 1, stream);
        Main.printlnWithIndent("\tright:", indent, stream);
        right.print(indent + 1, stream);
        Main.printlnWithIndent("} BinaryNode", indent, stream);
    }
}
