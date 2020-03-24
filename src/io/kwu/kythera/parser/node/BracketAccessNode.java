package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

/**
 * Access of compound type with brackets, e.g.
 * myArray[0]
 */
public class BracketAccessNode extends ExpressionNode {
    public final ExpressionNode target;
    public final ExpressionNode key;

    public BracketAccessNode(ExpressionNode target, ExpressionNode key) {
        super(NodeKind.ACCESS);

        this.target = target;
        this.key = key;

        // TODO find out type
    }

    @Override
    public void print(int indent) {
    }
}
