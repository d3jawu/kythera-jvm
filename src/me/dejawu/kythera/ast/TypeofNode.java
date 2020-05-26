package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class TypeofNode extends ExpressionNode {
    public final ExpressionNode target;

    public TypeofNode(ExpressionNode target) {
        super(NodeKind.TYPEOF);
        this.target = target;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("TypeofNode {", indent, stream);
        Main.printlnWithIndent("\ttarget:", indent, stream);
        target.print(indent + 1, stream);
        Main.printlnWithIndent("} TypeofNode", indent, stream);
    }
}
