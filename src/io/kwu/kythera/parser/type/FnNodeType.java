package io.kwu.kythera.parser.type;

import java.util.List;

public class FnNodeType extends NodeType {
    public final List<NodeType> parameters;
    public final NodeType returnType;

    public FnNodeType(List<NodeType> parameters, NodeType returnType) {
        super(BaseType.FN);
        this.parameters = parameters;
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof FnNodeType)) {
            return false;
        }

        FnNodeType otherFnNodeType = (FnNodeType)other;

        assert(otherFnNodeType.baseType.equals(BaseType.FN));

        return this.returnType.equals(otherFnNodeType.returnType) &&
            this.parameters.equals(otherFnNodeType.parameters);
    }
}
