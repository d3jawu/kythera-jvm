package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class WhileNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;

    public WhileNode(ExpressionNode condition, BlockNode body) {
        super(NodeKind.WHILE);

        this.condition = condition;
        this.body = body;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("WhileNode {", indent, stream);
        Main.printlnWithIndent("\t condition:", indent, stream);
        condition.print(indent + 2, stream);
        Main.printlnWithIndent("\tbody:", indent, stream);
        body.print(indent + 2, stream);
        Main.printlnWithIndent("} WhileNode", indent, stream);
    }
}
