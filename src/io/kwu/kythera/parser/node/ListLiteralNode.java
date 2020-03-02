package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.ListNodeType;
import io.kwu.kythera.parser.type.NodeType;
import io.kwu.kythera.parser.ParserException;

import java.util.ArrayList;
import java.util.List;

public class ListLiteralNode extends LiteralNode {
    public final List<ExpressionNode> entries;

    public ListLiteralNode(ListNodeType listNodeType) {
        super(listNodeType);

        this.entries = new ArrayList<>();
    }

    public ListLiteralNode(NodeType entryType) {
        super(new ListNodeType(entryType));

        this.entries = new ArrayList<>();
    }

    public ListLiteralNode(NodeType entryType, List<ExpressionNode> entries) throws ParserException {
        super(new ListNodeType(entryType));

        this.entries = entries;
    }
}
