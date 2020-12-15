package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class ConstNode extends StatementNode {
    public final String identifier;
    public final ExpressionNode value;

    public ConstNode(String identifier, ExpressionNode value) {
        super(NodeKind.CONST);

        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("ConstNode {", indent, stream);

        Main.printlnWithIndent("\tidentifier: " + identifier, indent, stream);
        Main.printlnWithIndent("\tvalue:", indent, stream);

        value.print(indent + 1, stream);

        Main.printlnWithIndent("} ConstNode", indent, stream);
    }
}
