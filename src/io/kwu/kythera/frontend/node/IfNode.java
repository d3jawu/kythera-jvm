package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.frontend.BaseType;

import java.io.PrintStream;

public class IfNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;
    public final ExpressionNode elseBody;

    public IfNode(ExpressionNode condition, BlockNode body) {
        super(NodeKind.IF);

        if (!condition.typeExp.equals(BaseType.BOOL.typeLiteral)) {
            System.err.println("Type error: If-expression condition must evaluate to bool.");
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.typeExp = body.typeExp;
        this.elseBody = null;
    }

    public IfNode(ExpressionNode condition, BlockNode body, ExpressionNode elseBody) {
        super(NodeKind.IF);

        // else body can be either an if or a block
        if (!(elseBody instanceof IfNode) && !(elseBody instanceof BlockNode)) {
            System.err.println("'else' must be followed by either a block or an if statement.");
            System.exit(1);
        }

        if (!condition.typeExp.equals(BaseType.BOOL.typeLiteral)) {
            System.err.println("Type error: If-expression condition must evaluate to bool.");
            System.exit(1);
        }

        // make sure both blocks have matching return types
        if (!body.typeExp.equals(elseBody.typeExp)) {
            System.err.println("Type mismatch: 'if' block has type ");
            body.typeExp.print(0, System.err);
            System.err.println(" but 'else' block has type ");
            elseBody.typeExp.print(0, System.err);
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.typeExp = body.typeExp;
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
