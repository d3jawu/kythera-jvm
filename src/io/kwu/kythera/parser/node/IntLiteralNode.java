package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class IntLiteralNode extends ExpressionNode {
    public final int value;

    public IntLiteralNode(int value) {
        super(NodeKind.LITERAL, BaseType.INT.typeLiteral);

        this.value = value;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("IntLiteralNode { value: " + value + " }", indent);
    }
}
