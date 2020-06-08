package me.dejawu.kythera.ast;

import me.dejawu.kythera.BaseType;
import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.List;

public class FnTypeLiteralNode extends TypeLiteralNode {
    public final List<ExpressionNode> parameterTypeExps;
    public final ExpressionNode returnTypeExp;

    public FnTypeLiteralNode(List<ExpressionNode> parameterTypeExps, ExpressionNode returnTypeExp) {
        super(BaseType.FN);

        this.parameterTypeExps = parameterTypeExps;
        this.returnTypeExp = returnTypeExp;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("FnTypeLiteralNode {", indent, stream);
        Main.printlnWithIndent("\tparams:", indent, stream);

        int n = 0;

        for (ExpressionNode exp : parameterTypeExps) {
            Main.printlnWithIndent("\t\t" + n + ":", indent, stream);
            exp.print(indent + 1, stream);
        }

        Main.printlnWithIndent("\treturn type:", indent, stream);
        Main.printlnWithIndent("" + returnTypeExp, indent, stream);
        returnTypeExp.print(indent + 1, stream);

        Main.printlnWithIndent("} FnTypeLiteralNode", indent, stream);
    }
}
