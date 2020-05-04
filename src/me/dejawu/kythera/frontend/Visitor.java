package me.dejawu.kythera.frontend;

import me.dejawu.kythera.ast.*;

import java.util.ArrayList;
import java.util.List;

// template class for traversing AST nodes
public abstract class Visitor {
    protected final List<StatementNode> input;

    public Visitor(List<StatementNode> input) {
        this.input = input;
    }

    // runs operation on all nodes and returns new AST list
    public List<StatementNode> visit() {
        List<StatementNode> result = new ArrayList<>();

        for(StatementNode st : input) {
            result.add(visitStatement(st));
        }

        return result;
    }

    protected StatementNode visitStatement(StatementNode st) {
        switch (st.kind) {
            case LET:
                return visitLet((LetNode) st);
            case RETURN:
                return visitReturn((ReturnNode) st);
            default:
                return visitExpression((ExpressionNode) st);
        }
    }

    protected ExpressionNode visitExpression(ExpressionNode exp) {
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
//            case TYPEOF:
//                return visitTypeof
            case UNARY:
                return visitUnary((UnaryNode) exp);
            case WHILE:
                return visitWhile((WhileNode) exp);
            default: return exp;
        }
    }


    protected abstract StatementNode visitLet(LetNode letNode);
    protected abstract StatementNode visitReturn(ReturnNode returnNode);

    protected abstract ExpressionNode visitAs(AsNode asNode);
    protected abstract ExpressionNode visitAssign(AssignNode assignNode);
    protected abstract ExpressionNode visitBinary(BinaryNode binaryNode);
    protected abstract ExpressionNode visitBlock(BlockNode blockNode);
    protected abstract ExpressionNode visitBracketAccess(BracketAccessNode bracketAccessNode);
    protected abstract ExpressionNode visitCall(CallNode callNode);
    protected abstract ExpressionNode visitDotAccess(DotAccessNode dotAccessNode);
    protected abstract ExpressionNode visitLiteral(LiteralNode literalNode);
//    protected abstract ExpressionNode visitTypeof()
    protected abstract ExpressionNode visitIdentifier(IdentifierNode identifierNode);
    protected abstract ExpressionNode visitIf(IfNode ifNode);
    protected abstract ExpressionNode visitUnary(UnaryNode unaryNode);
    protected abstract ExpressionNode visitWhile(WhileNode whileNode);
}
