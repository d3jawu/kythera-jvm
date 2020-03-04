package io.kwu.kythera.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.kwu.kythera.Scope;
import io.kwu.kythera.parser.node.*;
import io.kwu.kythera.parser.tokenizer.*;

public final class Parser {
    private List<StatementNode> program;

    private Scope rootScope;
    private Scope currentScope;

    private InputStream inputStream;
    private Tokenizer tokenizer;

    public Parser(String input) {
        this.program = new ArrayList<StatementNode>();

        this.rootScope = new Scope();
        this.currentScope = this.rootScope;

        this.inputStream = new InputStream(input);
        this.tokenizer = new Tokenizer(this.inputStream);
    }

    public List<StatementNode> parse() throws ParserException {
        while (!this.tokenizer.eof()) {
                this.program.add(this.parseExpression(true));
            if (this.confirmToken(";", TokenType.PUNC) == null) {
                throw new ParserException("Missing semicolon.");
            }
            this.consumeToken(";", TokenType.PUNC);
        }

        return this.program;
    }

    // TODO clean up error handling, which uses null way too much
    private ExpressionNode parseExpression(boolean canSplit) throws ParserException {
        ExpressionSupplier parseExpressionAtom = () -> {
            Token t;
            t = new Token(Operator.OPEN_PAREN.symbol, TokenType.PUNC);
            if (this.confirmToken(t) != null) {
                this.consumeToken(t);
                // ... where do exceptions thrown by parseExpression go?
                ExpressionNode contents = this.parseExpression(true);
                this.consumeToken(Operator.CLOSE_PAREN.symbol, TokenType.PUNC);
            }

            // hang on to this for later
            Token nextToken = this.tokenizer.peek();

            if(this.confirmToken(Operator.OPEN_BRACE.symbol, TokenType.PUNC) != null) {
                return this.parseStructLiteral();
            }

            if(this.confirmToken(Operator.OPEN_BRACKET.symbol, TokenType.PUNC) != null) {
                return this.parseFunctionLiteral();
            }

            if(this.confirmToken(Operator.NOT.symbol, TokenType.OP) != null) {
                this.consumeToken(Operator.NOT.symbol, TokenType.OP);
                return new UnaryNode(Operator.NOT, this.parseExpression(false));
            }

            // in Kythera-JS type literals were read in at this point; in the JVM implementation
            // they are inserted with the runtime and not part of the parser

            if(this.confirmToken(null, TokenType.KW) != null) {
                this.consumeToken(nextToken);

                switch(Keyword.valueOf(nextToken.value.toUpperCase())) {
                    // true, false, and unit are no longer keywords, they are literals
                    case TYPEOF:
                        break;
                    case LET:
                        Token identToken = this.tokenizer.next();
                        if(identToken.tokentype != TokenType.VAR) {
                            this.inputStream.err("Expected identifier but got " + identToken.value);
                            return null;
                        }

                        this.consumeToken(Operator.EQUALS.symbol, TokenType.OP);

                        ExpressionNode value = this.parseExpression(true);

                        try {
                            this.currentScope.create(identToken.value, value.type);

                            return new LetNode(identToken.value, value);
                        } catch (Exception e) {
                            this.inputStream.err(e.getMessage());
                            return null;
                        }
                }
            }
        };
    }

    private List<ExpressionNode> delimited(Token start, Token stop, Token delimiter, Supplier<ExpressionNode> supplier) {
        List<ExpressionNode> results = new ArrayList<>();
        boolean first = true;

        this.consumeToken(start);

        while (!this.tokenizer.eof()) {
            if (this.confirmToken(stop) != null) {
                break;
            }

            if (first) {
                first = false;
            } else {
                this.consumeToken(delimiter);
            }

            if (this.confirmToken(stop) != null) {
                break;
            }

            results.add(supplier.get());
        }

        this.consumeToken(stop);

        return results;
    }

    // TODO making this a separate function may be redundant if it is always followed by a call to consumeToken
    // TODO make this call with this signature redundant and remove it
    /**
     * @param value Contents of the token. Pass null to confirm only type.
     * @param type  Token type. Pass null to confirm only value.
     * @return Next token if value and/or type match, null otherwise
     */
    private Token confirmToken(String value, TokenType type) {
        if (this.tokenizer.eof()) {
            return null;
        }

        final Token token = this.tokenizer.peek();

        if (type != null && token.tokentype != type) {
            return null;
        }

        if (value != null && !token.value.equals(value)) {
            return null;
        }

        return token;
    }

    private Token confirmToken(Token token) {
        return confirmToken(token.value, token.tokentype);
    }

    // TODO verify value matches without checking type
    private Token confirmToken(String value) {
        return null;
    }

    // TODO verify token matches type without checking value
    private Token confirmToken(TokenType type) {
        return null;
    }

    // TODO this is only ever called with type=null from delimited(), it may be possible to remove the null altogether
    private void consumeToken(String value, TokenType type) {
        if (this.confirmToken(value, type) != null) {
            this.tokenizer.next();
        } else {
            final Token nextVal = this.tokenizer.peek();
            this.inputStream.err("Expecting "
                    + type.toString() + ": " + value
                    + " but got" + nextVal.tokentype.toString() + ": " + nextVal.value + " instead.");
        }
    }

    private void consumeToken(Token token) {
        consumeToken(token.value, token.tokentype);
    }
}
