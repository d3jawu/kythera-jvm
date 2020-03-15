package io.kwu.kythera.parser.type;

/**
 * This naming is a bit confusing, so to clarify,
 * this is the parser's internal understanding of a type value within the language.
 * It is extended from NodeType, and also contains a NodeType to represent the underlying type,
 * in the same way that other compound data types are subtyped based on the types they contain.
 * It generally helps to think of `type` as just another compound data type, like struct or function.
 */
public class TypeNodeType extends NodeType {
    // the type restrictions on this type node (null is "any")
    public final NodeType subType;

    public TypeNodeType() {
        super(BaseType.TYPE);
        this.subType = null;
    }

    public TypeNodeType(NodeType subType) {
        super(BaseType.TYPE);
        this.subType = subType;
    }
}
