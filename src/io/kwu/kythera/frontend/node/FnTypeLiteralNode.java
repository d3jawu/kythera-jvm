package io.kwu.kythera.frontend.node;

import io.kwu.kythera.frontend.BaseType;

import java.util.List;

public class FnTypeLiteralNode extends TypeLiteralNode {
    public final List<ExpressionNode> parameterTypeExps;
    public final ExpressionNode returnTypeExp;

    public FnTypeLiteralNode(List<ExpressionNode> parameterTypeExps,
                             ExpressionNode returnTypeExp) {
        super(BaseType.FN);

        this.parameterTypeExps = parameterTypeExps;
        this.returnTypeExp = returnTypeExp;
    }
}
