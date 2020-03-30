package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

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
    public void print(int indent) {
        Main.printlnWithIndent("FnLiteralNode {", indent);
        Main.printlnWithIndent("\tparameters:", indent);

        int n = 0;

        for(Map.Entry<String, ExpressionNode> entry : parameters.entrySet()) {
            Main.printlnWithIndent("\t\tparam " + n + ": " + entry.getKey(), indent);
            Main.printlnWithIndent("\t\ttype exp:", indent);
            entry.getValue().print(indent + 3);

            n += 1;
        }

        Main.printlnWithIndent("\tbody:", indent);

        body.print(indent + 2);

        Main.printlnWithIndent("} FnLiteralNode", indent);
    }
}
