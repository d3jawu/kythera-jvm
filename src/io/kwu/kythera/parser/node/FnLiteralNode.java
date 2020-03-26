package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

public class FnLiteralNode extends LiteralNode {
    public final SortedMap<String, ExpressionNode> parameters;
    public final BlockNode body;
    public final ExpressionNode returnType;

    public FnLiteralNode(SortedMap<String, ExpressionNode> parameters, BlockNode body, ExpressionNode returnType) {
        super(new FnTypeLiteralNode(new ArrayList<>(parameters.values()), returnType));

        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("FnLiteralNode {", indent);
        Main.printlnWithIndent("\tParameters:", indent);

        for(Map.Entry<String, ExpressionNode> entry : parameters.entrySet()) {
            Main.printlnWithIndent("\tName:", indent);
            Main.printlnWithIndent(entry.getKey(), indent + 1);
            Main.printlnWithIndent("\tType expression:", indent);
            entry.getValue().print(indent + 1);
        }

        Main.printlnWithIndent("\tBody:", indent);

        body.print(indent + 1);

        Main.printlnWithIndent("\tReturn type:", indent);

        returnType.print(indent + 1);
        Main.printlnWithIndent("} FnLiteralNode", indent);
    }
}
