package me.dejawu.kythera.frontend;

import me.dejawu.kythera.ast.*;

import java.util.List;

// type-checks nodes
// links statically known types to type literals
// marks dynamically known types
// makes sure identifiers and field accesses are valid
public class TypeChecker extends Visitor {
    public TypeChecker(List<StatementNode> program) {
        super(program);
    }

    @Override
    public List<StatementNode> visit() {
        return this.input;
    }

    @Override
    protected StatementNode visitLet(LetNode letNode) {
        return null;
    }

    @Override
    protected StatementNode visitReturn(ReturnNode returnNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitAs(AsNode asNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitAssign(AssignNode assignNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitBinary(BinaryNode binaryNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitBlock(BlockNode blockNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitBracketAccess(BracketAccessNode bracketAccessNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitCall(CallNode callNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitDotAccess(DotAccessNode dotAccessNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitLiteral(LiteralNode literalNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitIdentifier(IdentifierNode identifierNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitIf(IfNode ifNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitUnary(UnaryNode unaryNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitWhile(WhileNode whileNode) {
        return null;
    }
}
