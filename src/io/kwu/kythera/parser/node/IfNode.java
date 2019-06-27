package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.NodeType;
import io.kwu.kythera.parser.ParserException;

public class IfNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;
    public final BlockNode elseBody;

    public IfNode(ExpressionNode condition, BlockNode body) throws ParserException {
        super(NodeKind.IF);

        if(!condition.type.equals(NodeType.BOOL)) {
            throw new ParserException("Type error: If-expression condition must evaluate to bool.");
        }

        this.condition = condition;
        this.body = body;

        this.type = body.type;
        this.elseBody = null;
    }

    public IfNode(ExpressionNode condition, BlockNode body, BlockNode elseBody) throws ParserException{
        super(NodeKind.IF);

        if(!condition.type.equals(NodeType.BOOL)) {
            throw new ParserException("Type error: If-expression condition must evaluate to bool.");
        }

        if(!body.type.equals(elseBody.type)) {
            throw new ParserException("Type mismatch: 'if' block has type " + body.type.toString() + " but 'else' block has type " + elseBody.type.toString());
        }

        this.condition = condition;
        this.body = body;

        this.type = body.type;

        this.elseBody = elseBody;
    }
}
