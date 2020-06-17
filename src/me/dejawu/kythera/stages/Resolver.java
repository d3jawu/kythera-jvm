package me.dejawu.kythera.stages;

import me.dejawu.kythera.ast.*;
import me.dejawu.kythera.stages.tokenizer.Symbol;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// associates types with identifiers, registers parameter variables, resolves struct field types, etc
// TODO resolve and inline constants here too?
public class Resolver extends Visitor {
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
        // standalone block should not exist here - it should only appear as a
        // child node of fn literal, if/else, or while

        List<ReturnNode> returnStatements = new ArrayList<>();

        List<StatementNode> newBody = blockNode.body.stream().map((node) -> {
            StatementNode newNode = this.visitStatement(node);

            if (newNode instanceof ReturnNode) {
                returnStatements.add((ReturnNode) newNode);
            }

            return newNode;
        }).collect(Collectors.toList());

        ExpressionNode typeExp = null;

        StatementNode lastNode = blockNode.body.get(blockNode.body.size() - 1);

        if (lastNode.kind != NodeKind.RETURN && !(lastNode instanceof ExpressionNode)) {
            System.err.println("Last statement in block must be a return or an expression.");
            System.exit(1);
            return null;
        }

        // actual agreement of return/lastNode types is checked later in the TypeChecker stage

        for (ReturnNode ret: returnStatements) {
            if (typeExp == null) {
                typeExp = ret.exp.typeExp;
            }
        }

        if (typeExp == null) {
            // if no return statements, use last expression as value
            typeExp = ((ExpressionNode) lastNode).typeExp;
        }

        return new BlockNode(newBody, typeExp);
    }

    @Override
    protected ExpressionNode visitBracketAccess(BracketAccessNode bracketAccessNode) {
        System.err.println("Bracket access node should not be present at resolution stage.");
        return null;
    }

    @Override
    protected ExpressionNode visitCall(CallNode callNode) {
        // look up signature of target fn and attach return type expression to node
        ExpressionNode target = this.visitExpression(callNode.target);
        List<ExpressionNode> arguments =
                callNode
                        .arguments
                        .stream()
                        .map(arg -> this.visitExpression(arg))
                        .collect(Collectors.toList());

        // TODO this assertion is no longer true once type values come into play
        // assert target.typeExp instanceof FnTypeLiteralNode
        ExpressionNode typeExp =
            ((FnTypeLiteralNode) target.typeExp)
                .returnTypeExp;

        System.out.println("Call node");
        System.out.println(target);
//        target.print(0, System.out);
//        typeExp.print(0, System.out);

        return new CallNode(
            target,
            arguments,
            typeExp
        );
    }

    @Override
    protected ExpressionNode visitDotAccess(DotAccessNode dotAccessNode) {
        // look up fields of target struct and attach type expression
        ExpressionNode target = this.visitExpression(dotAccessNode.target);

        System.out.println("DotNode target:");
        System.out.println(target.typeExp);

        // TODO this assertion is no longer true once type values come into play
        // assert target.typeExp instanceof StructTypeLiteralNode

        // TODO define fields for builtin types, eg int.+ (this may take some thinking)

        target.typeExp.print(0, System.out);
        System.out.println(dotAccessNode.key);
        System.out.println(((TypeLiteralNode) target.typeExp)
            .entryTypes
            .get(dotAccessNode.key));

        ExpressionNode typeExp = this.visitExpression(
            ((TypeLiteralNode) target.typeExp)
                .entryTypes
                .get(dotAccessNode.key)
        );

        return new DotAccessNode(
            target,
            dotAccessNode.key,
            typeExp
        );
    }

    @Override
    protected ExpressionNode visitLiteral(LiteralNode literalNode) {
        System.out.println("Literal node:");
        literalNode.print(0, System.out);

        if (literalNode instanceof StructLiteralNode) {
            System.err.println("Struct literal node not yet implemented in resolver");
            System.exit(0);
            return null;
        } else if (literalNode instanceof FnLiteralNode) {
            FnLiteralNode fnLiteralNode = (FnLiteralNode) literalNode;

            this.scope = new Scope(this.scope);

            // each literal should have been associated with a type literal
            // assert fnLiteralNode.typeExp instanceof FnTypeLiteralNode

            ArrayList<ExpressionNode> paramTypes = new ArrayList<>();

            for (int n = 0; n < fnLiteralNode.parameterNames.size(); n += 1) {
                ExpressionNode paramTypeExp =  this.visitExpression(
                        ((FnTypeLiteralNode) fnLiteralNode.typeExp)
                                .parameterTypeExps.get(n)
                );
                paramTypes.add(paramTypeExp);
                this.scope.create(
                        fnLiteralNode.parameterNames.get(n),
                        paramTypeExp
                );
            }

            BlockNode body = (BlockNode) this.visitBlock(fnLiteralNode.body);

            // assert body.typeExp != null

            this.scope = this.scope.parent;

            return new FnLiteralNode(
                    new FnTypeLiteralNode(paramTypes, body.typeExp),
                    fnLiteralNode.parameterNames,
                    body);
        } else if (literalNode instanceof TypeLiteralNode) {
            TypeLiteralNode typeLiteralNode = (TypeLiteralNode) literalNode;

            if(typeLiteralNode instanceof FnTypeLiteralNode) {
                FnTypeLiteralNode fnTypeLiteralNode = (FnTypeLiteralNode) typeLiteralNode;

                List<ExpressionNode> paramTypeExps = fnTypeLiteralNode
                        .parameterTypeExps
                        .stream()
                        .map(exp -> this.visitExpression(exp))
                        .collect(Collectors.toList());

                return new FnTypeLiteralNode(
                        paramTypeExps,
                        this.visitExpression(fnTypeLiteralNode.returnTypeExp)
                );
            }
            // other TypeLiteralNodes can just pass through

            return literalNode;

//
//            System.err.println("Not yet implemented in resolver:");
//            typeLiteralNode.print(0, System.err);
//            System.exit(1);
//            return null;
        } else {
            return literalNode;
        }
    }

    @Override
    protected ExpressionNode visitIdentifier(IdentifierNode identifierNode) {
//        assert identifierNode != null;
        return new IdentifierNode(identifierNode.name, this.scope.get(identifierNode.name));
    }

    @Override
    protected ExpressionNode visitIf(IfNode ifNode) {
        // evaluate if/else bodies and attach types
        System.err.println("if/else is not yet implemented at the Resolver stage");
        System.exit(1);
        return null;
    }

    @Override
    protected ExpressionNode visitUnary(UnaryNode unaryNode) {
        System.err.println("Unary node should not be present at Resolver stage.");
        System.exit(1);
        return null;
    }

    @Override
    protected ExpressionNode visitWhile(WhileNode whileNode) {
        // evaluate body and attach type
        System.err.println("while is not yet implemented at the Resolver stage");
        System.exit(1);
        return null;
    }
}
