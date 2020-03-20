package io.kwu.kythera.parser.type;

import java.util.HashMap;

public class StructNodeType extends NodeType {
    public final HashMap<String, NodeType> entries;

    public StructNodeType() {
        super(BaseType.STRUCT);
        this.entries = new HashMap<>();
    }

    public StructNodeType(HashMap<String, NodeType> entries) {
        super(BaseType.STRUCT);
        this.entries = entries;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof StructNodeType)) {
            return false;
        }

        StructNodeType otherStructNodeType = (StructNodeType) other;
        assert (otherStructNodeType.baseType.equals(BaseType.STRUCT));

        return this.entries.equals(otherStructNodeType.entries);
    }
}