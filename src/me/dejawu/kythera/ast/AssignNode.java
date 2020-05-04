package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;
import me.dejawu.kythera.frontend.tokenizer.Symbol;

import java.io.PrintStream;

import static me.dejawu.kythera.frontend.tokenizer.Symbol.SymbolKind;

/**
 * Node for any assignment symbol
 */
public class AssignNode extends ExpressionNode {
    public final Symbol operator;
    public final ExpressionNode left;
    public final ExpressionNode right;

    public AssignNode(Symbol operator, ExpressionNode left, ExpressionNode right) {
        super(NodeKind.ASSIGN, right.typeExp);

        if (operator.kind != SymbolKind.ASSIGN) {
            System.err.println("Invalid operator: '" + operator.symbol + "' is not " + "a valid assignment operator.");
            System.exit(1);
        }

        this.operator = operator;

        this.left = left;
        this.right = right;
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
