package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

public class AsNode extends ExpressionNode {
    public final ExpressionNode from;
    public final NodeType to;

    public AsNode(ExpressionNode from, NodeType to) {
       super(NodeKind.AS);

       this.from = from;
       this.to = to;

       this.type = to;
    }
}
