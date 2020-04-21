package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.frontend.BaseType;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class StructTypeLiteralNode extends TypeLiteralNode {
    public final HashMap<String, ExpressionNode> entries;

    public StructTypeLiteralNode() {
        super(BaseType.STRUCT);
        this.entries = new HashMap<>();
    }

    public StructTypeLiteralNode(HashMap<String, ExpressionNode> entries) {
        super(BaseType.STRUCT);
        this.entries = entries;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("StructTypeLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tentries:", indent, stream);

        for (Map.Entry<String, ExpressionNode> entry : entries.entrySet()) {
            Main.printlnWithIndent("\t\t" + entry.getKey() + ":", indent,
                stream);
            Main.printlnWithIndent("\t\t\ttypeExp:", indent, stream);
            entry.getValue().print(indent + 3, stream);
        }

        Main.printlnWithIndent("} StructTypeLiteralNode", indent, stream);
    }
}
