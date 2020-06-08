package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

public class BlockNode extends ExpressionNode {
    public final List<StatementNode> body;

    public BlockNode(List<StatementNode> body) {
        super(NodeKind.BLOCK); // type to be set later
        this.body = body;

        if (body.size() <= 0) {
            System.err.println("Block cannot have empty body. To return " + "nothing, use `unit`.");
            System.exit(1);
        }

        List<StatementNode> returnStatements = body.stream().filter(node -> node.kind == NodeKind.RETURN).collect(Collectors.toList());

        StatementNode lastNode = body.get(body.size() - 1);

        if (lastNode.kind != NodeKind.RETURN && !(lastNode instanceof ExpressionNode)) {
            System.err.println("Last statement in block must be a return or " + "expression.");
            System.exit(1);
        }

        // Resolver will recreate BlockNode with return type set
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("BlockNode {", indent, stream);
        Main.printlnWithIndent("\tbody:", indent, stream);

        for (StatementNode st : this.body) {
            st.print(indent + 2, stream);
        }

        Main.printlnWithIndent("} BlockNode", indent, stream);
    }
}
