package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ListLiteralNode extends LiteralNode {
    public final List<ExpressionNode> entries;

    public ListLiteralNode(ListTypeLiteralNode typeExp) {
        super(typeExp);
        this.entries = new ArrayList<>();
    }

    public ListLiteralNode(ListTypeLiteralNode typeExp, List<ExpressionNode> entries) {
        super(typeExp);
        this.entries = entries;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("ListLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tentries:", indent, stream);

        int i = 0;
        for (ExpressionNode exp : entries) {
            Main.printlnWithIndent("\t\t" + i + ":", indent, stream);
            exp.print(indent + 3, stream);
            i += 1;
        }

        Main.printlnWithIndent("} ListLiteralNode", indent, stream);
    }
}
