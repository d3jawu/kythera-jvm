package me.dejawu.kythera

import me.dejawu.kythera.stages.lexer.Keyword
import me.dejawu.kythera.stages.lexer.Symbol
import java.io.PrintStream

abstract class AstNode {
    abstract fun print(indent: Int, stream: PrintStream?)
}

class BlockNode(val body: List<AstNode>) : AstNode() {
    init {
        if (body.isEmpty()) {
            throw Exception("Block cannot have empty body. To return nothing, use `unit`.")
        }
    }

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("BlockNode {", indent, stream!!)
        printlnWithIndent("\tbody:", indent, stream)
        for (st in body) {
            st.print(indent + 2, stream)
        }
        printlnWithIndent("} BlockNode", indent, stream)
    }
}

class BinaryNode(val operator: Symbol, val left: AstNode, val right: AstNode) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("BinaryNode {", indent, stream!!)
        printlnWithIndent("\top: " + operator.symbol, indent, stream)
        printlnWithIndent("\tleft:", indent, stream)
        left.print(indent + 1, stream)
        printlnWithIndent("\tright:", indent, stream)
        right.print(indent + 1, stream)
        printlnWithIndent("} BinaryNode", indent, stream)
    }
}

object BooleanLiteral {
    // since there are only two boolean values we just pre-generate their
    // nodes and just reuse them
    var TRUE: BooleanLiteralNode = BooleanLiteralNode(true)
    var FALSE: BooleanLiteralNode = BooleanLiteralNode(false)

    class BooleanLiteralNode constructor(val value: Boolean) : AstNode() {
        override fun print(indent: Int, stream: PrintStream?) {
            printlnWithIndent("BooleanLiteralNode { $value }", indent, stream!!)
        }
    }

}
class BracketAccessNode(val target: AstNode, val key: AstNode) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {}
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

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("CallNode {", indent, stream!!)
        printlnWithIndent("\ttarget:", indent, stream)
        target.print(indent + 2, stream)
        printlnWithIndent("\targuments:", indent, stream)
        var n = 0
        for (ex in arguments) {
            printlnWithIndent("\t\targ $n:", indent, stream)
            ex.print(indent + 3, stream)
            n += 1
        }
        printlnWithIndent("} CallNode", indent, stream)
    }
}

class DeclarationNode(val identifier: String, val value: AstNode, val op: Keyword) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("DeclarationNode {", indent, stream!!)
        printlnWithIndent("\tidentifier: $identifier", indent, stream)
        printlnWithIndent("\top:$op", indent, stream)
        printlnWithIndent("\tvalue:", indent, stream)
        value.print(indent + 1, stream)
        printlnWithIndent("} LetNode", indent, stream)
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

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("DotAccessNode {", indent, stream!!)
        printlnWithIndent("\ttarget:", indent, stream)
        target.print(indent + 1, stream)
        printlnWithIndent("\tkey: $key", indent, stream)
        printlnWithIndent("} DotAccessNode", indent, stream)
    }
}


class FnLiteralNode(
    val parameterNames: List<String>, val body: BlockNode
) :
    AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("FnLiteralNode {", indent, stream!!)
        printlnWithIndent("\tparameters:", indent, stream)
        var n = 0
        for (param in parameterNames) {
            printlnWithIndent("\t\tparam $n: $param", indent, stream)
            n += 1
        }
        printlnWithIndent("\tbody:", indent, stream)
        body.print(indent + 2, stream)
        printlnWithIndent("} FnLiteralNode", indent, stream)
    }
}

class FnTypeLiteralNode(val parameterTypeExps: List<AstNode>, val returnTypeExp: AstNode) :
    TypeLiteralNode(BaseType.FN) {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("FnTypeLiteralNode {", indent, stream!!)
        printlnWithIndent("\tparams:", indent, stream)
        val n = 0
        for (exp in parameterTypeExps) {
            printlnWithIndent("\t\t$n:", indent, stream)
            exp.print(indent + 2, stream)
        }
        printlnWithIndent("\treturn type:", indent, stream)
        returnTypeExp.print(indent + 2, stream)
        printlnWithIndent("} FnTypeLiteralNode", indent, stream)
    }
}

class IdentifierNode(val name: String) : AstNode() {

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("IdentifierNode { name: $name }", indent, stream!!)
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

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("IfNode {", indent, stream!!)
        printlnWithIndent("\tcondition:", indent, stream)
        condition.print(indent + 2, stream)
        printlnWithIndent("\tbody:", indent, stream)
        body.print(indent + 2, stream)
        if (elseBody != null) {
            printlnWithIndent("\telse body:", indent, stream)
            elseBody.print(indent + 2, stream)
        }
        printlnWithIndent("} IfNode", indent, stream)
    }
}

class JumpNode(val result: AstNode?, val op: Keyword) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("ReturnNode {", indent, stream!!)
        printlnWithIndent("\top:$op", indent, stream)

        if(result != null) {
            printlnWithIndent("\tresult:", indent, stream)
            result.print(indent + 1, stream)

        }

        printlnWithIndent("} ReturnNode", indent, stream)
    }
}


class ListLiteralNode : AstNode {
    val entries: List<AstNode>

    constructor() {
        entries = ArrayList()
    }

    constructor(entries: List<AstNode>) {
        this.entries = entries
    }

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("ListLiteralNode {", indent, stream!!)
        printlnWithIndent("\tentries:", indent, stream)
        var i = 0
        for (exp in entries) {
            printlnWithIndent("\t\t$i:", indent, stream)
            exp.print(indent + 3, stream)
            i += 1
        }
        printlnWithIndent("} ListLiteralNode", indent, stream)
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

    // for type values, equals means an *exact* match
    // remember, values in Kythera must be cast to exactly the type they are
    // to be used as (?)
    override fun equals(o: Any?): Boolean {
        if (o !is TypeLiteralNode) {
            return false
        }
        return baseType == o.baseType
    }

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("TypeLiteralNode {", indent, stream!!)
        printlnWithIndent("\tbasetype: $baseType", indent, stream)
        printlnWithIndent("\tentries:", indent, stream)

        printlnWithIndent("} TypeLiteralNode", indent, stream)
    }
}

class DoubleLiteralNode(val value: Double) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("DoubleLiteralNode { $value }", indent, stream!!)
    }
}

class IntLiteralNode(val value: Int) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("IntLiteralNode { $value }", indent, stream!!)
    }
}

class StrLiteralNode(val value: String) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("StrLiteralNode { $value }", indent, stream!!)
    }
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

    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("StructLiteralNode {", indent, stream!!)
        printlnWithIndent("\tentries:", indent, stream)

        printlnWithIndent("} StructLiteralNode", indent, stream)
    }
}

class TypeofNode(val target: AstNode) : AstNode() {
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("TypeofNode {", indent, stream!!)
        printlnWithIndent("\ttarget:", indent, stream)
        target.print(indent + 1, stream)
        printlnWithIndent("} TypeofNode", indent, stream)
    }
}

class UnaryNode(op: Symbol, target: AstNode) : AstNode() {
    val operator: Symbol
    val target: AstNode
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("UnaryNode {", indent, stream!!)
        printlnWithIndent("\top: " + operator.symbol, indent, stream)
        printlnWithIndent("\ttarget", indent, stream)
        target.print(indent + 1, stream)
        printlnWithIndent("} UnaryNode", indent, stream)
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
    override fun print(indent: Int, stream: PrintStream?) {
        printlnWithIndent("WhileNode {", indent, stream!!)
        printlnWithIndent("\t condition:", indent, stream)
        condition.print(indent + 2, stream)
        printlnWithIndent("\tbody:", indent, stream)
        body.print(indent + 2, stream)
        printlnWithIndent("} WhileNode", indent, stream)
    }
}
