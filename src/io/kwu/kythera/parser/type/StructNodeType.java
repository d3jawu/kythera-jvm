package io.kwu.kythera.parser.type;

import java.util.Map;

public class StructNodeType extends NodeType {
    public final Map<String, NodeType> entries;

    public StructNodeType(Map<String, NodeType> entries) {
        super(BaseType.STRUCT);
        this.entries = entries;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof StructNodeType)) {
            return false;
        }

        StructNodeType otherStructNodeType = (StructNodeType)other;
        assert(otherStructNodeType.baseType.equals(BaseType.STRUCT));

        return this.entries.equals(otherStructNodeType.entries);
    }
}