package io.kwu.kythera.parser.node;

public class IdentifierNode extends ExpressionNode {
    public final String name;

    public IdentifierNode(String name) {
        // TODO symbol table lookup
        super(NodeKind.IDENTIFIER);
        this.name = name;
    }

    public IdentifierNode(String name, ExpressionNode typeExp) {
        super(NodeKind.IDENTIFIER);
        this.name = name;
        this.typeExp = typeExp;
    }

    @Override
    public void print(int indent) {

    }
}
