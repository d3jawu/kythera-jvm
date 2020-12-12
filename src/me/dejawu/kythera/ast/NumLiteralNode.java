package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class NumLiteralNode extends LiteralNode {
    public final double value;

    public NumLiteralNode(double value) {
        super(TypeLiteralNode.NUM);
        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("NumLiteralNode { " + value + " }", indent, stream);
    }
}
