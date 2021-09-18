package me.dejawu.kythera.stages

import me.dejawu.kythera.*
import me.dejawu.kythera.stages.lexer.Symbol
import java.util.*
import java.util.stream.Collectors

class Desugarer(private val program: List<AstNode>) {
    // runs operation on all nodes and returns new AST list
    fun visit(): List<AstNode> {
        val result: MutableList<AstNode> = ArrayList()
        for (exp in program) {
            result.add(visitExpression(exp))
        }
        return result
    }

    private fun visitExpression(exp: AstNode): AstNode {
        return when (exp) {
            is DeclarationNode -> visitDeclaration(exp)
            is JumpNode -> visitJump(exp)
            is DotAccessNode -> visitDotAccess(exp)
            is BracketAccessNode -> visitBracketAccess(exp)
            is AssignNode -> visitAssign(exp)
            is BinaryNode -> visitBinary(exp)
            is BlockNode -> visitBlock(exp)
            is CallNode -> visitCall(exp)
            is IdentifierNode -> visitIdentifier(exp)
            is IfNode -> visitIf(exp)
            is TypeofNode -> visitTypeof(exp)
            is UnaryNode -> visitUnary(exp)
            is WhileNode -> visitWhile(exp)
            is FnLiteralNode -> visitFnLiteralNode(exp)
            is StructLiteralNode -> visitStructLiteralNode(exp)
            is StructTypeLiteralNode -> visitStructTypeLiteralNode(exp)
            // primitive literals are terminal
            is IntLiteralNode -> exp
            is DoubleLiteralNode -> exp
            else -> {
                TODO("Unimplemented expression: $exp")
            }
        }
    }

    private fun visitStructTypeLiteralNode(structTypeLiteralNode: StructTypeLiteralNode) =
        StructTypeLiteralNode(structTypeLiteralNode.entryTypes.mapValues { visitExpression(it.value) })

    private fun visitStructLiteralNode(structLiteralNode: StructLiteralNode) =
        StructLiteralNode(structLiteralNode.entries.mapValues { visitExpression(it.value) })

    private fun visitFnLiteralNode(fnLiteralNode: FnLiteralNode) =
        FnLiteralNode(fnLiteralNode.parameterNames, visitBlock(fnLiteralNode.body))

    private fun visitFnTypeLiteralNode(fnTypeLiteralNode: FnTypeLiteralNode) =
        FnTypeLiteralNode(
            fnTypeLiteralNode.parameterTypeExps.map { visitExpression(it) },
            visitExpression(fnTypeLiteralNode.returnTypeExp)
        )

    private fun visitTypeof(typeofNode: TypeofNode): TypeofNode = TypeofNode(visitExpression(typeofNode.target))

    private fun visitIdentifier(identifierNode: IdentifierNode) = identifierNode

    private fun visitCall(callNode: CallNode) = CallNode(
        visitExpression(callNode.target),
        callNode.arguments
            .stream()
            .map { exp: AstNode -> visitExpression(exp) }
            .collect(Collectors.toList())
    )

    // default implementations pass node on unchanged
    private fun visitDeclaration(declarationNode: DeclarationNode) =
        DeclarationNode(declarationNode.identifier, visitExpression(declarationNode.value), declarationNode.op)

    private fun visitJump(jumpNode: JumpNode) = if (jumpNode.result == null) {
        JumpNode(jumpNode.op, null)
    } else {
        JumpNode(jumpNode.op, visitExpression(jumpNode.result))
    }

    private fun visitAssign(assignNode: AssignNode) =
        if (assignNode.operator == Symbol.EQUAL) {
            AssignNode(
                Symbol.EQUAL,
                assignNode.id,
                visitExpression(assignNode.exp)
            )
        } else {
            // separate assignment, e.g. x += 10 becomes x = (x + 10)
            AssignNode(
                Symbol.EQUAL,
                assignNode.id,
                CallNode(
                    DotAccessNode(
                        IdentifierNode(assignNode.id),
                        "" + assignNode.operator.symbol
                    ),
                    arrayListOf(
                        visitExpression(assignNode.exp)
                    )
                )
            )
        }

    private fun visitUnary(unaryNode: UnaryNode) = CallNode(
        DotAccessNode(
            visitExpression(unaryNode.target),
            unaryNode.operator.symbol,
        ),
        emptyList()
    )

    // binary infix becomes function call
    private fun visitBinary(binaryNode: BinaryNode) = CallNode(
        DotAccessNode(
            visitExpression(binaryNode.left),
            binaryNode.operator.symbol
        ),
        arrayListOf(visitExpression(binaryNode.right))
    )

    private fun visitIf(ifNode: IfNode) = if (ifNode.elseBody == null) {
        IfNode(
            visitExpression(ifNode.condition),
            visitBlock(ifNode.body)
        )
    } else {
        IfNode(
            visitExpression(ifNode.condition),
            visitBlock(ifNode.body),
            visitExpression(ifNode.elseBody)
        )
    }

    private fun visitWhile(whileNode: WhileNode) = WhileNode(
        visitExpression(whileNode.condition),
        visitBlock(whileNode.body)
    )

    private fun visitBlock(blockNode: BlockNode): BlockNode {
        val desugared: MutableList<AstNode> = ArrayList()
        for (st in blockNode.body) {
            desugared.add(visitExpression(st))
        }
        return BlockNode(desugared)
    }

    private fun visitBracketAccess(bracketAccessNode: BracketAccessNode) = CallNode(
        visitExpression(bracketAccessNode.target),
        arrayListOf(visitExpression(bracketAccessNode.key))
    )

    private fun visitDotAccess(dotAccessNode: DotAccessNode): DotAccessNode =
        DotAccessNode(visitExpression(dotAccessNode.target), dotAccessNode.key)
}