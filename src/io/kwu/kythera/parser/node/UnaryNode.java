package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.tokenizer.Operator;

public class UnaryNode extends ExpressionNode {
    public final Operator operator;
    public final ExpressionNode target;

    public UnaryNode(Operator op, ExpressionNode target) {
        // TODO look up op in symbol table for target
        super(NodeKind.UNARY, target.typeExp);

        if (op != Operator.BANG) {
            System.err.println("Invalid operator: " + op.symbol + " cannot be used as a unary operator.");
        }

        this.operator = op;
        this.target = target;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("UnaryNode {", indent);
        Main.printlnWithIndent("\top: " + operator.symbol, indent);
        Main.printlnWithIndent("\ttarget", indent);
        target.print(indent + 1);
        Main.printlnWithIndent("} UnaryNode", indent);
    }
}
