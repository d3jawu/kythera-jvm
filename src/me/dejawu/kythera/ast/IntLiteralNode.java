package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

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
