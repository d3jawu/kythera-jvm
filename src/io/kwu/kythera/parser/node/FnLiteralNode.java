package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.FnNodeType;
import io.kwu.kythera.parser.type.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class FnLiteralNode extends LiteralNode {
    public final SortedMap<String, NodeType> parameters;
    public final BlockNode body;
    public final NodeType returnType;

    public FnLiteralNode(SortedMap<String, NodeType> parameters, BlockNode body, NodeType returnType) {
        super(new FnNodeType(new ArrayList<>(parameters.values()), returnType));

        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }
}
