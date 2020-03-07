package io.kwu.kythera.parser.type;

/**
 * This naming is a bit confusing, so to clarify,
 * this is the parser's internal understanding of a type value within the language.
 * It is extended from NodeType, and also contains a NodeType to represent the underlying type,
 * in the same way that other compound data types are subtyped based on the types they contain.
 * For example,
 */
public class TypeNodeType extends NodeType {
    public final NodeType subType;

    public TypeNodeType(NodeType subType) {
        this.subType = subType;
    }
}
