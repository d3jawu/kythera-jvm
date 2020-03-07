package io.kwu.kythera.parser.type;

import io.kwu.kythera.parser.node.TypeLiteralNode;

import java.util.Collections;
import java.util.Map;

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

    public NodeType(TypeLiteralNode typeNode) {
        this.baseType = typeNode.baseType;
    }

    // scalar types only need one instance for the whole parser, provided statically here
    public static NodeType UNIT = new NodeType(BaseType.UNIT);
    public static NodeType INT = new NodeType(BaseType.INT);
    public static NodeType BOOL = new NodeType(BaseType.BOOL);
    public static NodeType TYPE = new NodeType(BaseType.TYPE);
    public static NodeType STR = new NodeType(BaseType.STR);

    public enum PrimitiveNodeType {
        UNIT(),


    }
}