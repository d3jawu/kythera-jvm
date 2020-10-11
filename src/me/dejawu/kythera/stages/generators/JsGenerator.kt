package me.dejawu.kythera.stages.generators

import me.dejawu.kythera.ast.*
import kotlin.system.exitProcess

class JsGenerator(program: List<StatementNode>) : Generator {
    private val out: StringBuilder = StringBuilder()
    private val input: List<StatementNode> = program
    private val RUNTIMEVAR = "_KY"

    override fun compile(): ByteArray {
        // initialize runtime
        out.append("import $RUNTIMEVAR from './runtime/index.js';\n")
        for (st in input) {
            out.append(visitStatement(st))
        }
        return out.toString().toByteArray()
    }

    private fun visitStatement(st: StatementNode): String = when (st.kind) {
        NodeKind.LET -> visitLet(st as LetNode)
        NodeKind.RETURN -> visitReturn(st as ReturnNode)
        else -> visitExpression(st as ExpressionNode) + ";\n"
    }

    // variable conflicts should have been caught by the Resolver stage
    // keep original variable names, let the JS toolchain minify them
    private fun visitLet(node: LetNode): String = "let ${node.identifier} = ${visitExpression(node.value)};\n"

    private fun visitReturn(node: ReturnNode): String = "return ${visitExpression(node.exp)};\n"

    private fun visitExpression(node: ExpressionNode): String = when (node.kind) {
        NodeKind.ASSIGN -> visitAssign(node as AssignNode)
        NodeKind.LITERAL -> visitLiteral(node as LiteralNode)
        NodeKind.IDENTIFIER -> visitIdentifier(node as IdentifierNode)
        NodeKind.IF -> visitIf(node as IfNode)
        NodeKind.WHILE -> visitWhile(node as WhileNode)
        NodeKind.AS -> visitAs(node as AsNode)
        NodeKind.CALL -> visitCall(node as CallNode)
        NodeKind.TYPEOF -> visitTypeof(node as TypeofNode)
        NodeKind.BLOCK -> visitBlock(node as BlockNode)
        NodeKind.ACCESS -> when (node) {
            is DotAccessNode -> visitDotAccess(node)
            is BracketAccessNode -> visitBracketAccess(node)
            else -> {
                System.err.println("Invalid access node: " + node.kind.name)
                exitProcess(1)
            }
        }
        else -> {
            System.err.println("Unsupported or not implemented: " + node.kind.name)
            exitProcess(1)
        }
    }

    // operator has been desugared to always be '='
    private fun visitAssign(node: AssignNode): String = "(${visitExpression(node.left)} = ${visitExpression(node.right)})"

    // TODO optimize by using unwrapped primitives and re-wrapping where needed
    // TODO interning
    private fun visitLiteral(node: LiteralNode): String = when (node) {
        is IntLiteralNode -> "($RUNTIMEVAR.get.int(${node.value}))"
        is StructLiteralNode -> "<struct literal placeholder>"
        is FnLiteralNode -> "<fn literal placeholder"
        is TypeLiteralNode -> "<type literal placeholder>"
        else -> {
            System.err.println("Unimplemented literal node: ${node.kind}")
            exitProcess(1)
        }
    }

    // misused identifiers have been caught at Resolver stage
    private fun visitIdentifier(node: IdentifierNode): String = node.name

    private fun visitIf(node: IfNode): String = "<if placeholder>"
    private fun visitWhile(node: WhileNode): String = "<while placeholder>"
    private fun visitAs(node: AsNode): String = "<as placeholder>"

    private fun visitCall(node: CallNode): String {
        val target = this.visitExpression(node.target)

        val result = StringBuilder("$target.value(")

        // bind "self" variable
        if (node.target is DotAccessNode) {
            // TODO is this okay? If target is an anonymous struct, the instance that the fn is pulled from and the instance used as "self" will not be the same
            result.append(this.visitExpression(node.target.target))
            result.append(',')
        }

        // add parameters
        for(exp in node.arguments) {
            result.append(visitExpression(exp))
            result.append(',')
        }

        result.append(')')

        return result.toString()
    }

    private fun visitTypeof(node: TypeofNode): String = "<typeof placeholder>"
    private fun visitBlock(node: BlockNode): String = "<block node placeholder>"

    private fun visitDotAccess(node: DotAccessNode): String = "${visitExpression(node.target)}.fieldValues['${node.key}']"

    private fun visitBracketAccess(node: BracketAccessNode): String = "<Bracket access placeholder>"
}