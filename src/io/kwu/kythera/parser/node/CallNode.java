package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.FnNodeType;

public class CallNode extends ExpressionNode {
    public final ExpressionNode target;
    public final ExpressionNode[] arguments;

    public CallNode(ExpressionNode target, ExpressionNode[] arguments) {
        super(NodeKind.CALL);

        if(target.type instanceof FnNodeType) {
            System.err.println("Type error: Call must be performed on a function type.");
            System.exit(1);
        }

        this.target = target;
        this.arguments = arguments;
    }
}
