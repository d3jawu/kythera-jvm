package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class FloatLiteralNode extends LiteralNode {
    public final float value;

    public FloatLiteralNode(float value) {
        super(TypeLiteralNode.FLOAT);
        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("FloatLiteralNode { " + value + " }", indent, stream);
    }
}
