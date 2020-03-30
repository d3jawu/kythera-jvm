package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

public class TypeLiteralNode extends LiteralNode {
    public final BaseType baseType;

    public TypeLiteralNode(BaseType baseType) {
        super(baseType.typeLiteral);

        this.baseType = baseType;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("TypeLiteralNode { " + baseType.name + " }", indent);
    }

    // for type expressions, "equals" means "is type-compatible with"
    // TODO this might not be commutative for non-scalar types
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TypeLiteralNode)) {
            return false;
        }

        TypeLiteralNode node = (TypeLiteralNode) o;

        return this.baseType.name.equals(node.baseType.name);
    }
}
