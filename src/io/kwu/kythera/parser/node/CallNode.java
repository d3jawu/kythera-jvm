package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.BaseType;
import io.kwu.kythera.parser.ParserException;

public class CallNode extends ExpressionNode {
    public final ExpressionNode target;
    public final ExpressionNode[] arguments;

    public CallNode(ExpressionNode target, ExpressionNode[] arguments) throws ParserException {
        super(NodeKind.CALL);

        if(target.type.baseType != BaseType.FN) {
            throw new ParserException("Type error: Call must be performed on a function type.");
        }

        this.target = target;
        this.arguments = arguments;
    }
}
