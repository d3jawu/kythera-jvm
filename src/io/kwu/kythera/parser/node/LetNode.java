package io.kwu.kythera.parser.node;

public class LetNode extends StatementNode {
    public final String identifier;
    public final ExpressionNode value;

    public LetNode(String identifier, ExpressionNode value) {
        super(NodeKind.LET);

        this.identifier = identifier;
        this.value = value;
    }
}
