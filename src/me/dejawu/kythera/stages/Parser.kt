package me.dejawu.kythera.stages

import me.dejawu.kythera.BaseType
import me.dejawu.kythera.ast.*
import me.dejawu.kythera.stages.tokenizer.*
import me.dejawu.kythera.stages.tokenizer.Symbol.SymbolKind
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.system.exitProcess

class Parser(input: String?) {
    private val program: MutableList<StatementNode>
    private val tokenizer: Tokenizer
    fun parse(): List<StatementNode> {
        while (!tokenizer.eof()) {
            loadStatement(program)
        }
        return program
    }

    private fun parseStatement(): StatementNode {
        val nextToken = tokenizer.peek()

        // let and return are the only non-expression nodes
        if (nextToken.tokentype == TokenType.KW) {
            if (nextToken.value == Keyword.LET.toString()) {
                tokenizer.consume(nextToken)
                val identifierToken = tokenizer.confirm(TokenType.VAR)
                if (identifierToken == null) {
                    System.err.println("Expecting identifier token, got " + tokenizer.peek())
                    exitProcess(1)
                }
                tokenizer.consume(TokenType.VAR)
                tokenizer.consume(Symbol.EQUALS.token)
                val value = parseExpression(true)
                return LetNode(identifierToken.value, value)
            }
            if (nextToken.value == Keyword.RETURN.toString()) {
                tokenizer.consume(Keyword.RETURN.toString(), TokenType.KW)
                return ReturnNode(parseExpression(true))
            }
        }
        return parseExpression(true)
    }

    private fun parseExpression(canSplit: Boolean): ExpressionNode {
        var exp = parseExpressionAtom()
        while (canSplit && tokenizer.confirm(TokenType.OP) != null ||  //
                // can start binary
                tokenizer.confirm(Symbol.OPEN_PAREN.token) != null ||  // can start call
                tokenizer.confirm(Symbol.DOT.token) != null || // can make dot access
                tokenizer.confirm(Symbol.OPEN_BRACKET.token) != null // can make bracket access
//                (canSplit && this.tokenizer.confirm(Symbol.OPEN_BRACKET.token) != null) // can make bracket access
        ) {
            if (canSplit && tokenizer.confirm(TokenType.OP) != null) {
                exp = makeBinary(exp, 0)
            }
            if (tokenizer.confirm(Symbol.OPEN_PAREN.token) != null) {
                exp = makeCall(exp)
            }
            if (tokenizer.confirm(Symbol.DOT.token) != null) {
                exp = makeDotAccess(exp)
            }
            if (tokenizer.confirm(Symbol.OPEN_BRACKET.token) != null) {
                System.err.println("Bracket access not yet implemented.");
                exitProcess(1)
            }
        }
        return exp
    }

    private fun parseExpressionAtom(): ExpressionNode {
        if (tokenizer.confirm(Symbol.OPEN_PAREN.token) != null) {
            tokenizer.consume(Symbol.OPEN_PAREN.token)

            // check for immediate close-paren, which indicates a fn with no params
            if (tokenizer.confirm(")", TokenType.PUNC) != null) {
                // TODO this is ambiguous, it also could be the start of an fn type literal
                // potential solution: all fn literals require {} bodies?
                return parseFnLiteral()
            } else { // beginning of fn literal or fn type literal
                // parse the first expression
                val contents = parseExpression(true)

                when {
                    tokenizer.confirm(TokenType.VAR) != null -> {
                        // if the next token is a var value, it's the beginning of a fn literal because
                        // we're picking up the identifier name after the type expression, e.g. (int x) => {}
                        return parseFnLiteral(arrayListOf(contents))
                    }
                    tokenizer.confirm(",", TokenType.PUNC) != null -> {
                        // a comma immediately after means we just picked up the first type expression, e.g. (int, int,) => {}
                        System.err.println("fn type literals not yet implemented.")
                        exitProcess(1)
                    }
                    tokenizer.confirm(")", TokenType.PUNC) != null -> {
                        tokenizer.consume(")", TokenType.PUNC)
                        // paren-wrapped expression, just return contents
                        return contents;
                    }
                    else -> {
                        System.err.println("Unexpected token: ${tokenizer.peek()}")
                        exitProcess(1)
                    }
                }
            }
        }

        // hang on to this for later
        val nextToken = tokenizer.peek()
        if (tokenizer.confirm(Symbol.OPEN_BRACE.token) != null) {
            // distinguish between struct literal or struct type literal
            tokenizer.consume(Symbol.OPEN_BRACE.token)

            // consume first statement; what kind of expression it is depends
            // on what comes after
            val firstStatement = parseStatement()
            return when {
                tokenizer.confirm(Symbol.COLON.token) != null -> {
                    // colon: struct literal (first statement must be IdentifierNode)
                    if (firstStatement !is IdentifierNode) {
                        System.err.println("Expected IdentifierNode for first " + "entry in struct literal.")
                        exitProcess(1)
                    }
                    this.parseStructLiteral(firstStatement.name)
                }
                tokenizer.confirm(Symbol.SEMICOLON.token) != null -> {
                    // semicolon: code block
                    this.parseBlock(firstStatement)
                }
                else -> {
                    // another expression: struct type literal
                    if (firstStatement !is ExpressionNode) {
                        System.err.println("Expected expression for first type in" + " struct type literal.")
                        exitProcess(1)
                    }
                    this.parseStructTypeLiteral(firstStatement)
                }
            }
        }
        if (tokenizer.confirm(Symbol.OPEN_BRACKET.token) != null) {
            return this.parseListLiteral();
        }

        if (tokenizer.confirm(Symbol.BANG.token) != null) {
            tokenizer.consume(Symbol.BANG.token)
            return UnaryNode(Symbol.BANG, parseExpression(false))
        }

        // in Kythera-JS type literals were read in at this point; in the JVM
        // implementation
        // they are inserted with the runtime and not part of the parser
        if (tokenizer.confirm(TokenType.KW) != null) {
            tokenizer.consume(nextToken)
            when (Keyword.valueOf(nextToken.value.toUpperCase())) {
                Keyword.TYPEOF -> {
                    val target = parseExpression(true)
                    return TypeofNode(target)
                }
                Keyword.IF -> {
                    val ifCondition = parseExpression(true)
                    val ifBody = this.parseBlock()
                    return if (tokenizer.confirm(Keyword.ELSE.token) != null) {
                        // else block present
                        tokenizer.consume(Keyword.ELSE.token)
                        val ifElse: ExpressionNode = when {
                            tokenizer.confirm(Symbol.OPEN_BRACE.token) != null -> {
                                // else only, block follows
                                this.parseBlock()
                            }
                            tokenizer.confirm(Keyword.IF.token) != null -> {
                                // else-if, if follows
                                parseExpression(true)
                            }
                            else -> {
                                System.err.println(tokenizer.peek().toString() + " is not valid after 'else'.")
                                exitProcess(1)
                            }
                        }
                        IfNode(ifCondition, ifBody, ifElse)
                    } else {
                        // no else block
                        IfNode(ifCondition, ifBody)
                    }
                }
                Keyword.WHILE -> {
                    val whileCondition = parseExpression(true)
                    val whileBody = this.parseBlock()
                    return WhileNode(whileCondition, whileBody)
                }
                else -> {
                    System.err.println("Unexpected keyword: ${nextToken.value}")
                    exitProcess(1);
                }
            }
        }

        // from this point forward, nodes are generated directly, not dispatched
        tokenizer.next()
        println("Using token directly: ${nextToken.value}")
        when (nextToken.tokentype) {
            TokenType.NUM ->
                return NumLiteralNode(nextToken.value.toDouble())
            TokenType.VAR -> return when (nextToken.value) {
                "true" -> BooleanLiteral.TRUE
                "false" -> BooleanLiteral.FALSE
                "unit" -> UnitLiteral.UNIT

                "Unit" -> TypeLiteralNode.UNIT
                "Num" -> TypeLiteralNode.NUM
                "Bool" -> TypeLiteralNode.BOOL
                else -> IdentifierNode(nextToken.value)
            }
            else -> {
                System.err.println("Unexpected token: $nextToken")
                exitProcess(1)
            }
        }
    }

    // sometimes parseBlock will be called with the first statement already
    // parsed
    private fun parseBlock(firstStatement: StatementNode): BlockNode {
        val body: MutableList<StatementNode> = ArrayList()
        body.add(firstStatement)

        // TODO optional semi
        tokenizer.consume(Symbol.SEMICOLON.token)
        while (tokenizer.confirm(Symbol.CLOSE_BRACE.token) == null) {
            loadStatement(body)
        }
        tokenizer.consume(Symbol.CLOSE_BRACE.token)
        return BlockNode(body)
    }

    private fun parseBlock(): BlockNode {
        tokenizer.consume(Symbol.OPEN_BRACE.token)
        val firstStatement = parseStatement()
        return parseBlock(firstStatement)
    }

    private fun loadStatement(statements: MutableList<StatementNode>) {
        val st = parseStatement()
        statements.add(st)
        st.print(0, System.out)

        // TODO optional semi
        if (tokenizer.confirm(Symbol.SEMICOLON.token) == null) {
            System.err.println("Expected semicolon but got " + tokenizer.peek())
            exitProcess(1)
        }
        tokenizer.consume(Symbol.SEMICOLON.token)
    }

    // the parser should have already consumed the opening paren
    // if there are one or more parameters, the ExpressionNode of the first (i.e. the type of the first argument)
    // should be preloaded in the paramTypes argument, along with
    private fun parseFnLiteral(paramTypes: ArrayList<ExpressionNode> = ArrayList()): FnLiteralNode {
        val paramNames = ArrayList<String>()

        // if paramTypes is pre-populated, grab the next param name as well.
        if (paramTypes.size == 1) {
            val name = tokenizer.confirm(TokenType.VAR).value
            tokenizer.consume(TokenType.VAR)
            paramNames.add(name)
            tokenizer.consume(",", TokenType.PUNC)
        }

        while (tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
            val paramTypeExp = parseExpression(true)
            val paramName = tokenizer.confirm(TokenType.VAR).value
            tokenizer.consume(TokenType.VAR)
            paramNames.add(paramName)
            paramTypes.add(paramTypeExp)
            if (tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
                tokenizer.consume(Symbol.COMMA.token)
            }
        }

        tokenizer.consume(Symbol.CLOSE_PAREN.token)

        tokenizer.consume("=>", TokenType.OP)

        val body = this.parseBlock()
        return FnLiteralNode(
                FnTypeLiteralNode(paramTypes, null), // resolver will fill in return type
                paramNames,
                body)
    }

    // sometimes parseStructLiteral is called with the first identifier already consumed
    private fun parseStructLiteral(firstIdentifier: String): StructLiteralNode {
        val resultContents = HashMap<String, ExpressionNode>()
        var firstRun = true
        while (tokenizer.confirm(Symbol.CLOSE_BRACE.token) == null) {
            var entryKey: String
            if (firstRun) {
                entryKey = firstIdentifier
                firstRun = false
            } else {
                entryKey = tokenizer.next().value
            }
            tokenizer.consume(Symbol.COLON.token)
            val entryValue = parseExpression(true)
            tokenizer.consume(Symbol.COMMA.token)
            resultContents[entryKey] = entryValue
        }

        // type information is populated later at the resolver
        val structResult = StructLiteralNode(TypeLiteralNode(BaseType.STRUCT), resultContents)

        tokenizer.consume(Symbol.CLOSE_BRACE.token)
        return structResult
    }

    private fun parseStructLiteral(): StructLiteralNode {
        tokenizer.consume(Symbol.OPEN_BRACE.token)
        val t = tokenizer.next()
        if (t.tokentype != TokenType.VAR) {
            System.err.println("Expecting identifier for struct literal but got $t")
            exitProcess(1)
        }
        return this.parseStructLiteral(t.value)
    }

    private fun parseStructTypeLiteral(firstTypeExp: ExpressionNode): TypeLiteralNode {
        val entries = HashMap<String, ExpressionNode>()
        var firstRun = true
        while (tokenizer.confirm(Symbol.CLOSE_BRACE.token) == null) {
            var typeExp: ExpressionNode
            if (firstRun) {
                typeExp = firstTypeExp
                firstRun = false
            } else {
                typeExp = parseExpression(true)
            }
            val entryKey = tokenizer.next().value
            tokenizer.consume(Symbol.COMMA.token)
            entries[entryKey] = typeExp
        }

        val structType = TypeLiteralNode(entries)

        tokenizer.consume(Symbol.CLOSE_BRACE.token)
        return structType
    }

    private fun parseStructTypeLiteral(): TypeLiteralNode {
        tokenizer.consume(Symbol.OPEN_BRACE.token)
        val firstTypeExp = parseExpression(true)
        return this.parseStructTypeLiteral(firstTypeExp)
    }

    private fun parseListLiteral(): ListLiteralNode {
        tokenizer.consume(Symbol.OPEN_BRACKET.token)

        var firstRun = true
        var containedType: ExpressionNode = TypeLiteralNode.UNIT

        val contents = ArrayList<ExpressionNode>()

        while (tokenizer.confirm(Symbol.CLOSE_BRACKET.token) == null) {
            val entry = this.parseExpression(true)
            if (firstRun) {
                firstRun = false
            }

            contents.add(entry)

            // TODO allow optional final comma
            tokenizer.consume(Symbol.COMMA.token)
        }

        tokenizer.consume(Symbol.CLOSE_BRACKET.token)

        val listType = ListTypeLiteralNode(containedType)
        val listNode = ListLiteralNode(listType, contents);

        return listNode
    }

    private fun makeBinary(left: ExpressionNode, currentPrecedence: Int): ExpressionNode {
        val token = tokenizer.confirm(TokenType.OP)
        if (token != null) {
            val op = Symbol.symbolOf(token.value)
            val nextPrecedence = op.precedence
            if (nextPrecedence > currentPrecedence) {
                tokenizer.next()
                val right = makeBinary(parseExpression(false), nextPrecedence)
                val binary: ExpressionNode
                binary = when (op.kind) {
                    SymbolKind.ASSIGN -> AssignNode(op, left, right)
                    SymbolKind.LOGICAL, SymbolKind.COMPARE, SymbolKind.ARITHMETIC -> BinaryNode(op, left, right)
                    else -> {
                        System.err.println("Invalid operator for binary " + "operation: " + op.symbol)
                        exitProcess(1)
                    }
                }
                return makeBinary(binary, currentPrecedence)
            }
        }
        return left
    }

    private fun makeCall(exp: ExpressionNode): ExpressionNode {
        val arguments: MutableList<ExpressionNode> = ArrayList()
        tokenizer.consume(Symbol.OPEN_PAREN.token)
        while (tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
            arguments.add(parseExpression(true))

            // allow last comma to be missing
            if (tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
                tokenizer.consume(Symbol.COMMA.token)
            }
        }
        tokenizer.consume(Symbol.CLOSE_PAREN.token)
        return CallNode(exp, arguments)
    }

    private fun makeDotAccess(exp: ExpressionNode): ExpressionNode {
        tokenizer.consume(Symbol.DOT.token)
        val memberName = tokenizer.next().value
        return DotAccessNode(exp, memberName)
    }

//    private fun makeBracketAccess(exp: ExpressionNode): ExpressionNode {
//        return null
//    }

    init {
        program = ArrayList()
        val inputStream = InputStream(input)
        tokenizer = Tokenizer(inputStream)
    }
}