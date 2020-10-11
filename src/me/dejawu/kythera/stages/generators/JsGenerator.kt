package me.dejawu.kythera.stages.generators

import me.dejawu.kythera.ast.*
import java.util.*

class JsGenerator(program: List<StatementNode>) : Generator {
    private val out: StringBuilder
    private val input: List<StatementNode>
    private val RUNTIME_VAR = "_KYTHERA"

    init {
        out = StringBuilder()
        input = program
    }

    // keeps track of variable slots
    private class SymbolTable : HashMap<String?, Int?> {
        val parent: SymbolTable?
        private val out: StringBuilder

        // root scope (no parent)
        constructor(out: StringBuilder) {
            parent = null
            this.out = out
        }

        // scope with parent
        constructor(parent: SymbolTable?, out: StringBuilder) {
            this.parent = parent
            this.out = out
        }

        // find the next available variable name and
        fun addSymbol(name: String?) {
            // offset scope variables by 1 to leave room for parameter variable (KytheraValue[])
            val slot = this.size + 1
            this[name] = slot
        }

        // generates instructions that will push the given (existing) symbol
        // on the stack
        fun loadSymbol(name: String?) {
            val slot = this[name]!!
        }

        // generates instructions that will store the variable at the
        // top of the stack into the slot for the given (existing) symbol
        fun storeSymbol(name: String?) {
            val slot = this[name]!!
        }
    }

    override fun compile(): ByteArray {
        // initialize runtime
        out.append("const $RUNTIME_VAR = require('./runtime');")
        for (st in input) {
            visitStatement(st)
        }
        return out.toString().toByteArray()
    }

    private fun visitStatement(st: StatementNode) {
        when (st.kind) {
            NodeKind.LET -> visitLet(st as LetNode)
            NodeKind.RETURN -> visitReturn(st as ReturnNode)
            else -> visitExpression(st as ExpressionNode)
        }
    }

    private fun visitLet(node: LetNode) {}
    private fun visitReturn(node: ReturnNode) {}
    private fun visitExpression(node: ExpressionNode) {
        when (node.kind) {
            NodeKind.ASSIGN -> {
                visitAssign(node as AssignNode)
                return
            }
            NodeKind.LITERAL -> {
                visitLiteral(node as LiteralNode)
                return
            }
            NodeKind.IDENTIFIER -> {
                visitIdentifier(node as IdentifierNode)
                return
            }
            NodeKind.IF -> {
                visitIf(node as IfNode)
                return
            }
            NodeKind.WHILE -> {
                visitWhile(node as WhileNode)
                return
            }
            NodeKind.AS -> {
                visitAs(node as AsNode)
                return
            }
            NodeKind.CALL -> {
                visitCall(node as CallNode)
                return
            }
            NodeKind.TYPEOF -> {
                visitTypeof(node as TypeofNode)
                return
            }
            NodeKind.BLOCK -> {
                visitBlock(node as BlockNode)
                return
            }
            NodeKind.UNARY -> {
                visitUnary(node as UnaryNode)
                return
            }
            NodeKind.BINARY -> {
                visitBinary(node as BinaryNode)
                return
            }
            NodeKind.ACCESS -> if (node is DotAccessNode) {
                visitDotAccess(node)
                return
            } else if (node is BracketAccessNode) {
                visitBracketAccess(node)
                return
            } else {
                System.err.println("Invalid access node: " + node.kind.name)
                System.exit(1)
            }
            else -> {
            }
        }
        System.err.println("Unsupported or not implemented: " + node.kind.name)
        System.exit(1)
    }

    private fun visitAssign(node: AssignNode) {}
    private fun visitLiteral(node: LiteralNode) {}
    private fun visitIdentifier(node: IdentifierNode) {}
    private fun visitIf(node: IfNode) {}
    private fun visitWhile(node: WhileNode) {}
    private fun visitAs(node: AsNode) {}
    private fun visitCall(node: CallNode) {}
    private fun visitTypeof(node: TypeofNode) {}
    private fun visitBlock(node: BlockNode) {}
    private fun visitUnary(node: UnaryNode) {}
    private fun visitBinary(node: BinaryNode) {}
    private fun visitDotAccess(node: DotAccessNode) {}
    private fun visitBracketAccess(node: BracketAccessNode) {}
}