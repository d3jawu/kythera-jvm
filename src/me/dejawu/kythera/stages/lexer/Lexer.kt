package me.dejawu.kythera.stages.lexer

class InputStream(private val input: String) {
    private var pos = 0
    private var line = 0
    private var col = 0
    operator fun next(): Char {
        val c = input[pos]
        pos += 1
        if (c == '\n') {
            line += 1
            col = 0
        } else {
            col += 1
        }
        return c
    }

    fun line(): Int {
        return line + 1
    }

    fun col(): Int {
        return col + 1
    }

    fun peek(): Char {
        return input[pos]
    }

    // yes, this is kind of cheating
    fun peekTwo(): Char {
        return input[Math.min(input.length - 1, pos + 1)]
    }

    fun eof(): Boolean {
        return pos >= input.length
    }
}


enum class TokenType {
    SYM, STR, NUM, KW, ID, EOF
}

// lexer returns EOF token when it encounters an acceptable EOF (throws errors for unexpected EOFs)
val EOF_TOKEN = Token("", TokenType.EOF)

class Token(val value: String, val tokenType: TokenType) {
    override fun toString(): String {
        return "${tokenType.name}:'$value'"
    }

    override fun equals(o: Any?): Boolean {
        if (o !is Token) {
            return false
        }
        return tokenType == o.tokenType && value == o.value
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + tokenType.hashCode()
        return result
    }
}

class UnexpectedTokenException(expected: Token, got: Token, line: Int, col: Int) : Exception(
    "Expected $expected but got $got at $line:$col")

class Lexer(private val inputStream: InputStream) {
    init {
        this.advance()
    }

    private lateinit var current: Token

    private fun readWhile(condition: (Char) -> Boolean): String {
        val output = StringBuilder()
        while (!inputStream.eof() && condition(inputStream.peek())) {
            output.append(inputStream.next())
        }
        return output.toString()
    }

    // update this.current with the next token in the stream
    private fun advance() {
        if(eof()) {
            current = EOF_TOKEN
            return
        }

        // skip whitespace and comments
        while (true) {
            val nextChar = inputStream.peek()
            when {
                this.isWhitespace(nextChar) -> {
                    readWhile { isWhitespace(it) }
                }
                // possibly a comment
                nextChar == '/' -> {
                    val nextOver = inputStream.peekTwo()
                    if (nextOver == '/') { // skip "//" comment
                        // read until newline
                        while (inputStream.peek() != '\n') {
                            inputStream.next()
                        }

                        inputStream.next()
                    } else if (nextOver == '*') { // skip "/* */" comment
                        while (!inputStream.eof()) {
                            inputStream.next()
                            if (inputStream.peek() == '*' && inputStream.peekTwo() == '/') {
                                inputStream.next()
                                inputStream.next()
                                break
                            }
                        }
                    } else {
                        // just a slash
                        break
                    }
                }
                else -> {
                    break
                }
            }
        }

        if(inputStream.eof()) {
            this.current = EOF_TOKEN
            return
        }

        val c = inputStream.peek()

        this.current = when {
            (c == '"' || c == '\'') -> {
                // this.insertAutoSemi();
                var escaped = false
                val output = StringBuilder()
                inputStream.next()
                while (!inputStream.eof()) {
                    val c = inputStream.next()
                    if (escaped) {
                        output.append(c)
                        escaped = false
                    } else if (c == '\\') {
                        escaped = true
                    } else if (c == '"') {
                        break
                    } else {
                        output.append(c)
                    }
                }
                Token(output.toString(), TokenType.STR)
            }
            (isDigit(c) || c == '-') -> {
                // this.insertAutoSemi();
                var number = ""
                if (inputStream.peek() == '-') {
                    number += "-"
                    inputStream.next()
                }
                var hasDot = false
                number += readWhile { c: Char ->
                    if (c == '.') {
                        if (hasDot) {
                            return@readWhile false // two dots in a row, not a number
                        }
                        hasDot = true
                        return@readWhile true
                    }
                    isDigit(c)
                }
                if (number == "-") {
                    Token("-", TokenType.SYM)
                } else Token(number, TokenType.NUM)        // if the minus sign is alone, return it as an operator
            }
            (isIdentStart(c)) -> {
                // this.insertAutoSemi();
                val id = readWhile { isIdent(it) }
                Token(id, if (isKeyword(id)) TokenType.KW else TokenType.ID)
            }
            (isPunc(c)) -> {
                val p = inputStream.next()

                Token("" + p, TokenType.SYM)
            }
            (isOp(c)) -> {
                Token(readWhile { c: Char -> isOp(c) }, TokenType.SYM)
            }
            else -> {
                throw Exception("Unexpected character: '$c' at ${loc()}")
            }
        }
    }

    /*
    private void insertAutoSemi() {

    }
    */

    fun peek(): Token {
        return current
    }

    private fun expect(t: Token) {
        if (t == current) {
            return
        }
        else {
            throw UnexpectedTokenException(t, current, inputStream.line(), inputStream.col())
        }
    }

    private fun consume(): Token {
        val current = this.current
        advance()
        return current
    }

    // optionally expect exact token value
    fun consume(expected: Token): Token {
        expect(expected)
        return consume()
    }

    // optionally expect token type
    fun consume(expectedType: TokenType): Token {
        if (current.tokenType != expectedType ){
            throw Exception("Expected ${expectedType.name} but got $current instead at ${loc()}.")
        }

        return this.consume()
    }

    fun loc(): String = "${this.inputStream.line()}:${this.inputStream.col()}"
    fun eof(): Boolean = inputStream.eof()

    private fun isKeyword(word: String): Boolean {
        for (kw in Keyword.values()) {
            if (kw.name.toLowerCase() == word) {
                return true
            }
        }
        return false
    }

    private fun isDigit(c: Char): Boolean {
        return ("" + c).matches(Regex("[0-9]"))
    }

    private fun isIdentStart(c: Char): Boolean {
        return ("" + c).matches(Regex("(?i)[a-z_]"))
    }

    private fun isIdent(c: Char): Boolean {
        return isIdentStart(c) || "_0123456789".indexOf(c) >= 0
    }

    // the lexer treats ops and puncs differently
    // in the parser ops and puncs are treated the same, as Symbols
    // from the lexer's perspective ops are symbols that may be followed
    // by other symbols, e.g. '+='
    private fun isOp(c: Char): Boolean {
        return "+-*/%=&|<>!~".indexOf(c) >= 0
    }

    // from the lexer's perspective puncs are symbols that only appear alone
    private fun isPunc(c: Char): Boolean {
        return ",;(){}[]:.".indexOf(c) >= 0
    }

    private fun isWhitespace(c: Char): Boolean {
        return " \t\r\n".indexOf(c) >= 0
    }
}