package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.BaseType;

import java.util.List;

public class FnTypeLiteralNode extends TypeLiteralNode {
    public final List<ExpressionNode> parameterTypes;
    public final ExpressionNode returnType;

    public FnTypeLiteralNode(List<ExpressionNode> parameterTypes, ExpressionNode returnType) {
        super(BaseType.FN);

        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }
}
