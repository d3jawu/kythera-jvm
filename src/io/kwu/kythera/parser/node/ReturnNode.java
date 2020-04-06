package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

import java.io.PrintStream;

public class ReturnNode extends StatementNode {
    public final ExpressionNode exp;

    public ReturnNode(ExpressionNode exp) {
        super(NodeKind.RETURN);
        this.exp = exp;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("ReturnNode {", indent, stream);
        Main.printlnWithIndent("\texp:", indent, stream);
        exp.print(indent + 1, stream);
        Main.printlnWithIndent("} ReturnNode", indent, stream);
    }
}
