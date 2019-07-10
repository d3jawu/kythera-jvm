package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.BaseType;
import io.kwu.kythera.parser.NodeType;
import io.kwu.kythera.parser.ParserException;

/**
 * For scalar literals (e.g. 0, true)
 */
public class ScalarLiteralNode<V> extends LiteralNode {
    public final V value;

    public ScalarLiteralNode(NodeType type, V value) throws ParserException {
        super(type);

        if (!type.baseType.scalar) {
            throw new ParserException(type.baseType.name + " is not a scalar type.");
        }

        this.value = value;
    }
}