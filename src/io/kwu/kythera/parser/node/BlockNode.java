package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.NodeType;
import io.kwu.kythera.parser.ParserException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockNode extends ExpressionNode {
    public final ExpressionNode[] body;

    public BlockNode(ExpressionNode[] body) throws ParserException {
        super(NodeKind.BLOCK); // type to be set later
        this.body = body;

        NodeType returnType = null;
        List<ExpressionNode> returns = Arrays
            .stream(body)
            .filter(
                node -> node.kind == NodeKind.RETURN
            )
            .collect(Collectors.toList());

        ExpressionNode lastNode = body[body.length - 1];
        if(lastNode.kind != NodeKind.RETURN) {
            returns.add(body[body.length - 1]); // if no return, block evaluates to last expression value
        }

        for(ExpressionNode e : body) {
            if(returnType == null) {
                returnType = e.type;
            } else if(!e.type.equals(returnType)){
                throw new ParserException("Type mismatch: Block returned " + returnType.toString() + " but later returned " + e.type.toString());
            }
        }

        this.type = returnType;
    }
}
