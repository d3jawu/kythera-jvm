package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;
import me.dejawu.kythera.BaseType;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class StructTypeLiteralNode extends TypeLiteralNode {
    public final HashMap<String, ExpressionNode> entryTypes;

    public StructTypeLiteralNode() {
        super(BaseType.STRUCT);
        this.entryTypes = new HashMap<>();
    }

    public StructTypeLiteralNode(HashMap<String, ExpressionNode> entryTypes) {
        super(BaseType.STRUCT);
        this.entryTypes = entryTypes;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("StructTypeLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tentries:", indent, stream);

        for (Map.Entry<String, ExpressionNode> entry : entryTypes.entrySet()) {
            Main.printlnWithIndent("\t\t" + entry.getKey() + ":", indent, stream);
            Main.printlnWithIndent("\t\t\ttypeExp:", indent, stream);
            entry.getValue().print(indent + 3, stream);
        }

        Main.printlnWithIndent("} StructTypeLiteralNode", indent, stream);
    }
}
