package me.dejawu.kythera.frontend.node;

import me.dejawu.kythera.Main;

import java.io.PrintStream;

/**
 * Node for access by dot, e.g.
 * myObject.fieldName
 */
public class DotAccessNode extends ExpressionNode {
    public final ExpressionNode target;
    public final String key;

    public DotAccessNode(ExpressionNode target, String key) {
        super(NodeKind.ACCESS);

        this.target = target;
        this.key = key;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("DotAccessNode {", indent, stream);
        Main.printlnWithIndent("\ttarget:", indent, stream);
        target.print(indent + 1, stream);
        Main.printlnWithIndent("\tkey: " + key, indent, stream);
        Main.printlnWithIndent("} DotAccessNode", indent, stream);
    }
}
