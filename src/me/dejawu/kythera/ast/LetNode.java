package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class LetNode extends StatementNode {
    public final String identifier;
    public final ExpressionNode value;

    public LetNode(String identifier, ExpressionNode value) {
        super(NodeKind.LET);

        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("LetNode {", indent, stream);

        Main.printlnWithIndent("\tidentifier: " + identifier, indent, stream);
        Main.printlnWithIndent("\tvalue:", indent, stream);

        value.print(indent + 1, stream);

        Main.printlnWithIndent("} LetNode", indent, stream);
    }
}
