package io.kwu.kythera.parser.node;

import io.kwu.kythera.Main;

import java.util.List;
import java.util.stream.Collectors;

public class BlockNode extends ExpressionNode {
    public final List<StatementNode> body;
    public ExpressionNode returnTypeExp;

    public BlockNode(List<StatementNode> body) {
        super(NodeKind.BLOCK); // type to be set later
        this.body = body;

        List<StatementNode> returns = body
            .stream()
            .filter(
                node -> node.kind == NodeKind.RETURN
            )
            .collect(Collectors.toList());

        StatementNode lastNode = body.get(body.size() - 1);

        if(!(lastNode instanceof ExpressionNode)) {
            System.err.println("Last statement in block must be an expression.");
            System.exit(1);
        }

        if (lastNode.kind != NodeKind.RETURN) {
            returns.add(lastNode); // if no return, block evaluates to last expression value
        }

        // check all return statements for type equivalence

        returnTypeExp = null;
        for (StatementNode st : returns) {
            ReturnNode ret = (ReturnNode) st;

            if(returnTypeExp == null) {
                returnTypeExp = ret.exp.typeExp;
            } else if (!ret.exp.typeExp.equals(returnTypeExp)) {
                System.err.println("Type mismatch: Block returned " + returnTypeExp.typeExp.toString() + " but later also returned " + ret.exp.typeExp.toString());
                System.exit(1);
            }
        }

        this.typeExp = returnTypeExp;
    }

    @Override
    public void print(int indent) {
        Main.printlnWithIndent("BlockNode {", indent);
        Main.printlnWithIndent("\tBody:", indent);

        for(StatementNode st : this.body) {
            st.print(indent + 1);
        }

        Main.printlnWithIndent("} BlockNode", indent);
    }
}
