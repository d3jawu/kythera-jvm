package io.kwu.kythera.parser;

public class MapNodeType extends NodeType {
    public final NodeType keyType;
    public final NodeType valueType;

    public MapNodeType(NodeType keyType, NodeType valueType) {
        super(BaseType.MAP);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof MapNodeType)) {
            return false;
        }

        MapNodeType otherMapNodeType = (MapNodeType) other;
        assert(otherMapNodeType.baseType.equals(BaseType.MAP));

        return this.keyType.equals(otherMapNodeType.keyType) &&
                this.valueType.equals(otherMapNodeType.valueType);
    }
}
