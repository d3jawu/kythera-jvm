package io.kwu.kythera.frontend.node;

import io.kwu.kythera.Main;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

public class BlockNode extends ExpressionNode {
    public final List<StatementNode> body;

    public BlockNode(List<StatementNode> body) {
        super(NodeKind.BLOCK); // type to be set later
        this.body = body;

        if (body.size() <= 0) {
            System.err.println("Block cannot have empty body. To return " +
                "nothing, use `unit`.");
            System.exit(1);
        }

        List<StatementNode> returnStatements = body
            .stream()
            .filter(
                node -> node.kind == NodeKind.RETURN
            )
            .collect(Collectors.toList());

        StatementNode lastNode = body.get(body.size() - 1);

        if (lastNode.kind != NodeKind.RETURN && !(lastNode instanceof ExpressionNode)) {
            System.err.println("Last statement in block must be a return or " +
                "expression.");
            System.exit(1);
        }

        // check all return statements for type equivalence
        typeExp = null; // for BlockNodes, typeExp is the return type
        for (StatementNode st : returnStatements) {
            if (st.kind == NodeKind.RETURN) {
                ReturnNode ret = (ReturnNode) st;

                if (typeExp == null) {
                    typeExp = ret.exp.typeExp;
                } else if (!ret.exp.typeExp.equals(typeExp)) {
                    System.err.println("Type mismatch: Block returned ");
                    typeExp.print(0, System.err);
                    System.err.println("but later also returned ");
                    ret.exp.typeExp.print(0, System.err);
                    System.exit(1);
                }
            }
        }

        if (typeExp == null) {
            // if no return statements, use last expression as value
            this.typeExp = ((ExpressionNode) lastNode).typeExp;
        } else {
            // check last expression against return types
            if (lastNode instanceof ExpressionNode) {
                if (!((ExpressionNode) lastNode).typeExp.equals(typeExp)) {
                    System.err.println("Type mismatch between last expression" +
                        " and return type;");
                    ((ExpressionNode) lastNode).typeExp.print(0, System.err);
                    this.typeExp.print(0, System.err);
                    System.exit(1);
                }
            }
        }
    }

    @Override
    public void print(int indent, PrintStream stream) {
        Main.printlnWithIndent("BlockNode {", indent, stream);
        Main.printlnWithIndent("\tbody:", indent, stream);

        for (StatementNode st : this.body) {
            st.print(indent + 2, stream);
        }

        Main.printlnWithIndent("\treturn type exp:", indent, stream);
        typeExp.print(indent + 2, stream);

        Main.printlnWithIndent("} BlockNode", indent, stream);
    }
}
