package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

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
            System.err.println("Expected struct with field " + key + ", but found " + target.typeConstraint.toString());
            System.exit(1);
        }*/

        this.target = target;
        this.key = key;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("DotAccessNode {", indent);
        Main.printlnWithIndent("\ttarget:", indent);
        target.print(indent + 1);
        Main.printlnWithIndent("\tkey: " + key, indent);
        Main.printlnWithIndent("} DotAccessNode", indent);
    }
}
