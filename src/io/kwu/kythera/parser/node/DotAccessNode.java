package io.kwu.kythera.parser.node;

/**
 * Node for access by dot, e.g.
 * myObject.fieldName
 */
public class DotAccessNode extends ExpressionNode {
    public final ExpressionNode target;
    public final String key;

    public DotAccessNode(ExpressionNode target, String key) {
        super(NodeKind.ACCESS);

        if (target.type.baseType.scalar) {
            System.err.println("Expected struct with field " + key + ", but found " + target.type.toString());
            System.exit(1);
        }

        this.target = target;
        this.key = key;
    }
}
