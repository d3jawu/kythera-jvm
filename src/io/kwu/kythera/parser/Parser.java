package io.kwu.kythera.parser;

import io.kwu.kythera.Scope;
import io.kwu.kythera.parser.node.*;
import io.kwu.kythera.parser.tokenizer.*;

import java.util.*;

public final class Parser {
    private List<StatementNode> program;

    private Scope currentScope;

    private Tokenizer tokenizer;

    public Parser(String input) {
        this.program = new ArrayList<StatementNode>();

        this.currentScope = new Scope();

        InputStream inputStream = new InputStream(input);
        this.tokenizer = new Tokenizer(inputStream);
    }

    public List<StatementNode> parse() {
        while (!this.tokenizer.eof()) {
            loadStatement();
        }

        return this.program;
    }

    private StatementNode parseStatement() {
        Token nextToken = this.tokenizer.peek();

        // let and return are the only non-expression nodes
        if (nextToken.tokentype == TokenType.KW) {

            if (nextToken.value.equals(Keyword.LET.toString())) {
                this.consumeToken(nextToken);

                Token identifierToken = this.confirmToken(TokenType.VAR);
                if (identifierToken == null) {
                    System.err.println("Expecting identifier token, got " + identifierToken.toString());
                    System.exit(1);
                    return null;
                }

                this.consumeToken(TokenType.VAR);

                this.consumeToken("=", TokenType.OP);

                ExpressionNode value = this.parseExpression(true);

                this.currentScope.create(identifierToken.value, value.typeExp);

                return new LetNode(identifierToken.value, value);
            }

            if (nextToken.value.equals(Keyword.RETURN.toString())) {
                return new ReturnNode(this.parseExpression(true));
            }
        }

        return this.parseExpression(true);
    }

    private ExpressionNode parseExpression(boolean canSplit) {
        ExpressionNode exp = parseExpressionAtom();

        while(
                (canSplit && this.confirmToken(TokenType.OP) != null) || // can start binary
                        (this.confirmToken(Operator.OPEN_PAREN.symbol, TokenType.PUNC) != null) || // can start call
                        (this.confirmToken("as", TokenType.KW) != null) || // can make as
                        (this.confirmToken(".", TokenType.PUNC) != null) // can make dot access
//                      || (canSplit && this.confirmToken("[", TokenType.PUNC) != null) // can make bracket access
        ) {
            if(canSplit && this.confirmToken(TokenType.OP) != null) {
                exp = makeBinary(exp, 0);
            }

            if (this.confirmToken("as", TokenType.KW) != null) {
                exp = makeAs(exp);
            }

            if(this.confirmToken(Operator.OPEN_PAREN.symbol, TokenType.PUNC) != null) {
                exp = makeCall(exp);
            }

            if(this.confirmToken(".", TokenType.PUNC) != null) {
                exp = makeDotAccess(exp);
            }
        }

        return exp;
    }

    private ExpressionNode parseExpressionAtom() {
        Token t;
        t = new Token(Operator.OPEN_PAREN.symbol, TokenType.PUNC);
        if (this.confirmToken(t) != null) {
            this.consumeToken(t);
            ExpressionNode contents = this.parseExpression(true);
            this.consumeToken(Operator.CLOSE_PAREN.symbol, TokenType.PUNC);
            return contents;
        }

        // hang on to this for later
        Token nextToken = this.tokenizer.peek();

        if (this.confirmToken(Operator.OPEN_BRACE.symbol, TokenType.PUNC) != null) {
            return this.parseStructLiteral();
        }

        if (this.confirmToken(Operator.OPEN_BRACKET.symbol, TokenType.PUNC) != null) {
            return this.parseFnLiteral();
        }

        if (this.confirmToken(Operator.BANG.symbol, TokenType.OP) != null) {
            this.consumeToken(Operator.BANG.symbol, TokenType.OP);
            return new UnaryNode(Operator.BANG, this.parseExpression(false));
        }

        // in Kythera-JS type literals were read in at this point; in the JVM implementation
        // they are inserted with the runtime and not part of the parser

        if (this.confirmToken(null, TokenType.KW) != null) {
            this.consumeToken(nextToken);

            switch (Keyword.valueOf(nextToken.value.toUpperCase())) {
                case TYPEOF:
                    break;
                case IF:
                    ExpressionNode ifCondition = this.parseExpression(true);

                    this.currentScope = new Scope(this.currentScope, null, Scope.ScopeType.CONTROL_FLOW);

                    BlockNode ifBody = this.parseBlock();

                    this.currentScope = this.currentScope.parent;

                    IfNode result;

                    if (this.confirmToken(Keyword.ELSE.toString(), TokenType.KW) != null) {
                        BlockNode ifElse;

                        // else block
                        this.consumeToken(Keyword.ELSE.toString(), TokenType.KW);

                        if (this.confirmToken(Operator.OPEN_BRACE.symbol, TokenType.PUNC) != null) {
                            // else only
                            this.currentScope = new Scope(this.currentScope, null, Scope.ScopeType.CONTROL_FLOW);
                            ifElse = this.parseBlock();
                            this.currentScope = this.currentScope.parent;
                        } else {
                            // else-if
                            this.consumeToken(Keyword.IF.toString(), TokenType.KW);
                            ifElse = this.parseBlock();
                            // TODO this is wrong
                        }

                        result = new IfNode(ifCondition, ifBody, ifElse);
                    } else {
                        // no else block
                        result = new IfNode(ifCondition, ifBody);
                    }

                    return result;
                case WHILE:
                    ExpressionNode whileCondition = this.parseExpression(true);

                    this.currentScope = new Scope(this.currentScope, null, Scope.ScopeType.CONTROL_FLOW);
                    BlockNode whileBody = this.parseBlock();
                    this.currentScope = this.currentScope.parent;

                    return new WhileNode(whileCondition, whileBody);
            }
        }

        // from this point forward, nodes are generated directly, not dispatched
        this.tokenizer.next();

        // literals
        switch (nextToken.tokentype) {
            case NUM:
                if (nextToken.value.contains(".")) {
                    return new DoubleLiteralNode(Double.parseDouble(nextToken.value));
                } else {
                    return new IntLiteralNode(Integer.parseInt(nextToken.value));
                }
//            case STR:
//                return new StrLiteralNode(nextToken.value);
            case VAR:
                // insert built-in values
                switch(nextToken.value) {
                    case "true":
                        return BooleanLiteral.TRUE;
                    case "false":
                        return BooleanLiteral.FALSE;
                    case "unit":
                        return UnitLiteral.UNIT;
                    default:
                        // try type literal
                        TypeLiteralNode tl = BaseType.typeLiteralOf(nextToken.value);

                        // otherwise, it's a normal identifier
                        if(tl == null) {
                            return new IdentifierNode(nextToken.value, this.currentScope.getTypeOf(nextToken.value));
                        } else {
                            return tl;
                        }
                }
        }

        System.err.println("Unexpected token: " + nextToken.toString());
        System.exit(1);

        return null;
    }

    // TODO isn't the whole program a block? Why aren't we representing the top level program as a block?
    // as a consequence of this, there may be some duplication with the top-level parse function
    private BlockNode parseBlock() {
        List<StatementNode> body = new ArrayList<>();

        consumeToken("{", TokenType.PUNC);

        while(this.confirmToken("}", TokenType.PUNC) == null) {
            loadStatement();
        }

        consumeToken("}", TokenType.PUNC);

        return new BlockNode(body);
    }

    private void loadStatement() {
        StatementNode st = this.parseStatement();
        if(st == null) {
            System.err.println("Statement evaluation failed.");
            System.exit(1);
        }
        this.program.add(st);
        if(this.confirmToken(";", TokenType.PUNC) == null) {
            System.err.println("Missing semicolon.");
            System.exit(1);
        }
        this.consumeToken(";", TokenType.PUNC);
    }

    private FnLiteralNode parseFnLiteral() {
        this.currentScope = new Scope(this.currentScope, null, Scope.ScopeType.FUNCTION);

        SortedMap<String, ExpressionNode> parameters = new TreeMap<String, ExpressionNode>();

        this.consumeToken(Operator.OPEN_PAREN.symbol, TokenType.PUNC);

        while(this.confirmToken(Operator.CLOSE_PAREN.symbol, TokenType.PUNC) == null) {
            // type is an expression
            ExpressionNode paramType = this.parseExpression(true);

            String paramName = this.confirmToken(TokenType.STR).value;
            this.consumeToken(TokenType.STR);

            parameters.put(paramName, paramType);
            this.currentScope.create(paramName, paramType);

            if(this.confirmToken(Operator.CLOSE_PAREN.symbol, TokenType.PUNC) == null) {
                this.consumeToken(",", TokenType.PUNC);
            }
        }

        this.consumeToken(Operator.CLOSE_PAREN.symbol, TokenType.PUNC);

        BlockNode body = this.parseBlock();

        this.currentScope = this.currentScope.parent;

        return new FnLiteralNode(parameters, body, body.typeExp);
    }

    // literals beginning with '{' could be objects
    // TODO or maps
    // TODO they could also be blocks... how do we distinguish that?
    private StructLiteralNode parseStructLiteral() {
        this.consumeToken("{", TokenType.PUNC);

        StructTypeLiteralNode structType = new StructTypeLiteralNode();
        StructLiteralNode structResult = new StructLiteralNode(structType);

        HashMap<String, ExpressionNode> typeContents = structType.entries;
        HashMap<String, ExpressionNode> resultContents = structResult.values;

        this.currentScope = new Scope(this.currentScope, structType, Scope.ScopeType.FUNCTION);

        while(this.confirmToken("}", TokenType.PUNC) == null) {
            String entryKey = this.tokenizer.next().value;

            this.consumeToken(Operator.EQUALS.symbol, TokenType.OP);

            ExpressionNode entryValue = this.parseExpression(true);

            this.consumeToken(",", TokenType.PUNC);

            typeContents.put(entryKey, entryValue.typeExp);
            resultContents.put(entryKey, entryValue);
        }

        this.consumeToken("}", TokenType.PUNC);

        this.currentScope = this.currentScope.parent;

        return structResult;
    }

    private ExpressionNode makeBinary(ExpressionNode left, int currentPrecedence) {
        Token token = this.confirmToken(TokenType.OP);
        if (token != null) {
            Operator op = Operator.symbolOf(token.value);
            int nextPrecedence = op.precedence;
            if (nextPrecedence > currentPrecedence) {
                this.tokenizer.next();
                ExpressionNode right = this.makeBinary(this.parseExpression(false), nextPrecedence);

                ExpressionNode binary;

                switch (op.kind) {
                    case ASSIGN:
                        binary = new AssignNode(op, left, right);
                        break;
                    case LOGICAL:
                    case COMPARE:
                    case ARITHMETIC:
                        binary = new BinaryNode(op, left, right);
                        break;
                    default:
                        System.err.println("Invalid operator for binary operation: " + op.symbol);
                        System.exit(0);
                        binary = null;
                }

                return this.makeBinary(binary, currentPrecedence);
            }
        }

        return left;
    }

    private ExpressionNode makeAs(ExpressionNode exp) {
        this.consumeToken("as", TokenType.KW);

        return new AsNode(exp, this.parseExpression(true));
    }

    private ExpressionNode makeCall(ExpressionNode exp) {
        List<ExpressionNode> arguments = new ArrayList<>();

        this.consumeToken(Operator.OPEN_PAREN.symbol, TokenType.PUNC);

        while(this.confirmToken(Operator.CLOSE_PAREN.symbol) == null) {
            arguments.add(this.parseExpression(true));
            this.consumeToken(",", TokenType.PUNC);
        }

        this.consumeToken(Operator.CLOSE_PAREN.symbol, TokenType.PUNC);

        return new CallNode(exp, arguments);
    }

    private ExpressionNode makeDotAccess(ExpressionNode exp) {
        this.consumeToken(".", TokenType.PUNC);

        String memberName = this.tokenizer.next().value;

        return new DotAccessNode(exp, memberName);
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
        return confirmToken(value, null);
    }

    // TODO verify token matches type without checking value
    private Token confirmToken(TokenType type) {
        return confirmToken(null, type);
    }

    // consume token without checking value
    private void consumeToken(TokenType type) {
        if(this.confirmToken(type) != null) {
            this.tokenizer.next();
        } else {
            final Token nextVal = this.tokenizer.peek();
            System.err.println("Expecting " + type.toString() + " but got " + nextVal.tokentype.toString());
            System.exit(1);
        }
    }

    private void consumeToken(String value, TokenType type) {
        if (this.confirmToken(value, type) != null) {
            this.tokenizer.next();
        } else {
            final Token nextVal = this.tokenizer.peek();
            System.err.println("Expecting "
                    + type.toString() + ": " + value
                    + " but got " + nextVal.tokentype.toString() + ": " + nextVal.value);
            System.exit(1);
        }
    }

    private void consumeToken(Token token) {
        consumeToken(token.value, token.tokentype);
    }
}
