package io.kwu.kythera.parser.type;

public class PrimitiveNodeType extends NodeType {
    PrimitiveNodeType(BaseType bt) {
        super(bt);
    }


    // scalar types only need one instance for the whole parser, provided statically here
    public static NodeType UNIT = new PrimitiveNodeType(BaseType.UNIT);
    public static NodeType INT = new PrimitiveNodeType(BaseType.INT);
    public static NodeType DOUBLE = new PrimitiveNodeType(BaseType.DOUBLE);
    public static NodeType BOOL = new PrimitiveNodeType(BaseType.BOOL);
    public static NodeType TYPE = new PrimitiveNodeType(BaseType.TYPE);
    public static NodeType STR = new PrimitiveNodeType(BaseType.STR);

}
