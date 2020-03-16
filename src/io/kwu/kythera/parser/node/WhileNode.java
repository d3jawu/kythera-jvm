package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.PrimitiveNodeType;

public class WhileNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;

    public WhileNode(ExpressionNode condition, BlockNode body) {
        super(NodeKind.WHILE);
        if (!condition.type.equals(PrimitiveNodeType.BOOL)) {
            System.err.println("Type error: while-statement condition must evaluate to boolean.");
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        // while evaluates to last statement run (?)
        this.type = body.returnType;
    }
}
