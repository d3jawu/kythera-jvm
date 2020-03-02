package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.FnNodeType;
import io.kwu.kythera.parser.type.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class FnLiteralNode extends LiteralNode {
    public final SortedMap<String, NodeType> parameters;
    public final List<ExpressionNode> body;
    public final NodeType returnType;

    public FnLiteralNode(SortedMap<String, NodeType> parameters, NodeType returnType) {
        super(new FnNodeType(new ArrayList<>(parameters.values()), returnType));

        this.parameters = parameters;
        this.body = new ArrayList<>();
        this.returnType = returnType;
    }

    public FnLiteralNode(SortedMap<String, NodeType> parameters, List<ExpressionNode> body, NodeType returnType) {
        super(new FnNodeType(new ArrayList<>(parameters.values()), returnType));

        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }
}
