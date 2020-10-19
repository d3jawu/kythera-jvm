package me.dejawu.kythera.stages

import me.dejawu.kythera.ast.*
import me.dejawu.kythera.stages.tokenizer.Symbol
import java.util.*

class Desugarer(program: List<StatementNode>) : Visitor(program) {
    // TODO desugar "return;" into "return unit;"
    override fun visitAssign(assignNode: AssignNode): ExpressionNode {
        return if (assignNode.operator == Symbol.EQUALS) {
            AssignNode(Symbol.EQUALS,
                    visitExpression(assignNode.left),
                    visitExpression(assignNode.right)
            )
        } else {
            // separate assignment, e.g. x += 10 becomes x = (x + 10)
            AssignNode(
                    Symbol.EQUALS,
                    visitExpression(assignNode.left),
                    CallNode(
                            DotAccessNode(
                                    visitExpression(assignNode.left),
                                    "" + assignNode.operator.symbol[0]),
                            object : ArrayList<ExpressionNode>() {
                                init {
                                    add(
                                            visitExpression(assignNode.right)
                                    )
                                }
                            }
                    )
            )
        }
    }

    // TODO desugar unary into not()
    // binary infix becomes function call
    override fun visitBinary(binaryNode: BinaryNode): ExpressionNode {
        return CallNode(
                DotAccessNode(
                        visitExpression(binaryNode.left),
                        binaryNode.operator.symbol),
                object : ArrayList<ExpressionNode>() {
                    init {
                        add(
                                visitExpression(binaryNode.right)
                        )
                    }
                }
        )
    }

    // TODO desugar block into fn()unit
    override fun visitBlock(blockNode: BlockNode): ExpressionNode {
        val desugared: MutableList<StatementNode> = ArrayList()
        for (st in blockNode.body) {
            desugared.add(visitStatement(st))
        }
        return BlockNode(desugared)
    }

    // TODO desugar into overloaded method call
    override fun visitBracketAccess(bracketAccessNode: BracketAccessNode): ExpressionNode {
        return BracketAccessNode(
                visitExpression(bracketAccessNode.target),
                visitExpression(bracketAccessNode.key)
        )
    }
}