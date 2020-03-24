package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

public class LetNode extends StatementNode {
    public final String identifier;
    public final ExpressionNode value;

    public LetNode(String identifier, ExpressionNode value) {
        super(NodeKind.LET);

        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("LetNode {", indent);

        Main.printlnWithIndent("\tidentifier: " + identifier, indent);
        Main.printlnWithIndent("\tvalue:", indent);

        value.print(indent + 1);

        Main.printlnWithIndent("} LetNode", indent);
    }
}
