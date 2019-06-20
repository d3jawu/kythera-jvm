package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.ParserException;
import io.kwu.kythera.parser.Type;

// ExpressionNodes are statements that evaluate to a value and therefore also have a type.
public abstract class ExpressionNode extends StatementNode {
    public Type type; // may need to be set after ExpressionNode is instantiated

    ExpressionNode(NodeKind kind, Type type) throws ParserException {
        super(kind);
        this.type = type;
    }
}
