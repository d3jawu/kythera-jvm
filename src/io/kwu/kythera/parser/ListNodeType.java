package io.kwu.kythera.parser;

import io.kwu.kythera.parser.node.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

public class ListNodeType extends NodeType {
    public final NodeType entryType;

    public ListNodeType(NodeType entryType) {
        super(BaseType.LIST);

        this.entryType = entryType;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ListNodeType)) {
            return false;
        }

        ListNodeType otherListNodeType = (ListNodeType) other;
        assert(otherListNodeType.baseType.equals(BaseType.LIST));

        return this.entryType.equals(otherListNodeType.entryType);
    }
}
