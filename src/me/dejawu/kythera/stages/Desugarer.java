package me.dejawu.kythera.stages;

import me.dejawu.kythera.ast.*;
import me.dejawu.kythera.stages.tokenizer.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Desugarer extends Visitor {
    public Desugarer(List<StatementNode> program) {
        super(program);
    }

    // TODO desugar "return;" into "return unit;"

    @Override
    protected ExpressionNode visitAssign(AssignNode assignNode) {
        if (assignNode.operator.equals(Symbol.EQUALS)) {
            return new AssignNode(Symbol.EQUALS,
                visitExpression(assignNode.left),
                visitExpression(assignNode.right)
            );
        } else {
            // separate assignment, e.g. x += 10 becomes x = (x + 10)
            return new AssignNode(
                Symbol.EQUALS,
                visitExpression(assignNode.left),
                new CallNode(
                    new DotAccessNode(
                        visitExpression(assignNode.left),
                        "" + assignNode.operator.symbol.charAt(0)),
                    new ArrayList<>() {
                        {
                            add(
                                visitExpression(assignNode.right)
                            );
                        }
                    }
                )
            );
        }
    }

    // TODO desugar unary into not()

    // binary infix becomes function call
    @Override
    protected ExpressionNode visitBinary(BinaryNode binaryNode) {
        return new CallNode(
            new DotAccessNode(
                visitExpression(binaryNode.left),
                binaryNode.operator.symbol),
            new ArrayList<>() {
                {
                    add(
                        visitExpression(binaryNode.right)
                    );
                }
            }
        );
    }

    // TODO desugar block into fn()unit
    @Override
    protected ExpressionNode visitBlock(BlockNode blockNode) {
        List<StatementNode> desugared = new ArrayList<>();

        for (StatementNode st : blockNode.body) {
            desugared.add(visitStatement(st));
        }

        return new BlockNode(desugared);
    }

    // TODO desugar into overloaded method call
    @Override
    protected ExpressionNode visitBracketAccess(BracketAccessNode bracketAccessNode) {
        return new BracketAccessNode(
            visitExpression(bracketAccessNode.target),
            visitExpression(bracketAccessNode.key)
        );
    }
}
