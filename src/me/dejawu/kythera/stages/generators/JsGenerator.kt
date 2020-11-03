package me.dejawu.kythera.stages.generators

import me.dejawu.kythera.BaseType
import me.dejawu.kythera.ast.*
import kotlin.system.exitProcess

class JsGenerator(program: List<StatementNode>) : Generator {
    private val input: List<StatementNode> = program
    private val RUNTIME_VAR_PREFIX = "_KY"

    override fun compile(): ByteArray {
        // initialize runtime
        val out = StringBuilder("import $RUNTIME_VAR_PREFIX from '../runtime/index.js';\n")
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
        is BooleanLiteral.BooleanLiteralNode -> {
            "($RUNTIME_VAR_PREFIX.make.bool(${node.value}))"
        }
        // JS only has one number type, so all numbers map to "Num".
        // unfortunately with the way smart casts work we can't combine these into one case
        is IntLiteralNode -> "($RUNTIME_VAR_PREFIX.make.num(${node.value}))"
        is FloatLiteralNode -> "($RUNTIME_VAR_PREFIX.make.num(${node.value}))"
        is DoubleLiteralNode -> "($RUNTIME_VAR_PREFIX.make.num(${node.value}))"

        is StructLiteralNode -> "($RUNTIME_VAR_PREFIX.make.struct({\n" +
                node.entries.map { "${it.key}: ${visitExpression(it.value)}" }.joinToString(",\n") +
                "}," +
                this.visitExpression(node.typeExp) +
                "))"

        is ListLiteralNode -> "<list literal placeholder>"

        is FnLiteralNode -> "<fn literal placeholder"

        is TypeLiteralNode ->
            when(node.baseType) {
                BaseType.INT, BaseType.FLOAT, BaseType.DOUBLE -> "${RUNTIME_VAR_PREFIX}.consts.NUM"
                else -> {
                    "($RUNTIME_VAR_PREFIX.make.type({" +
                            node.entryTypes.map { "'${it.key}': ${this.visitExpression(it.value)}" }.joinToString(",\n") +
                            "}))"

                }
            }

        else -> {
            System.err.println("Unimplemented literal node: $node")
            exitProcess(1)
        }
    }

    // misused identifiers have been caught at Resolver stage
    private fun visitIdentifier(node: IdentifierNode): String = node.name

    private fun visitIf(node: IfNode): String = "${visitExpression(node.condition)}.value ? ${visitBlock(node.body)} : ${visitExpression(node.elseBody)}"
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
        for (exp in node.arguments) {
            result.append(visitExpression(exp))
            result.append(',')
        }

        result.append(')')

        return result.toString()
    }

    private fun visitTypeof(node: TypeofNode): String = "(${visitExpression(node.target)}.typeValue)"

    private fun visitBlock(node: BlockNode): String {
        val res = StringBuilder("(() => {\n")

        for (st in node.body) {
            res.append(visitStatement(st))
        }

        // TODO use last expression as return value

        res.append("})()")
        return res.toString()
    }

    private fun visitDotAccess(node: DotAccessNode): String = "${visitExpression(node.target)}.fieldValues['${node.key}']"

    private fun visitBracketAccess(node: BracketAccessNode): String = "<Bracket access placeholder>"
}