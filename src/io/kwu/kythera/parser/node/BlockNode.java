package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

import java.util.List;

public class BlockNode extends ExpressionNode {
    public final List<StatementNode> body;
    public NodeType returnType;

    public BlockNode(List<StatementNode> body) {
        super(NodeKind.BLOCK); // type to be set later
        this.body = body;

        returnType = null;

        StatementNode lastNode = body.get(body.size() - 1);

        if(!(lastNode instanceof ExpressionNode)) {
            System.err.println("Last statement in block must be an expression.");
            System.exit(1);
        } else {
            ExpressionNode lastExpressionNode = (ExpressionNode) lastNode;
            returnType = lastExpressionNode.type;
            this.type = returnType;
        }
    }
}
