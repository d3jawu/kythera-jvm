package io.kwu.kythera.parser.node;

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
    }
}
