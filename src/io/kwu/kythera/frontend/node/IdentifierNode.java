package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;

import java.io.PrintStream;

public class IdentifierNode extends ExpressionNode {
    public final String name;

    public IdentifierNode(String name, ExpressionNode typeExp) {
        super(NodeKind.IDENTIFIER);
        this.name = name;
        this.typeExp = typeExp;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("IdentifierNode { name: " + name + " }",
            indent, stream);
    }
}
