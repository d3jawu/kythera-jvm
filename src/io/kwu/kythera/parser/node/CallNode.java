package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;
import io.kwu.kythera.parser.BaseType;

import java.io.PrintStream;
import java.util.List;

public class CallNode extends ExpressionNode {
    public final ExpressionNode target;
    public final List<ExpressionNode> arguments;

    public CallNode(ExpressionNode target, List<ExpressionNode> arguments) {
        super(NodeKind.CALL);

        if (!(target.typeExp.equals(BaseType.FN.typeLiteral))) {
            System.err.println("Type error: Call must be performed on a function type.");
            System.exit(1);
        }

        this.target = target;
        this.arguments = arguments;
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("CallNode {", indent, stream);
        Main.printlnWithIndent("\ttarget:", indent, stream);
        target.print(indent + 2, stream);
        Main.printlnWithIndent("\targuments:", indent, stream);

        int n = 0;

        for (ExpressionNode ex : arguments) {
            Main.printlnWithIndent("\t\targ " + n + ":", indent, stream);
            ex.print(indent + 3, stream);

            n += 1;
        }

        Main.printlnWithIndent("} CallNode", indent, stream);
    }
}
