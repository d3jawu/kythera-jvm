package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class DoubleLiteralNode extends ExpressionNode {
    public final double value;

    public DoubleLiteralNode(double value) {
        super(NodeKind.LITERAL, BaseType.DOUBLE.typeLiteral);
        this.value = value;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("DoubleLiteralNode { value: " + value + " }", indent);
    }
}
