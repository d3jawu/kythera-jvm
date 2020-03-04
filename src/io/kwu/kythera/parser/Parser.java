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
            try {
                this.program.add(this.parseExpression(true));
            } catch (ParserException pe) {
                throw pe;
            }
            if (this.confirmToken(";", TokenType.PUNC) == null) {
                throw new ParserException("Missing semicolon.");
            }
            this.consumeToken(";", TokenType.PUNC);
        }

        return this.program;
    }

    private ExpressionNode parseExpression(boolean canSplit) throws ParserException {
        Supplier<ExpressionNode> ParseExpressionAtom = () -> {

        };
    }

    private List<ExpressionNode> delimited(Token start, Token stop, Token delimiter, Supplier<ExpressionNode> supplier) {
        List<ExpressionNode> results = new ArrayList<>();
        boolean first = true;

        this.consumeToken(start);

        while (!this.tokenizer.eof()) {
            if(this.confirmToken(stop) != null) {
                break;
            }

            if(first) {
                first = false;
            } else {
                this.consumeToken(delimiter);
            }

            if(this.confirmToken(stop) != null) {
                break;
            }

            results.add(supplier.get());
        }

        this.consumeToken(stop);

        return results;
    }

    // TODO making this a separate function may be redundant if it is always followed by a call to consumeToken
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
