package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class IdentifierNode extends ExpressionNode {
    public final String name;

    public IdentifierNode(String name) {
        super(NodeKind.IDENTIFIER);
        this.name = name;

    }

    // called by Resolver when typeExp is known
    public IdentifierNode(String name, ExpressionNode typeExp) {
        super(NodeKind.IDENTIFIER, typeExp);
        this.name = name;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("IdentifierNode { name: " + name + " }", indent, stream);
    }
}
