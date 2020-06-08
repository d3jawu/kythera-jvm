package me.dejawu.kythera.stages;

import me.dejawu.kythera.ast.*;

import java.util.*;
import java.util.stream.Collectors;

// template class for traversing AST nodes
// S and E are separate to allow extending classes to use StatementNode and ExpressionNode
public abstract class Visitor {
    protected final List<StatementNode> input;

    public Visitor(List<StatementNode> input) {
        this.input = input;
    }

    // runs operation on all nodes and returns new AST list
    public List<StatementNode> visit() {
        List<StatementNode> result = new ArrayList<>();

        for (StatementNode st : input) {
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
                if (exp instanceof DotAccessNode) {
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

    // default implementations pass node on unchanged

    protected StatementNode visitLet(LetNode letNode) {
        return new LetNode(letNode.identifier, visitExpression(letNode.value));
    }

    protected StatementNode visitReturn(ReturnNode returnNode) {
        return new ReturnNode(visitExpression(returnNode.exp));
    };

    protected ExpressionNode visitAs(AsNode asNode) {
        return new AsNode(visitExpression(asNode.from), visitExpression(asNode.to));
    }

    protected ExpressionNode visitAssign(AssignNode assignNode) {
        return new AssignNode(
                assignNode.operator,
                visitExpression(assignNode.left),
                visitExpression(assignNode.right)
                );
    }

    protected ExpressionNode visitBinary(BinaryNode binaryNode) {
        return new BinaryNode(
                binaryNode.operator,
                visitExpression(binaryNode.left),
                visitExpression(binaryNode.right)
        );
    }

    protected ExpressionNode visitBlock(BlockNode blockNode) {
            List<StatementNode> visited = new ArrayList<>();

            for (StatementNode st : blockNode.body) {
                visited.add(visitStatement(st));
            }

            return new BlockNode(visited);

        };

    protected ExpressionNode visitBracketAccess(BracketAccessNode bracketAccessNode) {
        return new BracketAccessNode(
                visitExpression(bracketAccessNode.target),
                visitExpression(bracketAccessNode.key)
        );
    }

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

    protected ExpressionNode visitDotAccess(DotAccessNode dotAccessNode) {
        return new DotAccessNode(visitExpression(dotAccessNode.target), dotAccessNode.key);
    }

    protected ExpressionNode visitLiteral(LiteralNode literalNode) {
        if (literalNode instanceof FnLiteralNode) {
            FnLiteralNode fnLiteralNode = (FnLiteralNode) literalNode;

            return new FnLiteralNode(
                    (FnTypeLiteralNode) visitExpression(fnLiteralNode.typeExp),
                    fnLiteralNode.parameterNames,
                    (BlockNode) visitExpression(fnLiteralNode.body)
            );
        } else if (literalNode instanceof StructLiteralNode) {
            StructLiteralNode structLiteralNode = (StructLiteralNode) literalNode;

            HashMap<String, ExpressionNode> entries = new HashMap<>();

            for (Map.Entry<String, ExpressionNode> e : structLiteralNode.entries.entrySet()) {
                entries.put(e.getKey(), visitExpression(e.getValue()));
            }

            return new StructLiteralNode((StructTypeLiteralNode) visitExpression(structLiteralNode.typeExp), entries);
        } else if (literalNode instanceof TypeLiteralNode) {
            System.out.println("Warning: desugaring for type literal nodes not yet implemented");
            return literalNode;
        } else {
            return literalNode;
        }
    }

    protected ExpressionNode visitTypeof(TypeofNode typeofNode) {
        return new TypeofNode(this.visitExpression(typeofNode.target));
    }

    protected ExpressionNode visitIdentifier(IdentifierNode identifierNode) {
        return identifierNode;
    }

    protected ExpressionNode visitIf(IfNode ifNode) {
        // TODO visit if block
        System.err.println("Not yet implemented.");
        System.exit(1);
        return null;
    }

    protected ExpressionNode visitUnary(UnaryNode unaryNode) {
        return new UnaryNode(unaryNode.operator, this.visitExpression(unaryNode.target));
    }

    protected ExpressionNode visitWhile(WhileNode whileNode) {
        // TODO visit while block
        System.err.println("Not yet implemented.");
        System.exit(1);
        return null;
    }
}
