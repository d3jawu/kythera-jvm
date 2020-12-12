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
            "($RUNTIME_VAR_PREFIX.consts.${node.value.toString().toUpperCase()})"
        }
        // JS only has one number type, so all numbers map to "Num".
        is NumLiteralNode ->
            "($RUNTIME_VAR_PREFIX.make.num(${node.value}))"

        is StructLiteralNode -> "($RUNTIME_VAR_PREFIX.make.struct({\n" +
                node.entries.map { "${it.key}: ${visitExpression(it.value)}" }.joinToString(",\n") +
                "}," +
                this.visitExpression(node.typeExp) +
                "))"

        is ListLiteralNode -> "('list literal placeholder')"

        is FnLiteralNode -> "$RUNTIME_VAR_PREFIX.make.fn(" +
                "(${node.parameterNames.joinToString(",")}) =>" +
                this.visitExpression(node.body) + // block nodes already evaluate to a value, just return that directly
                "," +
                this.visitExpression(node.typeExp) +
                ")"

        is FnTypeLiteralNode -> "('fn type literal node placeholder')"

        is TypeLiteralNode ->
            when (node.baseType) {
                BaseType.NUM -> "${RUNTIME_VAR_PREFIX}.consts.NUM"
                BaseType.BOOL -> "${RUNTIME_VAR_PREFIX}.consts.BOOL"
                BaseType.TYPE -> "${RUNTIME_VAR_PREFIX}.consts.TYPE"
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

    private fun visitIf(node: IfNode): String = "${visitExpression(node.condition)}.value ? ${visitBlock(node.body)} : ${if (node.elseBody != null) visitExpression(node.elseBody) else "(()=>{})()"}"
    private fun visitWhile(node: WhileNode): String = "while(${visitExpression(node.condition)}) {}"
    private fun visitAs(node: AsNode): String = "'as placeholder'"

    private fun visitCall(node: CallNode): String {
        val target = this.visitExpression(node.target)

        if (node.target is DotAccessNode) { // since the expression is used twice we assign it to a temporary __self variable
            val self = this.visitExpression(node.target.target)

            val result = StringBuilder("(() => {")

            result.append("const __self = $self;\n")

            result.append("return __self.fieldValues['${node.target.key}'].value(__self,")

            for (exp in node.arguments) {
                result.append(visitExpression(exp))
                result.append(',')
            }

            result.append(")")

            result.append("})()")

            return result.toString()
        } else {
            val result = StringBuilder("$target.value(")

            // add parameters
            for (exp in node.arguments) {
                result.append(visitExpression(exp))
                result.append(',')
            }

            result.append(')')

            return result.toString();
        }
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