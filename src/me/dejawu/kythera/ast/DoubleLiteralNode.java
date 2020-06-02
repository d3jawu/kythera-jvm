package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class DoubleLiteralNode extends LiteralNode {
    public final double value;

    public DoubleLiteralNode(double value) {
        super(BaseType.DOUBLE.typeLiteral);
        System.err.println("Double values are temporarily disabled.");
        System.exit(0);
        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("DoubleLiteralNode { " + value + " }", indent, stream);
    }
}
