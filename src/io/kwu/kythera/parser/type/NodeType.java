package io.kwu.kythera.parser.type;

/**
 * Parser's internal representation of a type.
 * Distinct from a TypeLiteralNode (which represents and comes from syntax).
 * <p>
 * Analogous to a ParseNode with kind "type" in kythera-js.
 */
public class NodeType {
    public final BaseType baseType;

    public NodeType(BaseType baseType) {
        this.baseType = baseType;
    }

    // scalar types only need one instance for the whole parser, provided statically here
    public static NodeType UNIT = new NodeType(BaseType.UNIT);
    public static NodeType INT = new NodeType(BaseType.INT);
    public static NodeType BOOL = new NodeType(BaseType.BOOL);
    public static NodeType TYPE = new NodeType(BaseType.TYPE);
    public static NodeType STR = new NodeType(BaseType.STR);

    // we only need one instance of each scalar node type, stored here
    public enum PrimitiveNodeType {
        UNIT(new NodeType(BaseType.UNIT)),
        BOOL(new NodeType(BaseType.BOOL)),
        INT(new NodeType(BaseType.INT)),
        DOUBLE(new NodeType(BaseType.DOUBLE)),
        CHAR(new NodeType(BaseType.CHAR));

        PrimitiveNodeType(BaseType bt) {

        }
    }
}