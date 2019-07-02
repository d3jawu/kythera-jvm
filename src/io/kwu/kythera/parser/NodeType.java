package io.kwu.kythera.parser;

import io.kwu.kythera.parser.node.TypeLiteralNode;

import java.util.Collections;
import java.util.Map;

/**
 * Parser's internal representation of a type.
 * Distinct from a TypeLiteralNode (which represents and comes from syntax).
 *
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

    public String toString() {
        return baseType.name;
    }

    public String toString(int indent) {
        // if called from a child class, will this use the overriding or parent implementation?
        return this.toString().indent(indent);
    }

    // TODO there might be a more elegant way to do this conversion
    // scalar types only need one instance for the whole parser, provided statically here
    public static NodeType INT = new NodeType(BaseType.INT);
    public static NodeType BOOL = new NodeType(BaseType.BOOL);
    public static NodeType TYPE = new NodeType(BaseType.TYPE);

    public static final Map<BaseType, NodeType> fromBaseType = Collections.unmodifiableMap(
        Map.ofEntries(
            Map.entry(BaseType.INT, INT),
            Map.entry(BaseType.BOOL, BOOL),
            Map.entry(BaseType.TYPE, TYPE)
        )
    );
}
