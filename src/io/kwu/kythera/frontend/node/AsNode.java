package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;

import java.io.PrintStream;

public class AsNode extends ExpressionNode {
    public final ExpressionNode from;
    public final ExpressionNode to;

    public AsNode(ExpressionNode from, ExpressionNode to) {
        super(NodeKind.AS, to);

        this.from = from;
        this.to = to;
    }

    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("AsNode {", indent, stream);

        Main.printlnWithIndent("\tFrom:", indent, stream);
        from.print(indent + 1, stream);
        Main.printlnWithIndent("\tTo:", indent, stream);
        to.print(indent + 1, stream);

        Main.printlnWithIndent("} AsNode", indent, stream);
    }
}
