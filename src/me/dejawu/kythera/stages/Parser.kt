package me.dejawu.kythera.stages

import me.dejawu.kythera.*
import me.dejawu.kythera.stages.lexer.*
import me.dejawu.kythera.stages.lexer.Symbol.*
import me.dejawu.kythera.stages.lexer.Keyword.*

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Parser(input: String) {
    private val program: MutableList<AstNode>
    private val lexer: Lexer
    fun parse(): List<AstNode> {
        try {
            while (!lexer.eof()) {
                program.add(parseStatement())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return program
    }

    // parse a full statement: expression + optional semicolon
    private fun parseStatement(): AstNode {
        val exp = parseExp(true)

        if (lexer.peek() == SEMICOLON.token) {
            lexer.consume(SEMICOLON.token)
        }

        return exp
    }

    private fun parseExp(compose: Boolean): AstNode {
        val exp = parseExpAtom()

        if (!compose) {
            return exp;
        }

        var composed = exp;

        while (true) {
            val next = lexer.peek()
            when {
                next == LEFT_PAREN.token -> {
                    composed = makeCall(composed)
                }
                // check if binary expression
                isBinaryOp(Symbol.from(next.value)) -> {
                    composed = makeBinary(composed, 0)
                }
                next == DOT.token -> {
                    composed = makeDotAccess(composed)
                }
                next == LEFT_BRACKET.token -> {
                    composed = makeBracketAccess(composed)
                }
                else -> {
                    break
                }
            }
        }

        return composed
    }

    private fun parseExpAtom(): AstNode {
        val next = lexer.peek()

        return when {
            LEFT_PAREN.token == next -> {
                lexer.consume(LEFT_PAREN.token)
                val nextToken = lexer.peek()

                if (nextToken == RIGHT_PAREN.token) {
                    return parseFnLiteral(null)
                }

                val nextExp = parseExp(true)

                when (lexer.peek()) {
                    COLON.token -> {
                        return parseFnLiteral(nextToken.value)
                    }
                    COMMA.token -> {
                        // a comma immediately after means we just picked up the first type expression, e.g. (int, int,) => {}
                        TODO("fn type literals not yet implemented.")
                    }
                    RIGHT_PAREN.token -> {
                        lexer.consume(RIGHT_PAREN.token)
                        // paren-wrapped expression, just return contents
                        return nextExp;
                    }
                    else -> {
                        throw Exception("Unexpected token: ${lexer.peek()} at ${lexer.loc()}")
                    }
                }
            }
            LEFT_BRACKET.token == next -> {
                parseListLiteral()
            }
            LEFT_BRACE.token == next -> {
                lexer.consume(LEFT_BRACE.token)

                // distinguish between struct literal or struct type literal

                // consume first exp; what kind of expression it is depends
                // on what comes after

                // struct literal and struct type literal both start with an identifier

                val firstExp = parseExp(true)
                return when (lexer.peek()) {
                    COMMA.token -> {
                        if (firstExp !is AssignNode) {
                            throw Exception("Expected assignment to stack member but got $firstExp for first entry in struct literal.")
                        }
                        parseStructLiteral(firstExp)
                    }

                    COLON.token -> {
                        if (firstExp !is IdentifierNode) {
                            throw Exception("Expected identifier but got $firstExp for first entry in struct type literal.")
                        }
                        parseStructTypeLiteral(firstExp.name)
                    }
                    SEMICOLON.token -> {
                        lexer.consume(SEMICOLON.token)
                        parseBlock(firstExp)
                    }
                    else -> {
                        throw Exception("Unexpected token ${lexer.peek()} after left brace '{' at ${lexer.loc()}")
                    }
                }
            }
            BANG.token == next -> {
                lexer.consume(BANG.token)
                UnaryNode(BANG, parseExp(false))
            }
            TYPEOF.token == next -> {
                lexer.consume(TYPEOF.token)
                return TypeofNode(parseExp(true))
            }
            IF.token == next -> {
                lexer.consume(IF.token)

                val ifCondition = parseExp(true)
                val ifBody = parseBlock()
                val next = lexer.peek()
                return if (next == ELSE.token) {
                    // else block present
                    lexer.consume(ELSE.token)
                    val ifElse: AstNode = when (lexer.peek()) {
                        LEFT_BRACE.token -> {
                            // else only, block follows
                            parseBlock()
                        }
                        IF.token -> {
                            // else-if, if follows
                            parseExp(true)
                        }
                        else -> {
                            throw Exception(lexer.peek().toString() + " is not valid after 'else'.")
                        }
                    }
                    IfNode(ifCondition, ifBody, ifElse)
                } else {
                    // no else block
                    IfNode(ifCondition, ifBody)
                }
            }
            WHILE.token == next -> {
                lexer.consume(WHILE.token)
                val whileCondition = parseExp(true)
                val whileBody = parseBlock()
                return WhileNode(whileCondition, whileBody)
            }
            CONST.token == next || LET.token == next -> {
                val declarationToken = lexer.consume(next)
                val identifierToken = lexer.consume(TokenType.ID)

                lexer.consume(EQUAL.token)

                return DeclarationNode(identifierToken.value, parseExp(true), Keyword.from(declarationToken.value))
            }
            RETURN.token == next || BREAK.token == next || CONTINUE.token == next -> {
                lexer.consume(next)

                if (lexer.peek() == SEMICOLON.token) {
                    JumpNode(Keyword.from(next.value), null)
                } else {
                    val result = parseExp(true)
                    JumpNode(Keyword.from(next.value), result)
                }
            }
            next.tokenType == TokenType.NUM -> {
                lexer.consume(TokenType.NUM)
                if (next.value.contains('.')) {
                    DoubleLiteralNode(next.value.toDouble())
                } else {
                    IntLiteralNode(next.value.toInt())
                }
            }
            next.tokenType == TokenType.STR -> {
                lexer.consume(TokenType.STR)
                StrLiteralNode(next.value)
            }
            next.tokenType == TokenType.ID -> {
                lexer.consume(TokenType.ID)

                return if (isAssignOp(Symbol.from(lexer.peek().value))) {
                    val op = lexer.consume(TokenType.SYM)
                    val right = parseExp(true)

                    AssignNode(Symbol.from(op.value), next.value, right)
                } else {
                    IdentifierNode(next.value)
                }
            }
            else -> {
                throw Exception("Unexpected token: ${lexer.peek()} at ${lexer.loc()}")
            }
        }
    }

    // sometimes parseBlock will be called with the first statement already
    // parsed
    private fun parseBlock(firstStatement: AstNode): BlockNode {
        val body: MutableList<AstNode> = ArrayList()
        body.add(firstStatement)

        while (lexer.peek() != RIGHT_BRACE.token) {
            body.add(parseStatement())
        }
        lexer.consume(RIGHT_BRACE.token)
        return BlockNode(body)
    }

    private fun parseBlock(): BlockNode {
        lexer.consume(LEFT_BRACE.token)
        val firstStatement = parseStatement()
        return parseBlock(firstStatement)
    }

    // the parser should have already consumed the left-paren
    // if first_param_name is present, that should have been consumed as well (but not the colon after it)
    private fun parseFnLiteral(firstParamName: String?): FnLiteralNode {
        val paramNames = ArrayList<String>()
        val paramTypes = ArrayList<AstNode>()

        // if first parameter is provided, handle first type exp as well
        if (firstParamName != null) {
            paramNames.add(firstParamName)
            lexer.consume(COLON.token)

            // type exp for first parameter
            paramTypes.add(parseExp(true))

            if (lexer.peek() == COMMA.token) {
                lexer.consume(COMMA.token)
            }
        }

        // read in remaining parameters
        while (lexer.peek() != RIGHT_PAREN.token) {
            val paramName = lexer.consume(TokenType.ID)
            lexer.consume(COLON.token)
            val paramType = parseExp(true)

            paramNames.add(paramName.value)
            paramTypes.add(paramType)

            if (lexer.peek() == COMMA.token) {
                lexer.consume(COMMA.token)
            }
        }

        lexer.consume(RIGHT_PAREN.token)

        lexer.consume(EQUAL_GREATER.token)

        val body = parseBlock()
        return FnLiteralNode(
            paramNames,
            body
        )
    }

    // sometimes parseStructLiteral is called with the first identifier already consumed
    private fun parseStructLiteral(firstEntry: AssignNode): StructLiteralNode {
        val resultContents = HashMap<String, AstNode>()

        resultContents[firstEntry.id] = firstEntry.exp

        lexer.consume(COMMA.token)

        while (lexer.peek() != RIGHT_BRACE.token) {
            var entryKey: String

                val keyToken = lexer.consume(TokenType.ID)

                entryKey = keyToken.value
            lexer.consume(EQUAL.token)
            val entryValue = parseExp(true)
            lexer.consume(COMMA.token)
            resultContents[entryKey] = entryValue
        }

        // type information is populated later at the resolver
        val structResult = StructLiteralNode(resultContents)

        lexer.consume(RIGHT_BRACE.token)
        return structResult
    }

    private fun parseStructTypeLiteral(firstEntryName: String): StructTypeLiteralNode {
        val entries = HashMap<String, AstNode>()

        lexer.consume(COLON.token)
        entries[firstEntryName] = parseExp(true)
        lexer.consume(COMMA.token)

        while (lexer.peek() != RIGHT_BRACE.token) {
            val entryToken = lexer.consume(TokenType.ID);

            lexer.consume(COLON.token)
            val typeExp = parseExp(true)
            lexer.consume(COMMA.token)

            entries[entryToken.value] = typeExp
        }

        val structType = StructTypeLiteralNode(entries)

        lexer.consume(RIGHT_BRACE.token)
        return structType
    }

    private fun parseListLiteral(): ListLiteralNode {
        lexer.consume(LEFT_BRACKET.token)

        var firstRun = true

        val contents = ArrayList<AstNode>()


        while (lexer.peek() != RIGHT_BRACKET.token) {
            val entry = parseExp(true)
            if (firstRun) {
                firstRun = false
            }

            contents.add(entry)

            // TODO allow optional final comma
            lexer.consume(COMMA.token)
        }

        lexer.consume(RIGHT_BRACKET.token)

        val listNode = ListLiteralNode(contents);

        return listNode
    }

    private fun makeBinary(left: AstNode, currentPrecedence: Int): AstNode {
        val token = lexer.peek()
        val op = Symbol.from(token.value)
        val nextPrecedence = op.precedence

        if (isBinaryOp(op)) {
            if (nextPrecedence > currentPrecedence) {
                lexer.consume(TokenType.SYM)

                val right = makeBinary(parseExp(false), nextPrecedence)
                val binary = BinaryNode(op, left, right)

                return makeBinary(binary, currentPrecedence)
            }
        }

        return left
    }

    private fun makeCall(exp: AstNode): AstNode {
        val arguments: MutableList<AstNode> = ArrayList()
        lexer.consume(LEFT_PAREN.token)
        while (lexer.peek() != RIGHT_PAREN.token) {
            arguments.add(parseExp(true))

            // allow last comma to be missing
            if (lexer.peek() != RIGHT_PAREN.token) {
                lexer.consume(COMMA.token)
            }
        }
        lexer.consume(RIGHT_PAREN.token)
        return CallNode(exp, arguments)
    }

    private fun makeDotAccess(exp: AstNode): AstNode {
        lexer.consume(DOT.token)
        val memberName = lexer.consume(TokenType.ID).value
        return DotAccessNode(exp, memberName)
    }

    private fun makeBracketAccess(exp: AstNode): AstNode {
        TODO("Not yet implemented")
    }

    private fun isBinaryOp(sym: Symbol): Boolean = arrayOf(
        BAR,
        AND,
        AND_AND,
        BAR_BAR,
        EQUAL_EQUAL,
        BANG_EQUAL,
        LESS,
        LESS_EQUAL,
        GREATER,
        GREATER_EQUAL,
        PLUS,
        MINUS,
        STAR,
        SLASH,
        PERCENT
    ).contains(sym);

    private fun isAssignOp(sym: Symbol): Boolean = arrayOf(
        EQUAL,
        PLUS_EQUAL,
        MINUS_EQUAL,
        STAR_EQUAL,
        SLASH_EQUAL,
        PERCENT_EQUAL
    ).contains(sym)

    init {
        program = ArrayList()
        val inputStream = InputStream(input)
        lexer = Lexer(inputStream)
    }
}