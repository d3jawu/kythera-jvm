package io.kwu.kythera.frontend.node;

import java.io.PrintStream;

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
    public void print(int indent, PrintStream stream) {
    }
}
