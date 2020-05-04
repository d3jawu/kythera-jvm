package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

public class FnLiteralNode extends LiteralNode {
    public final SortedMap<String, ExpressionNode> parameters;
    public final BlockNode body;

    public FnLiteralNode(SortedMap<String, ExpressionNode> parameters, BlockNode body) {
        super(new FnTypeLiteralNode(new ArrayList<>(parameters.values()), body.typeExp));

        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("FnLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tparameters:", indent, stream);

        int n = 0;

        for (Map.Entry<String, ExpressionNode> entry : parameters.entrySet()) {
            Main.printlnWithIndent("\t\tparam " + n + ": " + entry.getKey(), indent, stream);
            Main.printlnWithIndent("\t\ttype exp:", indent, stream);
            entry.getValue().print(indent + 3, stream);

            n += 1;
        }

        Main.printlnWithIndent("\tbody:", indent, stream);

        body.print(indent + 2, stream);

        Main.printlnWithIndent("} FnLiteralNode", indent, stream);
    }
}
