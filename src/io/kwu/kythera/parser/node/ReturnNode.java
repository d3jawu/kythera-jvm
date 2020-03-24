package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

public class ReturnNode extends StatementNode {
    public final ExpressionNode exp;

    public ReturnNode(ExpressionNode exp) {
        super(NodeKind.RETURN);
        this.exp = exp;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("ReturnNode {", indent);
        Main.printlnWithIndent("\texp:", indent);
        exp.print(indent + 1);
        Main.printlnWithIndent("} ReturnNode", indent);
    }
}
