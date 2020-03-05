package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;
import io.kwu.kythera.parser.ParserException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockNode extends ExpressionNode {
    public final List<ExpressionNode> body;

    public BlockNode(List<ExpressionNode> body) throws ParserException {
        super(NodeKind.BLOCK); // type to be set later
        this.body = body;

        NodeType returnType = null;
        List<ExpressionNode> returns = body
            .stream()
            .filter(
                node -> node.kind == NodeKind.RETURN
            )
            .collect(Collectors.toList());

        ExpressionNode lastNode = body.get(body.size() - 1);
        if(lastNode.kind != NodeKind.RETURN) {
            returns.add(lastNode); // if no return, block evaluates to last expression value
        }

        for(ExpressionNode e : body) {
            if(returnType == null) {
                returnType = e.type;
            } else if(!e.type.equals(returnType)){
                throw new ParserException("Type mismatch: Block returned " + returnType.toString() + " but later also returned " + e.type.toString());
            }
        }

        this.type = returnType;
    }
}
