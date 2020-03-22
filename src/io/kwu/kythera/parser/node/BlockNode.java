package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

import java.util.List;
import java.util.stream.Collectors;

public class BlockNode extends ExpressionNode {
    public final List<StatementNode> body;
    public NodeType returnType;

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
        returnType = null;
        for (StatementNode st : returns) {
            ReturnNode ret = (ReturnNode) st;

            if(returnType == null) {
                returnType = ret.value.type;
            } else if (!ret.value.type.equals(returnType)) {
                System.err.println("Type mismatch: Block returned " + returnType.toString() + " but later also returned " + ret.value.type.toString());
                System.exit(1);
            }
        }

        this.type = returnType;
    }
}
