package me.dejawu.kythera.stages

import me.dejawu.kythera.ast.*
import java.util.*
import java.util.stream.Collectors
import kotlin.system.exitProcess

// template class for traversing AST nodes
// S and E are separate to allow extending classes to use StatementNode and ExpressionNode
abstract class Visitor(protected val input: List<StatementNode>) {
    // runs operation on all nodes and returns new AST list
    fun visit(): List<StatementNode> {
        val result: MutableList<StatementNode> = ArrayList()
        for (st in input) {
            result.add(visitStatement(st))
        }
        return result
    }

    protected fun visitStatement(st: StatementNode): StatementNode {
        return when (st.kind) {
            NodeKind.LET -> visitLet(st as LetNode)
            NodeKind.RETURN -> visitReturn(st as ReturnNode)
            else -> visitExpression(st as ExpressionNode)
        }
    }

    protected fun visitExpression(exp: ExpressionNode): ExpressionNode {
        return when (exp.kind) {
            NodeKind.ACCESS -> if (exp is DotAccessNode) {
                visitDotAccess(exp)
            } else {
                visitBracketAccess(exp as BracketAccessNode)
            }
            NodeKind.ASSIGN -> visitAssign(exp as AssignNode)
            NodeKind.BINARY -> visitBinary(exp as BinaryNode)
            NodeKind.BLOCK -> visitBlock(exp as BlockNode)
            NodeKind.CALL -> visitCall(exp as CallNode)
            NodeKind.IDENTIFIER -> visitIdentifier(exp as IdentifierNode)
            NodeKind.IF -> visitIf(exp as IfNode)
            NodeKind.LITERAL -> visitLiteral(exp as LiteralNode)
            NodeKind.TYPEOF -> visitTypeof(exp as TypeofNode)
            NodeKind.UNARY -> visitUnary(exp as UnaryNode)
            NodeKind.WHILE -> visitWhile(exp as WhileNode)
            else -> {
                System.err.println("Invalid or unhandled expression: " + exp.kind)
                exitProcess(1)
            }
        }
    }

    // default implementations pass node on unchanged
    protected open fun visitLet(letNode: LetNode): StatementNode {
        return LetNode(letNode.identifier, visitExpression(letNode.value))
    }

    protected fun visitReturn(returnNode: ReturnNode): StatementNode {
        return ReturnNode(visitExpression(returnNode.exp))
    }

    protected open fun visitAssign(assignNode: AssignNode): ExpressionNode {
        return AssignNode(
                assignNode.operator,
                visitExpression(assignNode.left),
                visitExpression(assignNode.right)
        )
    }

    protected open fun visitBinary(binaryNode: BinaryNode): ExpressionNode {
        return BinaryNode(
                binaryNode.operator,
                visitExpression(binaryNode.left),
                visitExpression(binaryNode.right)
        )
    }

    protected open fun visitBlock(blockNode: BlockNode): ExpressionNode {
        val visited: MutableList<StatementNode> = ArrayList()
        for (st in blockNode.body) {
            visited.add(visitStatement(st))
        }
        return BlockNode(visited)
    }

    protected open fun visitBracketAccess(bracketAccessNode: BracketAccessNode): ExpressionNode {
        return BracketAccessNode(
                visitExpression(bracketAccessNode.target),
                visitExpression(bracketAccessNode.key)
        )
    }

    protected open fun visitCall(callNode: CallNode): ExpressionNode {
        return CallNode(
                visitExpression(callNode.target),
                callNode.arguments
                        .stream()
                        .map { exp: ExpressionNode -> visitExpression(exp) }
                        .collect(Collectors.toList())
        )
    }

    protected open fun visitDotAccess(dotAccessNode: DotAccessNode): ExpressionNode {
        return DotAccessNode(visitExpression(dotAccessNode.target), dotAccessNode.key)
    }

    protected open fun visitLiteral(literalNode: LiteralNode): ExpressionNode {
        return if (literalNode is FnLiteralNode) {
            val fnLiteralNode = literalNode
            FnLiteralNode(
                    visitExpression(fnLiteralNode.typeExp) as FnTypeLiteralNode?,
                    fnLiteralNode.parameterNames,
                    visitExpression(fnLiteralNode.body) as BlockNode?
            )
        } else if (literalNode is StructLiteralNode) {
            val structLiteralNode = literalNode
            val entries = HashMap<String, ExpressionNode>()
            for ((key, value) in structLiteralNode.entries) {
                entries[key] = visitExpression(value)
            }
            StructLiteralNode(visitExpression(structLiteralNode.typeExp) as TypeLiteralNode, entries)
        } else if (literalNode is TypeLiteralNode) {
            println("Warning: visitor for type literal nodes not yet implemented")
            literalNode
        } else {
            literalNode
        }
    }

    protected open fun visitTypeof(typeofNode: TypeofNode): ExpressionNode {
        return TypeofNode(visitExpression(typeofNode.target))
    }

    protected open fun visitIdentifier(identifierNode: IdentifierNode): ExpressionNode {
        return identifierNode
    }

    protected fun visitIf(ifNode: IfNode): ExpressionNode {
        return if (ifNode.elseBody == null) {
            IfNode(
                    visitExpression(ifNode.condition),
                    visitBlock(ifNode.body) as BlockNode?
            )
        } else {
            IfNode(
                    visitExpression(ifNode.condition),
                    visitBlock(ifNode.body) as BlockNode?,
                    visitExpression(ifNode.elseBody)
            )
        }
    }

    protected open fun visitUnary(unaryNode: UnaryNode): ExpressionNode {
        return UnaryNode(unaryNode.operator, visitExpression(unaryNode.target))
    }

    protected open fun visitWhile(whileNode: WhileNode): ExpressionNode {
        // TODO visit while block
        System.err.println("Not yet implemented.")
        exitProcess(1)
    }
}