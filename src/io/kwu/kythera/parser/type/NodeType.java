package io.kwu.kythera.parser.type;

/**
 * Parser's internal representation of a type.
 * Distinct from a TypeLiteralNode (which represents and comes from syntax).
 * <p>
 * Analogous to a ParseNode with kind "type" in kythera-js.
 */
public abstract class NodeType {
    public final BaseType baseType;

    public NodeType(BaseType baseType) {
        this.baseType = baseType;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof NodeType)) {
            return false;
        }

        NodeType otherNodeType = (NodeType) other;
        return otherNodeType.baseType == this.baseType;
    }
}