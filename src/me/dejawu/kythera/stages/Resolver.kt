package me.dejawu.kythera.stages

import me.dejawu.kythera.ast.*
import me.dejawu.kythera.stages.tokenizer.Symbol
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap
import kotlin.system.exitProcess

// associates types with identifiers, registers parameter variables, resolves struct field types, etc
// TODO resolve and inline constants here too?
class Resolver(input: List<StatementNode>) : Visitor(input) {
    // simple scope concept used to keep track of variable types
    private class Scope {
        val parent: Scope?
        private val symbols = HashMap<String, ExpressionNode>()

        // root scope
        constructor() {
            parent = null
        }

        // child scope
        constructor(parent: Scope?) {
            this.parent = parent
        }

        // Initialize variable, associating it with a type expression. Throws error if already declared (and cannot be overridden by a more local scope).
        fun create(name: String, typeExp: ExpressionNode) {
            if (symbols.containsKey(name)) {
                System.err.println("$name is already bound in this scope.")
                exitProcess(1)
            }
            symbols[name] = typeExp
        }

        // Get type of variable
        operator fun get(name: String): ExpressionNode {
            return if (symbols.containsKey(name)) {
                symbols[name] ?: exitProcess(1)  // HashMap can theoretically contain a null, but never should in practice
            } else {
                if (parent == null) {
                    System.err.println("$name is not defined in this scope.")
                    exitProcess(1)
                } else {
                    parent[name]
                }
            }
        }

        // true if variable is accessible in this scope (including its parents),
        // false otherwise
        fun has(name: String): Boolean {
            return if (symbols.containsKey(name)) {
                true
            } else {
                if (parent == null) {
                    false
                } else {
                    parent.has(name)
                }
            }
        }
    }

    private var scope: Scope?
    override fun visitLet(letNode: LetNode): StatementNode {
        val valueExp = visitExpression(letNode.value)
        scope!!.create(letNode.identifier, valueExp.typeExp)
        return LetNode(
                letNode.identifier,
                valueExp
        )
    }

    override fun visitAssign(assignNode: AssignNode): ExpressionNode {
        if (assignNode.operator != Symbol.EQUALS) {
            System.err.println("Compound assignment should not be present at resolution stage.")
            exitProcess(1)
        }
        return AssignNode(
                Symbol.EQUALS,
                visitExpression(assignNode.left),
                visitExpression(assignNode.right))
    }

    override fun visitBinary(binaryNode: BinaryNode): ExpressionNode {
        System.err.println("Binary expression should not be present at resolution stage")
        exitProcess(1)
    }

    override fun visitBlock(blockNode: BlockNode): ExpressionNode {
        // standalone block should not exist here - it should only appear as a
        // child node of fn literal, if/else, or while
        val returnStatements: MutableList<ReturnNode> = ArrayList()
        val newBody = blockNode.body.stream().map { node: StatementNode ->
            val newNode = visitStatement(node!!)
            if (newNode is ReturnNode) {
                returnStatements.add(newNode)
            }
            newNode
        }.collect(Collectors.toList())
        var typeExp: ExpressionNode? = null
        val lastNode = blockNode.body[blockNode.body.size - 1]
        if (lastNode.kind != NodeKind.RETURN && lastNode !is ExpressionNode) {
            System.err.println("Last statement in block must be a return or an expression.")
            exitProcess(1)
        }

        // actual agreement of return/lastNode types is checked later in the TypeChecker stage
        for (ret in returnStatements) {
            if (typeExp == null) {
                typeExp = ret.exp.typeExp
            }
        }
        if (typeExp == null) {
            // if no return statements, use last expression as value
            typeExp = (lastNode as ExpressionNode).typeExp
        }
        return BlockNode(newBody, typeExp)
    }

    override fun visitBracketAccess(bracketAccessNode: BracketAccessNode): ExpressionNode {
        System.err.println("Bracket access node should not be present at resolution stage.")
        exitProcess(1)
    }

    override fun visitCall(callNode: CallNode): ExpressionNode {
        // look up signature of target fn and attach return type expression to node
        val target = visitExpression(callNode.target)
        val arguments = callNode.arguments
                .stream()
                .map { arg: ExpressionNode -> visitExpression(arg) }
                .collect(Collectors.toList())

        // TODO this assertion is no longer true once type values come into play
        // assert target.typeExp instanceof FnTypeLiteralNode
        val typeExp = (target.typeExp as FnTypeLiteralNode).returnTypeExp

        return CallNode(
                target,
                arguments,
                typeExp
        )
    }

    override fun visitDotAccess(dotAccessNode: DotAccessNode): ExpressionNode {
        // look up fields of target struct and attach type expression
        val target = visitExpression(dotAccessNode.target)

        // TODO this assertion is no longer true once type values come into play
        // assert target.typeExp instanceof StructTypeLiteralNode

        val typeExp = visitExpression(
                (target.typeExp as TypeLiteralNode).entryTypes[dotAccessNode.key]!!
        )
        return DotAccessNode(
                target,
                dotAccessNode.key,
                typeExp
        )
    }

    override fun visitLiteral(literalNode: LiteralNode): ExpressionNode {
        return when (literalNode) {
            is StructLiteralNode -> {
                val resolvedEntries = HashMap<String, ExpressionNode>()
                val resolvedTypes = HashMap<String, ExpressionNode>()

                for((key, exp) in literalNode.entries) {
                    val entry = this.visitExpression(exp)
                    // at this stage, type exp for the expression must have been populated
                    resolvedEntries[key] = entry
                    resolvedTypes[key] = entry.typeExp
                }

                StructLiteralNode(
                        TypeLiteralNode(resolvedTypes),
                        resolvedEntries
                )
            }
            is FnLiteralNode -> {
                scope = Scope(scope)

                // each literal should have been associated with a type literal
                // assert fnLiteralNode.typeExp instanceof FnTypeLiteralNode
                val paramTypes = ArrayList<ExpressionNode>()
                var n = 0
                while (n < literalNode.parameterNames.size) {
                    val paramTypeExp = visitExpression(
                            (literalNode.typeExp as FnTypeLiteralNode).parameterTypeExps[n]
                    )
                    paramTypes.add(paramTypeExp)
                    scope!!.create(
                            literalNode.parameterNames[n],
                            paramTypeExp
                    )
                    n += 1
                }
                val body = visitBlock(literalNode.body) as BlockNode

                // assert body.typeExp != null
                scope = scope!!.parent
                FnLiteralNode(
                        FnTypeLiteralNode(paramTypes, body!!.typeExp),
                        literalNode.parameterNames,
                        body)
            }
            is TypeLiteralNode -> {
                if (literalNode is FnTypeLiteralNode) {
                    val paramTypeExps = literalNode.parameterTypeExps
                            .stream()
                            .map { exp: ExpressionNode -> visitExpression(exp!!) }
                            .collect(Collectors.toList())
                    return FnTypeLiteralNode(
                            paramTypeExps,
                            visitExpression(literalNode.returnTypeExp)
                    )
                }
                literalNode
            }
            else -> {
                // other literalNodes can just pass through
                literalNode
            }
        }
    }

    override fun visitIdentifier(identifierNode: IdentifierNode): ExpressionNode {
//        assert identifierNode != null;
        return IdentifierNode(identifierNode.name, scope!![identifierNode.name])
    }

    /*
    @Override
    protected ExpressionNode visitIf(IfNode ifNode) {
        // evaluate if/else bodies and attach types
        System.err.println("if/else is not yet implemented at the Resolver stage");
        exitProcess(1);
        return null;
    }
     */
    override fun visitUnary(unaryNode: UnaryNode): ExpressionNode {
        System.err.println("Unary node should not be present at Resolver stage.")
        exitProcess(1)
    }

    override fun visitWhile(whileNode: WhileNode): ExpressionNode {
        // evaluate body and attach type
        System.err.println("while is not yet implemented at the Resolver stage")
        exitProcess(1)
    }

    init {
        scope = Scope()
    }
}