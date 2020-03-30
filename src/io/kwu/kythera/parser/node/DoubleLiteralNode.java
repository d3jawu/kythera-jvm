package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class DoubleLiteralNode extends LiteralNode {
    public final double value;

    public DoubleLiteralNode(double value) {
        super(BaseType.DOUBLE.typeLiteral);
        this.value = value;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("DoubleLiteralNode { " + value + " }", indent);
    }
}
