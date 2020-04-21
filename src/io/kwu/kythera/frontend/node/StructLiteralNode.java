package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class StructLiteralNode extends LiteralNode {
    public final HashMap<String, ExpressionNode> entries;

    // struct type exp is built separately from struct literal
    public StructLiteralNode(StructTypeLiteralNode typeExp) {
        super(typeExp);
        this.entries = new HashMap<>();
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("StructLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tentries:", indent, stream);

        for (Map.Entry<String, ExpressionNode> entry : entries.entrySet()) {
            Main.printlnWithIndent("\t\t" + entry.getKey() + ":", indent,
                stream);
            entry.getValue().print(indent + 3, stream);
        }

        Main.printlnWithIndent("} StructLiteralNode", indent, stream);
    }
}
