package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.NodeType;
import io.kwu.kythera.parser.ParserException;

public class WhileNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;

    public WhileNode(ExpressionNode condition, BlockNode body) throws ParserException {
        super(NodeKind.WHILE);
        if(!condition.type.equals(NodeType.BOOL)) {
            throw new ParserException("Type error: while-statement condition must evaluate to ");
        }

        this.condition = condition;
        this.body = body;

        this.type = body.body[body.body.length - 1].type;

    }
}
