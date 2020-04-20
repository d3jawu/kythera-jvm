package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.frontend.BaseType;

import java.io.PrintStream;

public class IntLiteralNode extends LiteralNode {
    public final int value;

    public IntLiteralNode(int value) {
        super(BaseType.INT.typeLiteral);

        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("IntLiteralNode { " + value + " }", indent, stream);
    }
}
