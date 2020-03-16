package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.NodeType;

/**
 * For scalar literals (e.g. 0, true)
 */
public class ScalarLiteralNode<V> extends LiteralNode {
    public final V value;

    public ScalarLiteralNode(NodeType type, V value) {
        super(type);

        if (!type.baseType.scalar) {
            System.err.println(type.baseType.name + " is not a scalar type.");
        }

        this.value = value;
    }
}