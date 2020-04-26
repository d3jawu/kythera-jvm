package me.dejawu.kythera.frontend.node;

/**
 * ExpressionNodes are statements that evaluate to a value and therefore also
 * have a type.
 */
public abstract class ExpressionNode extends StatementNode {
    public ExpressionNode typeExp; // expression that actually gave this
    // value its type

    ExpressionNode(NodeKind kind) {
        super(kind);
    }

    ExpressionNode(NodeKind kind, ExpressionNode typeExp) {
        super(kind);
        this.typeExp = typeExp;
    }
}
