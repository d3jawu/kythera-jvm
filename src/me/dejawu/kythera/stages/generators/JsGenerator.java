package me.dejawu.kythera.stages.generators;

import me.dejawu.kythera.ast.*;

import java.util.List;

public class JsGenerator implements Generator {
    private final StringBuilder out;
    private final List<StatementNode> input;

    private final String RUNTIME_VAR = "_KYTHERA";

    public JsGenerator(List<StatementNode> program) {
        out = new StringBuilder();
        this.input = program;
    }

    @Override
    public byte[] compile() {
        // initialize runtime
        out.append("const " + RUNTIME_VAR + " = require('./runtime');");

        for(StatementNode st : input) {
            this.visitStatement(st);
        }

        return out.toString().getBytes();
    }

    private void visitStatement(StatementNode st) {
        switch (st.kind) {
            case LET:
                visitLet((LetNode) st);
                break;
            case RETURN:
                visitReturn((ReturnNode) st);
                break;
            default:
                visitExpression((ExpressionNode) st);
        }
    }

    private void visitLet(LetNode node) {

    }

    private void visitReturn(ReturnNode node) {

    }

    private void visitExpression(ExpressionNode node) {
        switch (node.kind) {
            case ASSIGN:
                this.visitAssign((AssignNode) node);
                return;
            case LITERAL:
                this.visitLiteral((LiteralNode) node);
                return;
            case IDENTIFIER:
                this.visitIdentifier((IdentifierNode) node);
                return;
            case IF:
                this.visitIf((IfNode) node);
                return;
            case WHILE:
                this.visitWhile((WhileNode) node);
                return;
            case AS:
                this.visitAs((AsNode) node);
                return;
            case CALL:
                this.visitCall((CallNode) node);
                return;
            case TYPEOF:
                this.visitTypeof((TypeofNode) node);
                return;
            case BLOCK:
                this.visitBlock((BlockNode) node);
                return;
            case UNARY:
                this.visitUnary((UnaryNode) node);
                return;
            case BINARY:
                this.visitBinary((BinaryNode) node);
                return;
            case ACCESS:
                if (node instanceof DotAccessNode) {
                    this.visitDotAccess((DotAccessNode) node);
                    return;
                } else if(node instanceof BracketAccessNode) {
                    this.visitBracketAccess((BracketAccessNode) node);
                    return;
                } else {
                    System.err.println("Invalid access node: " + node.kind.name());
                    System.exit(1);
                }
            default:
                break;
        }

        System.err.println("Unsupported or not implemented: " + node.kind.name());
        System.exit(1);
    }

    private void visitAssign(AssignNode node) {

    }

    private void visitLiteral(LiteralNode node) {

    }

    private void visitIdentifier(IdentifierNode node) {

    }

    private void visitIf(IfNode node) {

    }

    private void visitWhile(WhileNode node) {

    }

    private void visitAs(AsNode node) {

    }

    private void visitCall(CallNode node) {

    }

    private void visitTypeof(TypeofNode node) {

    }

    private void visitBlock(BlockNode node) {

    }

    private void visitUnary(UnaryNode node) {

    }

    private void visitBinary(BinaryNode node) {

    }

    private void visitDotAccess(DotAccessNode node) {

    }

    private void visitBracketAccess(BracketAccessNode node) {

    }
}
