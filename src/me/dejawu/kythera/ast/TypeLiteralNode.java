package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

import java.io.PrintStream;

public class TypeLiteralNode extends LiteralNode {
    public final BaseType baseType;

    public TypeLiteralNode(BaseType baseType) {
        super(baseType.typeLiteral);

        this.baseType = baseType;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("TypeLiteralNode { " + baseType.name + " }", indent, stream);
    }

    // for type values, equals means an *exact* match
    // remember, values in Kythera must be cast to exactly the type they are
    // to be used as (?)
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeLiteralNode)) {
            return false;
        }

        TypeLiteralNode node = (TypeLiteralNode) o;

        return this.baseType.name.equals(node.baseType.name);
    }
}
