package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.frontend.BaseType;

import java.io.PrintStream;

public class DoubleLiteralNode extends LiteralNode {
    public final double value;

    public DoubleLiteralNode(double value) {
        super(BaseType.DOUBLE.typeLiteral);
        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("DoubleLiteralNode { " + value + " }", indent, stream);
    }
}
