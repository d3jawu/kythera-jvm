package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class WhileNode extends ExpressionNode {
    public final ExpressionNode condition;
    public final BlockNode body;

    public WhileNode(ExpressionNode condition, BlockNode body) {
        super(NodeKind.WHILE);
        if (!condition.typeExp.equals(BaseType.BOOL.typeLiteral)) {
            System.err.println("Type error: while-statement condition must evaluate to boolean.");
            System.exit(1);
        }

        this.condition = condition;
        this.body = body;

        // while evaluates to block value
        this.typeExp = body.typeExp;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("WhileNode {", indent);
        Main.printlnWithIndent("\t condition:", indent);
        condition.print(indent + 2);
        Main.printlnWithIndent("\tbody:", indent);
        body.print(indent + 2);
        Main.printlnWithIndent("} WhileNode", indent);
    }
}
