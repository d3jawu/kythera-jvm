package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

public class IdentifierNode extends ExpressionNode {
    public final String name;

    public IdentifierNode(String name, ExpressionNode typeExp) {
        super(NodeKind.IDENTIFIER);
        this.name = name;
        this.typeExp = typeExp;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("IdentifierNode { name: " + name + " }", indent);
    }
}
