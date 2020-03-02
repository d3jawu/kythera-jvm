package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.type.BaseType;
import io.kwu.kythera.parser.ParserException;

/**
 * Node for access by dot, e.g.
 * myObject.fieldName
 */
public class DotAccessNode extends ExpressionNode {
    public final ExpressionNode target;
    public final String key;

    public DotAccessNode(ExpressionNode target, String key) throws ParserException {
        super(NodeKind.ACCESS);

        if(target.type.baseType != BaseType.STRUCT) {
            throw new ParserException("Expected struct with field " + key + ", but found " + target.type.toString());
        }

        this.target = target;
        this.key = key;
    }
}
