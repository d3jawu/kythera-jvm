package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class TypeLiteralNode extends LiteralNode {
    public final BaseType baseType;

    // since everything is a struct, an entry type list is needed for all type literals.
    public final HashMap<String, ExpressionNode> entryTypes;

    // declare built-in type literals
    public static final TypeLiteralNode TYPE;

    public static final TypeLiteralNode UNIT;

    public static final TypeLiteralNode BOOL;

    public static final TypeLiteralNode INT;
    public static final TypeLiteralNode FLOAT;
    public static final TypeLiteralNode DOUBLE;

    static {
        TYPE = new TypeLiteralNode();

        UNIT = new TypeLiteralNode(BaseType.UNIT);

        BOOL = new TypeLiteralNode(BaseType.BOOL);

        INT = new TypeLiteralNode(BaseType.INT);

        DOUBLE = new TypeLiteralNode(BaseType.DOUBLE);

        final FnTypeLiteralNode intIntToIntFnType = new FnTypeLiteralNode(new ArrayList<>() {
            {
                add(INT);
                add(INT);
            }
        }, INT);
        INT.entryTypes.put("+", intIntToIntFnType);
        INT.entryTypes.put("-", intIntToIntFnType);
        INT.entryTypes.put("*", intIntToIntFnType);
        INT.entryTypes.put("/", intIntToIntFnType);
        INT.entryTypes.put("%", intIntToIntFnType);

        FLOAT = new TypeLiteralNode(BaseType.FLOAT);
    }

    // used for creating root type only
    // the root type is a type value that has itself as its type expression.
    private TypeLiteralNode() {
        super(null);
        this.typeExp = this;

        this.baseType = BaseType.TYPE;
        this.entryTypes = new HashMap<>();
    }

    public TypeLiteralNode(BaseType baseType) {
        super(TYPE);

        this.baseType = baseType;
        this.entryTypes = new HashMap<>();
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

        return this.baseType.equals(node.baseType);
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("TypeLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tbasetype: " + this.baseType, indent, stream);
//        Main.printlnWithIndent("\tentries:", indent, stream);

        // printing can cause stack overflow when other types are printed
//        for (Map.Entry<String, ExpressionNode> entry : entryTypes.entrySet()) {
//            Main.printlnWithIndent("\t\t" + entry.getKey() + ":", indent, stream);
//            Main.printlnWithIndent("\t\t\ttypeExp:", indent, stream);
//            entry.getValue().print(indent + 3, stream);
//        }

        Main.printlnWithIndent("} TypeLiteralNode", indent, stream);
    }
}
