package io.kwu.kythera.parser.node;

public class ReturnNode extends StatementNode {
    public final ExpressionNode value;

    public ReturnNode(ExpressionNode value) {
        super(NodeKind.RETURN);
        this.value = value;
    }
}
