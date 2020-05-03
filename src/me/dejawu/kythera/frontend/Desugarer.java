package me.dejawu.kythera.frontend;

import me.dejawu.kythera.frontend.node.*;
import me.dejawu.kythera.frontend.tokenizer.Symbol;

import java.util.*;
import java.util.stream.Collectors;

public class Desugarer extends Visitor {
    public Desugarer(List<StatementNode> program) {
        super(program);
    }

    @Override
    protected StatementNode visitLet(LetNode letNode) {
        return new LetNode(letNode.identifier, visitExpression(letNode.value));
    }

    @Override
    protected StatementNode visitReturn(ReturnNode returnNode) {
        return new ReturnNode(visitExpression(returnNode.exp));
    }

    @Override
    protected ExpressionNode visitAs(AsNode asNode) {
        return new AsNode(visitExpression(asNode.from), visitExpression(asNode.to));
    }

    @Override
    protected ExpressionNode visitAssign(AssignNode assignNode) {
        if (assignNode.operator.equals(Symbol.EQUALS)) {
            return assignNode;
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

    @Override
    protected ExpressionNode visitBlock(BlockNode blockNode) {
        List<StatementNode> desugared = new ArrayList<>();

        for(StatementNode st : blockNode.body) {
            desugared.add(visitStatement(st));
        }

        return new BlockNode(desugared);
    }

    @Override
    protected ExpressionNode visitBracketAccess(BracketAccessNode bracketAccessNode) {
        return new BracketAccessNode(
            visitExpression(bracketAccessNode.target),
            visitExpression(bracketAccessNode.key)
        );
    }

    @Override
    protected ExpressionNode visitCall(CallNode callNode) {
        return new CallNode(
            visitExpression(callNode.target),
            callNode
                .arguments
                .stream()
                .map(this::visitExpression)
                .collect(Collectors.toList())
        );
    }

    @Override
    protected ExpressionNode visitDotAccess(DotAccessNode dotAccessNode) {
        return new DotAccessNode(visitExpression(dotAccessNode.target), dotAccessNode.key);
    }

    @Override
    protected ExpressionNode visitLiteral(LiteralNode literalNode) {
        if(literalNode instanceof FnLiteralNode) {
            FnLiteralNode fnLiteralNode = (FnLiteralNode) literalNode;

            SortedMap<String, ExpressionNode> params = new TreeMap<>();

            for(Map.Entry<String, ExpressionNode> e : fnLiteralNode.parameters.entrySet()) {
                params.put(e.getKey(), visitExpression(e.getValue()));
            }

            return new FnLiteralNode(
                params,
                (BlockNode) visitExpression(fnLiteralNode.body)
            );
        } else if(literalNode instanceof StructLiteralNode) {
            System.out.println("Warning: desugaring for struct literal nodes not yet implemented");
//            StructLiteralNode structLiteralNode = (StructLiteralNode) literalNode;

//            return new StructLiteralNode()
            return literalNode;
        } else if(literalNode instanceof TypeLiteralNode) {
            System.out.println("Warning: desugaring for type literal nodes not yet implemented");
            return literalNode;
        } else {
            return literalNode;
        }
    }

    @Override
    protected ExpressionNode visitIdentifier(IdentifierNode identifierNode) {
        return identifierNode;
    }

    @Override
    protected ExpressionNode visitIf(IfNode ifNode) {
        return ifNode;
    }

    // unary becomes function call
    @Override
    protected ExpressionNode visitUnary(UnaryNode unaryNode) {
        return new CallNode(
            new DotAccessNode(
                visitExpression(unaryNode.target),
                unaryNode.operator.symbol),
            new ArrayList<>()
        );
    }

    @Override
    protected ExpressionNode visitWhile(WhileNode whileNode) {
        return whileNode;
    }
}
