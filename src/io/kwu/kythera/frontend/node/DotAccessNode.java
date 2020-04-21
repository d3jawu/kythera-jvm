package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;

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

        // TODO read from symbol table
        /*if (target) {
            System.err.println("Expected struct with field " + key + ", but
            found " + target.typeConstraint.toString());
            System.exit(1);
        }*/

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
