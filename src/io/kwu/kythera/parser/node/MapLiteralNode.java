package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.MapNodeType;
import io.kwu.kythera.parser.NodeType;

import java.util.Map;

public class MapLiteralNode extends LiteralNode {
    public final Map<ExpressionNode, ExpressionNode> entries;

    public MapLiteralNode(NodeType keyType, NodeType valueType) {
        super(new MapNodeType(keyType, valueType));
    }
}
