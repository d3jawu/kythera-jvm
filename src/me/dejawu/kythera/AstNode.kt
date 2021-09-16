package me.dejawu.kythera

import me.dejawu.kythera.stages.lexer.Keyword
import me.dejawu.kythera.stages.lexer.Symbol
import kotlin.text.StringBuilder

fun String.tab(n: Int): String = "\t".repeat(0.coerceAtLeast(n)) + this

abstract class AstNode {
    override fun toString() = toString(0)
    abstract fun toString(indent: Int): String;
}

class AssignNode(val op: Symbol, val id: String, val value: AstNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()
        sb.appendLine("AssignNode {".tab(indent))
        sb.appendLine("\top: ${op.symbol}".tab(indent))
        sb.appendLine("\tid: $id".tab(indent))
        sb.appendLine("\tvalue:".tab(indent))
        sb.appendLine(value.toString(indent + 2))
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
        sb.appendLine(left.toString(indent + 1))
        sb.appendLine("\tright:".tab(indent))
        sb.appendLine(right.toString(indent + 1))
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

class CallNode : AstNode {
    val target: AstNode
    val arguments: List<AstNode>

    constructor(target: AstNode, arguments: List<AstNode>) {
        this.target = target
        this.arguments = arguments
    }

    // called by Resolver when return type is known
    constructor(target: AstNode, arguments: List<AstNode>, typeExp: AstNode?) {
        this.target = target
        this.arguments = arguments
    }

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
        sb.appendLine(value.toString(indent + 1))
        sb.appendLine("} LetNode".tab(indent))
        return sb.toString()
    }
}

class DotAccessNode : AstNode {
    val target: AstNode
    val key: String

    constructor(target: AstNode, key: String) {
        this.target = target
        this.key = key
    }

    constructor(target: AstNode, key: String, typeExp: AstNode?) {
        this.target = target
        this.key = key
    }

    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("DotAccessNode {".tab(indent))
        sb.appendLine("\ttarget:".tab(indent))
        target.toString(indent + 1)
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

class FnTypeLiteralNode(val parameterTypeExps: List<AstNode>, val returnTypeExp: AstNode) :
    TypeLiteralNode(BaseType.FN) {
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

class JumpNode(val result: AstNode?, val op: Keyword) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("ReturnNode {".tab(indent))
        sb.appendLine("\top:$op".tab(indent))

        if (result != null) {
            sb.appendLine("\tresult:".tab(indent))
            sb.appendLine(result.toString(indent + 1))

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

class ListTypeLiteralNode(val containedType: AstNode) : TypeLiteralNode(BaseType.LIST)

open class TypeLiteralNode : AstNode {
    val baseType: BaseType

    // since everything is a struct, an entry type list is needed for all type literals.
    val entryTypes: HashMap<String, AstNode>

    companion object {
        // declare built-in type literals
        val TYPE: TypeLiteralNode? = null
        val UNIT: TypeLiteralNode? = null
        val BOOL: TypeLiteralNode? = null
        val NUM: TypeLiteralNode? = null
    }

    // used for creating root type only
    // the root type is a type value that has itself as its type expression.
    private constructor() {
        baseType = BaseType.TYPE
        entryTypes = java.util.HashMap()
    }

    constructor(baseType: BaseType) {
        this.baseType = baseType
        entryTypes = java.util.HashMap()
    }

    // for user-defined types
    constructor(entryTypes: HashMap<String, AstNode>) {
        baseType = BaseType.STRUCT
        this.entryTypes = entryTypes
    }

    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("TypeLiteralNode {".tab(indent))
        sb.appendLine("\tbasetype: $baseType".tab(indent))
        sb.appendLine("\tentries:".tab(indent))

        sb.appendLine("} TypeLiteralNode".tab(indent))
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
    override fun toString(indent: Int): String  ="StrLiteralNode { $value }".tab(indent)
}

class StructLiteralNode : AstNode {
    val entries: HashMap<String, AstNode>

    // struct type exp is built separately from struct literal
    constructor() {
        entries = java.util.HashMap()
    }

    constructor(entries: HashMap<String, AstNode>) {
        this.entries = entries
    }

    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("StructLiteralNode {".tab(indent))
        sb.appendLine("\tentries:".tab(indent))

        sb.appendLine("} StructLiteralNode".tab(indent))
        return sb.toString()
    }
}

class TypeofNode(val target: AstNode) : AstNode() {
    override fun toString(indent: Int): String {
        val sb = StringBuilder()

        sb.appendLine("TypeofNode {".tab(indent))
        sb.appendLine("\ttarget:".tab(indent))
        sb.appendLine(target.toString(indent + 1))
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
        sb.appendLine(target.toString(indent + 1))
        sb.appendLine("} UnaryNode".tab(indent))
        return sb.toString()
    }

    init {
        if (op != Symbol.BANG) {
            System.err.println("Invalid operator: " + op.symbol + " cannot be" + " used as a unary operator.")
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
