package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.NodeType;
import io.kwu.kythera.parser.ParserException;

public class NewNode extends ExpressionNode {
    public NewNode(TypeLiteralNode target) throws ParserException  {
        super(NodeKind.NEW, new NodeType(target));
    }
}
