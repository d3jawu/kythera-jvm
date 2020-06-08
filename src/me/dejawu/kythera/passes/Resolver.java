package me.dejawu.kythera.passes;

import me.dejawu.kythera.ast.*;
import me.dejawu.kythera.passes.tokenizer.Symbol;

import java.util.HashMap;
import java.util.List;

// associates types with identifiers, registers parameter variables, resolves struct field types, etc
public class Resolver extends Visitor<StatementNode, ExpressionNode> {
    // simple scope concept used to keep track of variable types
    private static class Scope {

        public final Scope parent;
        private final HashMap<String, ExpressionNode> symbols = new HashMap<>();

        // root scope
        public Scope() {
            this.parent = null;
        }

        // child scope
        public Scope(Scope parent) {
            this.parent = parent;
        }

        // Initialize variable, associating it with a type expression. Throws error if already declared (and cannot be overridden by a more local scope).
        public void create(String name, ExpressionNode typeExp) {
            if (this.symbols.containsKey(name)) {
                System.err.println(name + " is already bound in this scope.");
                System.exit(1);
            }

            this.symbols.put(name, typeExp);
        }

        // Get type of variable
        public ExpressionNode get(String name) {
            if (this.symbols.containsKey(name)) {
                return this.symbols.get(name);
            } else {
                if (this.parent == null) {
                    System.err.println(name + " is not defined in this scope.");
                    System.exit(1);
                    return null;
                } else {
                    return this.parent.get(name);
                }
            }
        }

        // true if variable is accessible in this scope (including its parents),
        // false otherwise
        public boolean has(String name) {
            if (symbols.containsKey(name)) {
                return true;
            } else {
                if (this.parent == null) {
                    return false;
                } else {
                    return this.parent.has(name);
                }
            }
        }
    }

    private Scope scope;

    public Resolver(List<StatementNode> input) {
        super(input);
        this.scope = new Scope();
    }

    @Override
    protected StatementNode visitLet(LetNode letNode) {
        ExpressionNode valueExp = visitExpression(letNode.value);
        this.scope.create(letNode.identifier, valueExp.typeExp);
        return new LetNode(
                letNode.identifier,
                valueExp
        );
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
        if(!assignNode.operator.equals(Symbol.EQUALS)) {
            System.err.println("Compound assignment should not be present at resolution stage.");
            System.exit(0);
        }

        return new AssignNode(
                Symbol.EQUALS,
                visitExpression(assignNode.left),
                visitExpression(assignNode.right));
    }

    @Override
    protected ExpressionNode visitBinary(BinaryNode binaryNode) {
        System.err.println("Binary expression should not be present at resolution stage");
        System.exit(0);
        return null;
    }

    @Override
    protected ExpressionNode visitBlock(BlockNode blockNode) {
        System.err.println("Standalone block node should not be present at resolution stage.");
        System.exit(0);
        return null;
    }

    @Override
    protected ExpressionNode visitBracketAccess(BracketAccessNode bracketAccessNode) {
        System.err.println("Bracket access node should not be present at resolution stage.");
        return null;
    }

    @Override
    protected ExpressionNode visitCall(CallNode callNode) {
        // look up signature of target fn and attach return type expression to node
        return null;
    }

    @Override
    protected ExpressionNode visitDotAccess(DotAccessNode dotAccessNode) {
        // look up fields of target struct and attach type expression
        return null;
    }

    @Override
    protected ExpressionNode visitLiteral(LiteralNode literalNode) {
        // fn literal: detect block return type
        // fn literal: detect method type in parent struct type

        /*
        typeExp = null; // for BlockNodes, typeExp is the return type
        for (StatementNode st : returnStatements) {
            if (st.kind == NodeKind.RETURN) {
                ReturnNode ret = (ReturnNode) st;

                if (typeExp == null) {
                    typeExp = ret.exp.typeExp;
                }
            }
        }

        if (typeExp == null && lastNode.kind != NodeKind.RETURN) {
            // if no return statements, use last expression as value
            this.typeExp = ((ExpressionNode) lastNode).typeExp;
        }
        */

            return null;
    }

    @Override
    protected ExpressionNode visitTypeof(TypeofNode typeofNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitIdentifier(IdentifierNode identifierNode) {
        return new IdentifierNode(identifierNode.name, this.scope.get(identifierNode.name));
    }

    @Override
    protected ExpressionNode visitIf(IfNode ifNode) {
        // evaluate if/else bodies and attach types
        return null;
    }

    @Override
    protected ExpressionNode visitUnary(UnaryNode unaryNode) {
        return null;
    }

    @Override
    protected ExpressionNode visitWhile(WhileNode whileNode) {
        // evaluate body and attach type
        return null;
    }
}
