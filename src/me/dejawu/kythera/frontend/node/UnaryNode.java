package me.dejawu.kythera.frontend.node;

import me.dejawu.kythera.Main;
import me.dejawu.kythera.frontend.tokenizer.Symbol;

import java.io.PrintStream;

public class UnaryNode extends ExpressionNode {
    public final Symbol operator;
    public final ExpressionNode target;

    public UnaryNode(Symbol op, ExpressionNode target) {
        // TODO look up op in symbol table for target
        super(NodeKind.UNARY, target.typeExp);

        if (op != Symbol.BANG) {
            System.err.println("Invalid operator: " + op.symbol + " cannot be" + " used as a unary operator.");
        }

        this.operator = op;
        this.target = target;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("UnaryNode {", indent, stream);
        Main.printlnWithIndent("\top: " + operator.symbol, indent, stream);
        Main.printlnWithIndent("\ttarget", indent, stream);
        target.print(indent + 1, stream);
        Main.printlnWithIndent("} UnaryNode", indent, stream);
    }
}
