package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.List;

public class FnLiteralNode extends LiteralNode {
    public final List<String> parameterNames; // types for parameters are stored in associated FnTypeLiteralNode
    public final BlockNode body;

    public FnLiteralNode(FnTypeLiteralNode typeExp, List<String> parameterNames, BlockNode body) {
        super(typeExp);

        this.body = body;
        this.parameterNames = parameterNames;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("FnLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tparameters:", indent, stream);

        int n = 0;

        for (String param : parameterNames) {
            Main.printlnWithIndent("\t\tparam " + n + ": " + param, indent, stream);
            n += 1;
        }

        Main.printlnWithIndent("\tbody:", indent, stream);

        body.print(indent + 2, stream);

        Main.printlnWithIndent("} FnLiteralNode", indent, stream);
    }
}
