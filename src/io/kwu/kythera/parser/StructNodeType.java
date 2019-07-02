package io.kwu.kythera.parser;

import java.util.HashMap;

public class StructNodeType extends NodeType {
    public final HashMap<String, NodeType> entries;

    public StructNodeType(HashMap<String, NodeType> entries) {
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

        return otherStructNodeType.entries.equals(this.entries);
    }
}