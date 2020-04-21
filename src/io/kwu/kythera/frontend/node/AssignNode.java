package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.frontend.tokenizer.Symbol;

import java.io.PrintStream;

import static io.kwu.kythera.frontend.tokenizer.Symbol.SymbolKind;

/**
 * Node for any assignment symbol
 */
public class AssignNode extends ExpressionNode {
    public final Symbol op;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public AssignNode(Symbol op, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.ASSIGN, right.typeExp);

        // TODO ensure that left node is a valid identifier or object member

        if (op.kind != SymbolKind.ASSIGN) {
            System.err.println("Invalid operator: '" + op.symbol + "' is not " +
                "a valid assignment operator.");
            System.exit(1);
        }

        this.op = op;

        this.left = left;
        this.right = right;

        if (!left.typeExp.equals(right.typeExp)) {
            System.err.println("Left and right types do not match.");
            System.exit(1);
        }
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("AssignNode {", indent, stream);

        Main.printlnWithIndent("\tleft:", indent, stream);
        left.print(indent + 2, stream);
        Main.printlnWithIndent("\tright:", indent, stream);
        right.print(indent + 2, stream);

        Main.printlnWithIndent("} AssignNode", indent, stream);
    }
}
