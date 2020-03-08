package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.tokenizer.Operator;

public class UnaryNode extends ExpressionNode {
    public final Operator operator;
    public final ExpressionNode target;

    public UnaryNode(Operator op, ExpressionNode target)  {
        super(NodeKind.UNARY, target.type);

        if(op != Operator.BANG) {
            System.err.println("Invalid operator: " + op.symbol + " cannot be used as a unary operator.");
        }

        this.operator = op;
        this.target = target;
    }
}
