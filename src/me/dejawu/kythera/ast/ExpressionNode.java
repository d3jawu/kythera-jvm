package me.dejawu.kythera.ast;

/**
 * ExpressionNodes are statements that evaluate to a value and therefore also
 * have a type.
 */
public abstract class ExpressionNode extends StatementNode {
    // holds expression that gave this value its type
    public final ExpressionNode typeExp;

    ExpressionNode(NodeKind kind) {
        super(kind);
        this.typeExp = null;
    }

    ExpressionNode(NodeKind kind, ExpressionNode typeExp) {
        super(kind);
        this.typeExp = typeExp;
    }
}
