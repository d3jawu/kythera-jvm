package me.dejawu.kythera.ast;

public abstract class LiteralNode extends ExpressionNode {
    // we should always be able to determine the type for a literal
    // and provide the equivalent TypeLiteralNode at parse-time
    public LiteralNode(TypeLiteralNode typeExp) {
        super(NodeKind.LITERAL, typeExp);
    }
}
