package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

public class AsNode extends ExpressionNode {
    public final ExpressionNode from;
    public final ExpressionNode to;

    public AsNode(ExpressionNode from, ExpressionNode to) {
       super(NodeKind.AS, to);

       this.from = from;
       this.to = to;
    }

    public void print(int indent) {
        Main.printlnWithIndent("AsNode {", indent);

        Main.printlnWithIndent("\tFrom:", indent);
        from.print(indent + 1);
        Main.printlnWithIndent("\tTo:", indent);
        to.print(indent + 1);

        Main.printlnWithIndent("} AsNode", indent);
    }
}
