package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.List;

public class CallNode extends ExpressionNode {
    public final ExpressionNode target;
    public final List<ExpressionNode> arguments;

    public CallNode(ExpressionNode target, List<ExpressionNode> arguments) {
        super(NodeKind.CALL);

        this.target = target;
        this.arguments = arguments;
    }

    // called by Resolver when return type is known
    public CallNode(ExpressionNode target, List<ExpressionNode> arguments, ExpressionNode typeExp) {
        super(NodeKind.CALL, typeExp);

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
