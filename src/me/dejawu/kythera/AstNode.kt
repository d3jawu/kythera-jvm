package me.dejawu.kythera

import me.dejawu.kythera.stages.lexer.Keyword
import me.dejawu.kythera.stages.lexer.Symbol
import kotlin.text.StringBuilder

fun String.tab(n: Int): String = "\t".repeat(0.coerceAtLeast(n)) + this

abstract class AstNode {
    override fun toString() = toString(0)
    abstract fun toString(indent: Int): String;
}

class AssignNode(val operator: Symbol, val id: String, val exp: AstNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()
        sb.appendLine("AssignNode {".tab(indent))
        sb.appendLine("\top: ${operator.symbol}".tab(indent))
        sb.appendLine("\tid: $id".tab(indent))
        sb.appendLine("\tvalue:".tab(indent))
        sb.appendLine(exp.toString(indent + 2))
        sb.appendLine("} AssignNode")

        return sb.toString()
    }
}

class BlockNode(val body: List<AstNode>) : AstNode() {
    init {
        if (body.isEmpty()) {
            throw Exception("Block cannot have empty body. To return nothing, use `unit`.")
        }
    }

    override fun toString(indent: Int): String {
        val sb = StringBuilder()
        sb.appendLine("BlockNode {".tab(indent))
        sb.appendLine("\tbody:".tab(indent))
        for (st in body) {
            sb.appendLine(st.toString(indent + 2))
        }
        sb.appendLine("} BlockNode".tab(indent))

        return sb.toString()
    }
}

class BinaryNode(val operator: Symbol, val left: AstNode, val right: AstNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("BinaryNode {".tab(indent))
        sb.appendLine("\top: ${operator.symbol}".tab(indent))
        sb.appendLine("\tleft:".tab(indent))
        sb.appendLine(left.toString(indent + 2))
        sb.appendLine("\tright:".tab(indent))
        sb.appendLine(right.toString(indent + 2))
        sb.appendLine("} BinaryNode".tab(indent))


        return sb.toString()
    }
}

object BooleanLiteral {
    // since there are only two boolean values we just pre-generate their
    // nodes and just reuse them
    var TRUE: BooleanLiteralNode = BooleanLiteralNode(true)
    var FALSE: BooleanLiteralNode = BooleanLiteralNode(false)

    class BooleanLiteralNode constructor(val value: Boolean) : AstNode() {
        override fun toString(indent: Int): String {
            val sb = StringBuilder()
            sb.appendLine("BooleanLiteralNode { $value }".tab(indent))
            return sb.toString()
        }
    }

}

class BracketAccessNode(val target: AstNode, val key: AstNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()
        sb.appendLine("BracketAccessNode {".tab(indent))
        sb.appendLine("\ttarget:")
        sb.appendLine(target.toString(indent + 2))
        sb.appendLine("\t key:")
        sb.appendLine(key.toString(indent + 2))
        sb.appendLine("} BracketAccessNode".tab(indent))

        return sb.toString()
    }
}

class CallNode(val target: AstNode, val arguments: List<AstNode>) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("CallNode {".tab(indent))
        sb.appendLine("\ttarget:".tab(indent))
        sb.appendLine(target.toString(indent + 2))
        sb.appendLine("\targuments:".tab(indent))
        var n = 0
        for (ex in arguments) {
            sb.appendLine("\t\targ $n:".tab(indent))
            sb.appendLine(ex.toString(indent + 3))
            n += 1
        }
        sb.appendLine("} CallNode".tab(indent))
        return sb.toString()
    }
}

class DeclarationNode(val identifier: String, val value: AstNode, val op: Keyword) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("DeclarationNode {".tab(indent))
        sb.appendLine("\tidentifier: $identifier".tab(indent))
        sb.appendLine("\top:$op".tab(indent))
        sb.appendLine("\tvalue:".tab(indent))
        sb.appendLine(value.toString(indent + 2))
        sb.appendLine("} DeclarationNode".tab(indent))
        return sb.toString()
    }
}

class DotAccessNode(val target: AstNode, val key: String) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("DotAccessNode {".tab(indent))
        sb.appendLine("\ttarget:".tab(indent))
        target.toString(indent + 2)
        sb.appendLine("\tkey: $key".tab(indent))
        sb.appendLine("} DotAccessNode".tab(indent))
        return sb.toString()
    }
}


class FnLiteralNode(
    val parameterNames: List<String>, val body: BlockNode
) :
    AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("FnLiteralNode {".tab(indent))
        sb.appendLine("\tparameters:".tab(indent))
        var n = 0
        for (param in parameterNames) {
            sb.appendLine("\t\tparam $n: $param".tab(indent))
            n += 1
        }
        sb.appendLine("\tbody:".tab(indent))
        sb.appendLine(body.toString(indent + 2))
        sb.appendLine("} FnLiteralNode".tab(indent))
        return sb.toString()
    }
}

class FnTypeLiteralNode(val parameterTypeExps: List<AstNode>, val returnTypeExp: AstNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("FnTypeLiteralNode {".tab(indent))
        sb.appendLine("\tparams:".tab(indent))
        val n = 0
        for (exp in parameterTypeExps) {
            sb.appendLine("\t\t$n:".tab(indent))
            exp.toString(indent + 2)
        }
        sb.appendLine("\treturn type:".tab(indent))
        sb.appendLine(returnTypeExp.toString(indent + 2))
        sb.appendLine("} FnTypeLiteralNode".tab(indent))
        return sb.toString()
    }
}

class IdentifierNode(val name: String) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("IdentifierNode { name: $name }".tab(indent))
        return sb.toString()
    }
}

class IfNode : AstNode {
    val condition: AstNode
    val body: BlockNode
    val elseBody: AstNode?

    constructor(condition: AstNode, body: BlockNode) {
        this.condition = condition
        this.body = body
        elseBody = null
    }

    constructor(condition: AstNode, body: BlockNode, elseBody: AstNode?) {
        // else body can be either an if or a block
        if (elseBody !is IfNode && elseBody !is BlockNode) {
            throw Exception("'else' must be followed by either a block or " + "an if statement.")
        }
        this.condition = condition
        this.body = body
        this.elseBody = elseBody
    }

    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("IfNode {".tab(indent))
        sb.appendLine("\tcondition:".tab(indent))
        sb.appendLine(condition.toString(indent + 2))
        sb.appendLine("\tbody:".tab(indent))
        sb.appendLine(body.toString(indent + 2))
        if (elseBody != null) {
            sb.appendLine("\telse body:".tab(indent))
            sb.appendLine(elseBody.toString(indent + 2))
        }
        sb.appendLine("} IfNode".tab(indent))
        return sb.toString()
    }
}

class JumpNode(val op: Keyword, val result: AstNode?) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("ReturnNode {".tab(indent))
        sb.appendLine("\top:$op".tab(indent))

        if (result != null) {
            sb.appendLine("\tresult:".tab(indent))
            sb.appendLine(result.toString(indent + 2))

        }

        sb.appendLine("} ReturnNode".tab(indent))
        return sb.toString()
    }
}

class ListLiteralNode(val entries: List<AstNode>) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("ListLiteralNode {".tab(indent))
        sb.appendLine("\tentries:".tab(indent))
        var i = 0
        for (exp in entries) {
            sb.appendLine("\t\t$i:".tab(indent))
            sb.appendLine(exp.toString(indent + 3))
            i += 1
        }
        sb.appendLine("} ListLiteralNode".tab(indent))

        return sb.toString()
    }
}

class StructTypeLiteralNode
    (val entryTypes: Map<String, AstNode>) : AstNode() {

    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("StructTypeLiteralNode {".tab(indent))
        sb.appendLine("\tentries:".tab(indent))
        for(entry in entryTypes) {
            sb.appendLine("\t\t${entry.key}:".tab(indent))
            sb.appendLine(entry.value.toString(indent + 3))
        }
        sb.appendLine("} StructTypeLiteralNode".tab(indent))
        return sb.toString()
    }
}

class DoubleLiteralNode(val value: Double) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()
        sb.appendLine("DoubleLiteralNode { $value }".tab(indent))
        return sb.toString()
    }
}

class IntLiteralNode(val value: Int) : AstNode() {
    override fun toString(indent: Int): String = "IntLiteralNode { $value }".tab(indent)
}

class StrLiteralNode(val value: String) : AstNode() {
    override fun toString(indent: Int): String = "StrLiteralNode { $value }".tab(indent)
}

class StructLiteralNode(val entries: Map<String, AstNode>) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("StructLiteralNode {".tab(indent))
        sb.appendLine("\tentries:".tab(indent))
        for ((key, value) in entries) {
            sb.appendLine("\t\t'$key':".tab(indent))
            sb.appendLine(value.toString(indent + 3))
        }

        sb.appendLine("} StructLiteralNode".tab(indent))
        return sb.toString()
    }
}

class TypeofNode(val target: AstNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("TypeofNode {".tab(indent))
        sb.appendLine("\ttarget:".tab(indent))
        sb.appendLine(target.toString(indent + 2))
        sb.appendLine("} TypeofNode".tab(indent))
        return sb.toString()
    }
}

class UnaryNode(op: Symbol, target: AstNode) : AstNode() {
    val operator: Symbol
    val target: AstNode
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("UnaryNode {".tab(indent))
        sb.appendLine("\top: ${operator.symbol}".tab(indent))
        sb.appendLine("\ttarget".tab(indent))
        sb.appendLine(target.toString(indent + 2))
        sb.appendLine("} UnaryNode".tab(indent))
        return sb.toString()
    }

    init {
        if (op != Symbol.BANG) {
            throw Exception("Invalid operator: " + op.symbol + " cannot be" + " used as a unary operator.")
        }
        operator = op
        this.target = target
    }
}

class WhileNode(val condition: AstNode, val body: BlockNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("WhileNode {".tab(indent))
        sb.appendLine("\t condition:".tab(indent))
        sb.appendLine(condition.toString(indent + 2))
        sb.appendLine("\tbody:".tab(indent))
        sb.appendLine(body.toString(indent + 2))
        sb.appendLine("} WhileNode".tab(indent))

        return sb.toString()
    }
}
