package me.dejawu.kythera.passes;

import me.dejawu.kythera.ast.*;
import me.dejawu.kythera.passes.tokenizer.*;

import java.util.*;

public final class Parser {
    private final List<StatementNode> program;

    private final Tokenizer tokenizer;

    public Parser(String input) {
        this.program = new ArrayList<>();

        InputStream inputStream = new InputStream(input);
        this.tokenizer = new Tokenizer(inputStream);
    }

    public List<StatementNode> parse() {
        while (!this.tokenizer.eof()) {
            loadStatement(this.program);
        }

        return this.program;
    }

    private StatementNode parseStatement() {
        Token nextToken = this.tokenizer.peek();

        // let and return are the only non-expression nodes
        if (nextToken.tokentype == TokenType.KW) {
            if (nextToken.value.equals(Keyword.LET.toString())) {
                this.tokenizer.consume(nextToken);

                Token identifierToken = this.tokenizer.confirm(TokenType.VAR);
                if (identifierToken == null) {
                    System.err.println("Expecting identifier token, got " + this.tokenizer.peek());
                    System.exit(1);
                    return null;
                }

                this.tokenizer.consume(TokenType.VAR);

                this.tokenizer.consume(Symbol.EQUALS.token);

                ExpressionNode value = this.parseExpression(true);

                return new LetNode(identifierToken.value, value);
            }

            if (nextToken.value.equals(Keyword.RETURN.toString())) {
                this.tokenizer.consume(Keyword.RETURN.toString(), TokenType.KW);

                return new ReturnNode(this.parseExpression(true));
            }
        }

        return this.parseExpression(true);
    }

    private ExpressionNode parseExpression(boolean canSplit) {
        ExpressionNode exp = parseExpressionAtom();

        while ((canSplit && this.tokenizer.confirm(TokenType.OP) != null) || //
            // can start binary
            (this.tokenizer.confirm(Symbol.OPEN_PAREN.token) != null) ||
            // can start call
            (this.tokenizer.confirm(Keyword.AS.toString(), TokenType.KW) != null) || // can make as
            (this.tokenizer.confirm(Symbol.DOT.token) != null) // can
            // make dot access
//                      || (canSplit && this.tokenizer.confirm(Symbol
//                      .OPEN_BRACKET.token) != null) // can make bracket access
        ) {
            if (canSplit && this.tokenizer.confirm(TokenType.OP) != null) {
                exp = makeBinary(exp, 0);
            }

            if (this.tokenizer.confirm(Keyword.AS.toString(), TokenType.KW) != null) {
                exp = makeAs(exp);
            }

            if (this.tokenizer.confirm(Symbol.OPEN_PAREN.token) != null) {
                exp = makeCall(exp);
            }

            if (this.tokenizer.confirm(Symbol.DOT.token) != null) {
                exp = makeDotAccess(exp);
            }
        }

        return exp;
    }

    private ExpressionNode parseExpressionAtom() {
        if (this.tokenizer.confirm(Symbol.OPEN_PAREN.token) != null) {
            this.tokenizer.consume(Symbol.OPEN_PAREN.token);
            ExpressionNode contents = this.parseExpression(true);

            if (this.tokenizer.confirm(TokenType.VAR) != null) {
                // if the next token is a var value, it's the beginning of a
                // fn literal
                return this.parseFnLiteral(contents);
            }

            this.tokenizer.consume(Symbol.CLOSE_PAREN.token);
            return contents;
        }

        // hang on to this for later
        Token nextToken = this.tokenizer.peek();

        if (this.tokenizer.confirm(Symbol.OPEN_BRACE.token) != null) {
            // distinguish between struct literal or struct type literal

            this.tokenizer.consume(Symbol.OPEN_BRACE.token);

            // consume first statement; what kind of expression it is depends
            // on what comes after
            StatementNode firstStatement = this.parseStatement();

            if (this.tokenizer.confirm(Symbol.COLON.token) != null) {
                // colon: struct literal (first statement must be
                // IdentifierNode)

                if (!(firstStatement instanceof IdentifierNode)) {
                    System.err.println("Expected IdentifierNode for first " + "entry in struct literal.");
                    System.exit(1);
                    return null;
                }

                return this.parseStructLiteral(((IdentifierNode) firstStatement).name);
            } else if (this.tokenizer.confirm(Symbol.SEMICOLON.token) != null) {
                // semicolon: code block

                return this.parseBlock(firstStatement);
            } else {
                // another expression: struct type literal

                if (!(firstStatement instanceof ExpressionNode)) {
                    System.err.println("Expected expression for first type in" + " struct type literal.");
                    System.exit(1);
                    return null;
                }

                return this.parseStructTypeLiteral((ExpressionNode) firstStatement);
            }


        }

        if (this.tokenizer.confirm(Symbol.OPEN_BRACKET.token) != null) {
            // TODO parse list literal
            System.err.println("Not yet implemented.");
            System.exit(1);
            return null;
        }

        if (this.tokenizer.confirm(Symbol.BANG.token) != null) {
            this.tokenizer.consume(Symbol.BANG.token);
            return new UnaryNode(Symbol.BANG, this.parseExpression(false));
        }

        // in Kythera-JS type literals were read in at this point; in the JVM
        // implementation
        // they are inserted with the runtime and not part of the parser

        if (this.tokenizer.confirm(TokenType.KW) != null) {
            this.tokenizer.consume(nextToken);

            switch (Keyword.valueOf(nextToken.value.toUpperCase())) {
                case TYPEOF:
                    ExpressionNode target = this.parseExpression(true);
                    return new TypeofNode(target);
                case IF:
                    ExpressionNode ifCondition = this.parseExpression(true);

                    BlockNode ifBody = this.parseBlock();

                    if (this.tokenizer.confirm(Keyword.ELSE.token) != null) {
                        // else block present
                        ExpressionNode ifElse;

                        this.tokenizer.consume(Keyword.ELSE.token);

                        if (this.tokenizer.confirm(Symbol.OPEN_BRACE.token) != null) {
                            // else only, block follows
                            ifElse = this.parseBlock();
                        } else if (this.tokenizer.confirm(Keyword.IF.token) != null) {
                            // else-if, if follows
                            ifElse = this.parseExpression(true);
                        } else {
                            System.err.println(this.tokenizer.peek().toString() + " is not valid after 'else'.");
                            System.exit(1);
                            return null;
                        }

                        return new IfNode(ifCondition, ifBody, ifElse);
                    } else {
                        // no else block
                        return new IfNode(ifCondition, ifBody);
                    }
                case WHILE:
                    ExpressionNode whileCondition = this.parseExpression(true);

                    BlockNode whileBody = this.parseBlock();

                    return new WhileNode(whileCondition, whileBody);
            }
        }

        // from this point forward, nodes are generated directly, not dispatched
        this.tokenizer.next();

        // literals
        switch (nextToken.tokentype) {
            case NUM:
                if (nextToken.value.contains(".")) {
                    // TODO implement trailing 'f' syntax for floating points
                    // return new DoubleLiteralNode(Double.parseDouble(nextToken.value));
                    return new FloatLiteralNode(Float.parseFloat(nextToken.value));
                } else {
                    return new IntLiteralNode(Integer.parseInt(nextToken.value));
                }
                // case STR:
                // return new StrLiteralNode(nextToken.value);
            case VAR:
                // insert built-in values
                switch (nextToken.value) {
                    case "true":
                        return BooleanLiteral.TRUE;
                    case "false":
                        return BooleanLiteral.FALSE;
                    case "unit":
                        return UnitLiteral.UNIT;
                    default:
                        return new IdentifierNode(nextToken.value, null);
                }
        }

        System.err.println("Unexpected token: " + nextToken.toString());
        System.exit(1);

        return null;
    }

    // sometimes parseBlock will be called with the first statement already
    // parsed
    private BlockNode parseBlock(StatementNode firstStatement) {
        List<StatementNode> body = new ArrayList<>();

        body.add(firstStatement);

        // TODO optional semi
        this.tokenizer.consume(Symbol.SEMICOLON.token);

        while (this.tokenizer.confirm(Symbol.CLOSE_BRACE.token) == null) {
            loadStatement(body);
        }

        this.tokenizer.consume(Symbol.CLOSE_BRACE.token);

        return new BlockNode(body);
    }

    private BlockNode parseBlock() {
        this.tokenizer.consume(Symbol.OPEN_BRACE.token);

        StatementNode firstStatement = this.parseStatement();

        return parseBlock(firstStatement);
    }


    private void loadStatement(List<StatementNode> statements) {
        StatementNode st = this.parseStatement();
        if (st == null) {
            System.err.println("Statement evaluation failed.");
            System.exit(1);
        }
        statements.add(st);

        // TODO optional semi
        if (this.tokenizer.confirm(Symbol.SEMICOLON.token) == null) {
            System.err.println("Expected semicolon but got " + this.tokenizer.peek());
            System.exit(1);
        }
        this.tokenizer.consume(Symbol.SEMICOLON.token);
    }

    private FnLiteralNode parseFnLiteral(ExpressionNode firstTypeExpression) {
        // because of the way functions are read in, the parser has already
        // consumed the first type expression and can just pass it in here.

        boolean firstRun = true;
        SortedMap<String, ExpressionNode> parameters = new TreeMap<String, ExpressionNode>();

        // opening parentheses and first type expression have already been
        // consumed

        while (this.tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
            ExpressionNode paramTypeExp;
            if (firstRun) {
                paramTypeExp = firstTypeExpression;
                firstRun = false;
            } else {
                paramTypeExp = this.parseExpression(true);
            }

            String paramName = this.tokenizer.confirm(TokenType.VAR).value;
            this.tokenizer.consume(TokenType.VAR);

            parameters.put(paramName, paramTypeExp);
            if (this.tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
                this.tokenizer.consume(Symbol.COMMA.token);
            }
        }

        this.tokenizer.consume(Symbol.CLOSE_PAREN.token);

        BlockNode body = this.parseBlock();

        return new FnLiteralNode(parameters, body);
    }

    // sometimes parseStructLiteral is called with the first identifier
    // already consumed
    private StructLiteralNode parseStructLiteral(String firstIdentifier) {
        StructTypeLiteralNode structType = new StructTypeLiteralNode();
        StructLiteralNode structResult = new StructLiteralNode(structType);

        HashMap<String, ExpressionNode> typeContents = structType.entryTypes;
        HashMap<String, ExpressionNode> resultContents = structResult.entries;

        boolean firstRun = true;

        while (this.tokenizer.confirm(Symbol.CLOSE_BRACE.token) == null) {
            String entryKey;
            if (firstRun) {
                entryKey = firstIdentifier;
                firstRun = false;
            } else {
                entryKey = this.tokenizer.next().value;
            }

            this.tokenizer.consume(Symbol.COLON.token);

            ExpressionNode entryValue = this.parseExpression(true);

            this.tokenizer.consume(Symbol.COMMA.token);

            typeContents.put(entryKey, entryValue.typeExp);
            resultContents.put(entryKey, entryValue);
        }

        this.tokenizer.consume(Symbol.CLOSE_BRACE.token);

        return structResult;
    }

    private StructLiteralNode parseStructLiteral() {
        this.tokenizer.consume(Symbol.OPEN_BRACE.token);

        Token t = this.tokenizer.next();

        if (t.tokentype != TokenType.VAR) {
            System.err.println("Expecting identifier for struct literal but " + "got " + t.toString());
            System.exit(1);
        }

        return this.parseStructLiteral(t.value);
    }

    private StructTypeLiteralNode parseStructTypeLiteral(ExpressionNode firstTypeExp) {
        StructTypeLiteralNode structType = new StructTypeLiteralNode();

        HashMap<String, ExpressionNode> entries = structType.entryTypes;

        boolean firstRun = true;

        while (this.tokenizer.confirm(Symbol.CLOSE_BRACE.token) == null) {
            ExpressionNode typeExp;
            if (firstRun) {
                typeExp = firstTypeExp;
                firstRun = false;
            } else {
                typeExp = this.parseExpression(true);
            }

            String entryKey = this.tokenizer.next().value;

            this.tokenizer.consume(Symbol.COMMA.token);

            entries.put(entryKey, typeExp);
        }

        this.tokenizer.consume(Symbol.CLOSE_BRACE.token);

        return structType;
    }

    private StructTypeLiteralNode parseStructTypeLiteral() {
        this.tokenizer.consume(Symbol.OPEN_BRACE.token);

        ExpressionNode firstTypeExp = this.parseExpression(true);

        return this.parseStructTypeLiteral(firstTypeExp);
    }

    private ExpressionNode makeBinary(ExpressionNode left, int currentPrecedence) {
        Token token = this.tokenizer.confirm(TokenType.OP);
        if (token != null) {
            Symbol op = Symbol.symbolOf(token.value);
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
                        System.err.println("Invalid operator for binary " + "operation: " + op.symbol);
                        System.exit(1);
                        binary = null;
                }

                return this.makeBinary(binary, currentPrecedence);
            }
        }

        return left;
    }

    private ExpressionNode makeAs(ExpressionNode exp) {
        this.tokenizer.consume(Keyword.AS.token);

        return new AsNode(exp, this.parseExpression(true));
    }

    private ExpressionNode makeCall(ExpressionNode exp) {
        List<ExpressionNode> arguments = new ArrayList<>();

        this.tokenizer.consume(Symbol.OPEN_PAREN.token);

        while (this.tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
            arguments.add(this.parseExpression(true));

            // allow last comma to be missing
            if (this.tokenizer.confirm(Symbol.CLOSE_PAREN.token) == null) {
                this.tokenizer.consume(Symbol.COMMA.token);
            }
        }

        this.tokenizer.consume(Symbol.CLOSE_PAREN.token);

        return new CallNode(exp, arguments);
    }

    private ExpressionNode makeDotAccess(ExpressionNode exp) {
        this.tokenizer.consume(Symbol.DOT.token);

        String memberName = this.tokenizer.next().value;

        return new DotAccessNode(exp, memberName);
    }

    private ExpressionNode makeBracketAccess(ExpressionNode exp) {
        return null;
    }

}
