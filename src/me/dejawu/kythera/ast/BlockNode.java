package me.dejawu.kythera.ast;

import me.dejawu.kythera.Main;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

public class BlockNode extends ExpressionNode {
    public final List<StatementNode> body;

    public BlockNode(List<StatementNode> body) {
        super(NodeKind.BLOCK); // type to be set later; Resolver will recreate BlockNode with return type set
        this.body = body;

        if (body.size() <= 0) {
            System.err.println("Block cannot have empty body. To return nothing, use `unit`.");
            System.exit(1);
        }
    }

    // Call when block's return type expression is known
    public BlockNode(List<StatementNode> body, ExpressionNode typeExp) {
        super(NodeKind.BLOCK, typeExp);
        this.body = body;

        if(body.size() <= 0) {
            System.err.println("Block cannot have empty body.");
            System.exit(1);
        }
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
