package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class IfNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final ExpressionNode body;
    public final ExpressionNode elseBody;

    public IfNode(ExpressionNode condition, ExpressionNode body) {
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

    public IfNode(ExpressionNode condition, ExpressionNode body, ExpressionNode elseBody) {
        super(NodeKind.IF);

        if (!condition.typeExp.equals(BaseType.BOOL.typeLiteral)) {
            System.err.println("Type error: If-expression condition must evaluate to bool.");
            System.exit(1);
        }

        // make sure both blocks have matching return types
        if (!body.typeExp.equals(elseBody.typeExp)) {
            System.out.println("Type mismatch: 'if' block has type ");
            body.typeExp.print(0);
            System.out.println(" but 'else' block has type ");
            elseBody.typeExp.print(0);
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        this.typeExp = body.typeExp;
        this.elseBody = elseBody;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("IfNode {", indent);
        Main.printlnWithIndent("\tcondition:", indent);
        condition.print(indent + 2);
        Main.printlnWithIndent("\tbody:", indent);
        body.print(indent + 2);

        if(this.elseBody != null) {
            Main.printlnWithIndent("\telse body:", indent);
            elseBody.print(indent + 2);
        }

        Main.printlnWithIndent("} IfNode", indent);
    }
}
