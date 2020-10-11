package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class IfNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;
    public final ExpressionNode elseBody;

    public IfNode(ExpressionNode condition, BlockNode body) {
        super(NodeKind.IF);

        this.condition = condition;
        this.body = body;

        this.elseBody = null;
    }

    public IfNode(ExpressionNode condition, BlockNode body, ExpressionNode elseBody) {
        super(NodeKind.IF);

        // else body can be either an if or a block
        if (!(elseBody instanceof IfNode) && !(elseBody instanceof BlockNode)) {
            System.err.println("'else' must be followed by either a block or " + "an if statement.");
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.elseBody = elseBody;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("IfNode {", indent, stream);
        Main.printlnWithIndent("\tcondition:", indent, stream);
        condition.print(indent + 2, stream);
        Main.printlnWithIndent("\tbody:", indent, stream);
        body.print(indent + 2, stream);

        if (this.elseBody != null) {
            Main.printlnWithIndent("\telse body:", indent, stream);
            elseBody.print(indent + 2, stream);
        }

        Main.printlnWithIndent("} IfNode", indent, stream);
    }
}
