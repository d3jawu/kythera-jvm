package io.kwu.kythera.parser;

import java.util.List;

public class TupleNodeType extends NodeType {
    public final List<NodeType> entryTypes;

    public TupleNodeType(List<NodeType> entryTypes) {
        super(BaseType.TUPLE);
        this.entryTypes = entryTypes;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof TupleNodeType)) {
            return false;
        }

        TupleNodeType otherTupleNodeType = (TupleNodeType) other;
        assert(otherTupleNodeType.baseType.equals(BaseType.STRUCT));

        return this.entryTypes.equals(otherTupleNodeType.entryTypes);
    }
}
