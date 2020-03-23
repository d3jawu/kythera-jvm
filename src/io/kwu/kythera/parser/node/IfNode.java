package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.PrimitiveNodeType;

public class IfNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;
    public final BlockNode elseBody;

    public IfNode(ExpressionNode condition, BlockNode body) {
        super(NodeKind.IF);

        if (!condition.type.equals(PrimitiveNodeType.BOOL)) {
            System.err.println("Type error: If-expression condition must evaluate to bool.");
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.type = body.type;
        this.elseBody = null;
    }

    public IfNode(ExpressionNode condition, BlockNode body, BlockNode elseBody) {
        super(NodeKind.IF);

        if (!condition.type.equals(PrimitiveNodeType.BOOL)) {
            System.err.println("Type error: If-expression condition must evaluate to bool.");
            System.exit(1);
        }

        if (!body.type.equals(elseBody.type)) {
            System.err.println("Type mismatch: 'if' block has type " + body.type.toString() + " but 'else' block has type " + elseBody.type.toString());
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.type = body.type;

        this.elseBody = elseBody;
    }
}
