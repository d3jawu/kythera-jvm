package io.kwu.kythera.parser.node;

import io.kwu.kythera.parser.ParserException;

public class LetNode extends StatementNode{
    public final String identifier;
    public final ExpressionNode value;

    public LetNode(String identifier, ExpressionNode value) throws ParserException {
        super(NodeKind.LET);

        this.identifier = identifier;
        this.value = value;
    }
}
