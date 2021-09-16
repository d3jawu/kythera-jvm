package me.dejawu.kythera.stages

import me.dejawu.kythera.*
import me.dejawu.kythera.AstNode.*
import java.util.*
import java.util.stream.Collectors
import kotlin.system.exitProcess

// template class for traversing AST nodes
abstract class Visitor(protected val input: List<AstNode>) {
    // runs operation on all nodes and returns new AST list
    fun visit(): List<AstNode> {
        val result: MutableList<AstNode> = ArrayList()
        for (st in input) {
            result.add(visitExpression(st))
        }
        return result
    }

    protected fun visitExpression(exp: AstNode): AstNode {
        return when (exp) {
            is DeclarationNode -> visitDeclaration(exp)
            is JumpNode -> visitJump(exp)
            is DotAccessNode -> visitDotAccess(exp)
            is BracketAccessNode ->visitBracketAccess(exp)
            is AssignNode -> visitAssign(exp)
            is BinaryNode -> visitBinary(exp)
            is BlockNode -> visitBlock(exp)
            is CallNode -> visitCall(exp)
            is IdentifierNode -> visitIdentifier(exp)
            is IfNode -> visitIf(exp)
//            NodeKind.LITERAL -> visitLiteral(exp)
            is TypeofNode -> visitTypeof(exp)
            is UnaryNode -> visitUnary(exp)
            is WhileNode -> visitWhile(exp)
            else -> {
                throw Exception("Invalid or unhandled expression: $exp")
            }
        }
    }

    // default implementations pass node on unchanged
    protected open fun visitDeclaration(declarationNode: DeclarationNode): AstNode =
        DeclarationNode(declarationNode.identifier, visitExpression(declarationNode.value), declarationNode.op)

    protected open fun visitJump(jumpNode: JumpNode): AstNode {
        return if(jumpNode.result == null) {
            JumpNode(jumpNode.op, null)
        } else {
            JumpNode(jumpNode.op, visitExpression(jumpNode.result))
        }
    }

    protected open fun visitAssign(assignNode: AssignNode): AstNode {
        return AssignNode(
            assignNode.operator,
            assignNode.id,
            visitExpression(assignNode.exp)
        )
    }

    protected open fun visitBinary(binaryNode: BinaryNode): AstNode {
        return BinaryNode(
            binaryNode.operator,
            visitExpression(binaryNode.left),
            visitExpression(binaryNode.right)
        )
    }

    protected open fun visitBlock(blockNode: BlockNode): AstNode {
        val visited: MutableList<AstNode> = ArrayList()
        for (st in blockNode.body) {
            visited.add(visitExpression(st))
        }
        return BlockNode(visited)
    }

    protected open fun visitBracketAccess(bracketAccessNode: BracketAccessNode): AstNode {
        return BracketAccessNode(
            visitExpression(bracketAccessNode.target),
            visitExpression(bracketAccessNode.key)
        )
    }

    protected open fun visitCall(callNode: CallNode): AstNode {
        return CallNode(
            visitExpression(callNode.target),
            callNode.arguments
                .stream()
                .map { exp: AstNode -> visitExpression(exp) }
                .collect(Collectors.toList())
        )
    }

    protected open fun visitDotAccess(dotAccessNode: DotAccessNode): AstNode {
        return DotAccessNode(visitExpression(dotAccessNode.target), dotAccessNode.key)
    }

    /*
    protected open fun visitLiteral(literalNode: LiteralNode): AstNode {
        return if (literalNode is FnLiteralNode) {
            val fnLiteralNode = literalNode
            FnLiteralNode(
                visitExpression(fnLiteralNode.typeExp) as FnTypeLiteralNode?,
                fnLiteralNode.parameterNames,
                visitExpression(fnLiteralNode.body) as BlockNode?
            )
        } else if (literalNode is StructLiteralNode) {
            val structLiteralNode = literalNode
            val entries = HashMap<String, AstNode>()
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
     */

    protected open fun visitTypeof(typeofNode: TypeofNode): AstNode {
        return TypeofNode(visitExpression(typeofNode.target))
    }

    protected open fun visitIdentifier(identifierNode: IdentifierNode): AstNode {
        return identifierNode
    }

    protected fun visitIf(ifNode: IfNode): AstNode {
        return if (ifNode.elseBody == null) {
            IfNode(
                visitExpression(ifNode.condition),
                visitBlock(ifNode.body) as BlockNode
            )
        } else {
            IfNode(
                visitExpression(ifNode.condition),
                visitBlock(ifNode.body) as BlockNode,
                visitExpression(ifNode.elseBody)
            )
        }
    }

    protected open fun visitUnary(unaryNode: UnaryNode): AstNode {
        return UnaryNode(unaryNode.operator, visitExpression(unaryNode.target))
    }

    protected open fun visitWhile(whileNode: WhileNode): AstNode {
        // TODO visit while block
        System.err.println("Not yet implemented.")
        exitProcess(1)
    }
}