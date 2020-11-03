package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class StructLiteralNode extends LiteralNode {
    public final HashMap<String, ExpressionNode> entries;

    // struct type exp is built separately from struct literal
    public StructLiteralNode(TypeLiteralNode typeExp) {
        super(typeExp);
        this.entries = new HashMap<>();
    }

    public StructLiteralNode(TypeLiteralNode typeExp, HashMap<String, ExpressionNode> entries) {
        super(typeExp);
        this.entries = entries;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("StructLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tentries:", indent, stream);

        for (Map.Entry<String, ExpressionNode> entry : entries.entrySet()) {
            Main.printlnWithIndent("\t\t" + entry.getKey() + ":", indent, stream);
            entry.getValue().print(indent + 3, stream);
        }

        Main.printlnWithIndent("} StructLiteralNode", indent, stream);
    }
}
