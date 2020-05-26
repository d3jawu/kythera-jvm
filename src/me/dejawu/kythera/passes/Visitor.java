package me.dejawu.kythera.passes;

import me.dejawu.kythera.ast.*;

import java.util.ArrayList;
import java.util.List;

// template class for traversing AST nodes
// S and E are separate to allow extending classes to use StatementNode and ExpressionNode
public abstract class Visitor<S, E extends S> {
    protected final List<StatementNode> input;

    public Visitor(List<StatementNode> input) {
        this.input = input;
    }

    // runs operation on all nodes and returns new AST list
    public List<S> visit() {
        List<S> result = new ArrayList<>();

        for(StatementNode st : input) {
            result.add(visitStatement(st));
        }

        return result;
    }

    protected S visitStatement(StatementNode st) {
        switch (st.kind) {
            case LET:
                return visitLet((LetNode) st);
            case RETURN:
                return visitReturn((ReturnNode) st);
            default:
                return visitExpression((ExpressionNode) st);
        }
    }

    protected E visitExpression(ExpressionNode exp) {
        switch (exp.kind) {
            case ACCESS:
                if(exp instanceof DotAccessNode) {
                    return visitDotAccess((DotAccessNode) exp);
                } else {
                    return visitBracketAccess((BracketAccessNode) exp);
                }
            case AS:
                return visitAs((AsNode) exp);
            case ASSIGN:
                return visitAssign((AssignNode) exp);
            case BINARY:
                return visitBinary((BinaryNode) exp);
            case BLOCK:
                return visitBlock((BlockNode) exp);
            case CALL:
                return visitCall((CallNode) exp);
            case IDENTIFIER:
                return visitIdentifier((IdentifierNode) exp);
            case IF:
                return visitIf((IfNode) exp);
            case LITERAL:
                return visitLiteral((LiteralNode) exp);
            case TYPEOF:
                return visitTypeof((TypeofNode) exp);
            case UNARY:
                return visitUnary((UnaryNode) exp);
            case WHILE:
                return visitWhile((WhileNode) exp);
            default:
                System.err.println("Invalid or unhandled expression: " + exp.kind);
                System.exit(1);
                return null;
        }
    }


    protected abstract S visitLet(LetNode letNode);
    protected abstract S visitReturn(ReturnNode returnNode);

    protected abstract E visitAs(AsNode asNode);
    protected abstract E visitAssign(AssignNode assignNode);
    protected abstract E visitBinary(BinaryNode binaryNode);
    protected abstract E visitBlock(BlockNode blockNode);
    protected abstract E visitBracketAccess(BracketAccessNode bracketAccessNode);
    protected abstract E visitCall(CallNode callNode);
    protected abstract E visitDotAccess(DotAccessNode dotAccessNode);
    protected abstract E visitLiteral(LiteralNode literalNode);
    protected abstract E visitTypeof(TypeofNode typeofNode);
    protected abstract E visitIdentifier(IdentifierNode identifierNode);
    protected abstract E visitIf(IfNode ifNode);
    protected abstract E visitUnary(UnaryNode unaryNode);
    protected abstract E visitWhile(WhileNode whileNode);
}
