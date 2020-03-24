package io.kwu.kythera.parser.node;

import java.util.ArrayList;
import java.util.SortedMap;

public class FnLiteralNode extends LiteralNode {
    public final SortedMap<String, ExpressionNode> parameters;
    public final BlockNode body;
    public final ExpressionNode returnType;

    public FnLiteralNode(SortedMap<String, ExpressionNode> parameters, BlockNode body, ExpressionNode returnType) {
        super(new FnTypeLiteralNode(new ArrayList<>(parameters.values()), returnType));

        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
    }

    @Override
    public void print(int indent) {
        // TODO
    }
}
