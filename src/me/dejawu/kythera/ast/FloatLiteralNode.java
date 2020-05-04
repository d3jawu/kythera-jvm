package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;
import me.dejawu.kythera.frontend.BaseType;

import java.io.PrintStream;

public class FloatLiteralNode extends LiteralNode {
    public final float value;

    public FloatLiteralNode(float value) {
        super(BaseType.FLOAT.typeLiteral);
        this.value = value;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("FloatLiteralNode { " + value + " }", indent, stream);
    }
}
