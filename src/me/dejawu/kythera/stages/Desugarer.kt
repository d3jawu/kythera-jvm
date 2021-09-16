package me.dejawu.kythera.stages

import me.dejawu.kythera.*
import me.dejawu.kythera.stages.lexer.Symbol
import java.util.*

class Desugarer(program: List<AstNode>) : Visitor(program) {
    // TODO desugar "return;" into "return unit;"
    override fun visitAssign(assignNode: AssignNode): AstNode {
        return if (assignNode.operator == Symbol.EQUAL) {
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
    }

    override fun visitUnary(unaryNode: UnaryNode): AstNode {
        return CallNode(
            DotAccessNode(
                visitExpression(unaryNode.target),
                unaryNode.operator.symbol,
            ),
            emptyList<AstNode>()
        )
    }

    // binary infix becomes function call
    override fun visitBinary(binaryNode: BinaryNode): AstNode {
        return CallNode(
            DotAccessNode(
                visitExpression(binaryNode.left),
                binaryNode.operator.symbol
            ),
            object : ArrayList<AstNode>() {
                init {
                    add(
                        visitExpression(binaryNode.right)
                    )
                }
            }
        )
    }

    override fun visitWhile(whileNode: WhileNode): AstNode {
        return WhileNode(
            visitExpression(whileNode.condition),
            visitBlock(whileNode.body) as BlockNode
        )
    }

    // TODO desugar block into fn()unit
    override fun visitBlock(blockNode: BlockNode): AstNode {
        val desugared: MutableList<AstNode> = ArrayList()
        for (st in blockNode.body) {
            desugared.add(visitExpression(st))
        }
        return BlockNode(desugared)
    }

    // TODO desugar into overloaded method call
    override fun visitBracketAccess(bracketAccessNode: BracketAccessNode): AstNode {
        return BracketAccessNode(
            visitExpression(bracketAccessNode.target),
            visitExpression(bracketAccessNode.key)
        )
    }
}