package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.BaseType;

public class IfNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;
    public final BlockNode elseBody;

    public IfNode(ExpressionNode condition, BlockNode body) {
        super(NodeKind.IF);

        if (!condition.typeExp.equals(BaseType.BOOL.typeLiteral)) {
            System.err.println("Type error: If-expression condition must evaluate to bool.");
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.typeExp = body.typeExp;
        this.elseBody = null;
    }

    public IfNode(ExpressionNode condition, BlockNode body, BlockNode elseBody) {
        super(NodeKind.IF);

        if (!condition.typeExp.equals(BaseType.BOOL.typeLiteral)) {
            System.err.println("Type error: If-expression condition must evaluate to bool.");
            System.exit(1);
        }

        if (!body.typeExp.equals(elseBody.typeExp)) {
            System.err.println("Type mismatch: 'if' block has type " + body.typeExp.toString() + " but 'else' block has type " + elseBody.typeExp.toString());
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.typeExp = body.typeExp;

        this.elseBody = elseBody;
    }

    @Override
    public void print(int indent) {

    }
}
