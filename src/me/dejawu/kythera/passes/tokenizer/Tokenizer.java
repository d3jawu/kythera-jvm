package me.dejawu.kythera.passes.tokenizer;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Tokenizer {
    interface Condition {
        boolean check(char c);
    }

    private Token currentToken;
    private final InputStream inputStream;

    public Tokenizer(InputStream is) {
        this.currentToken = null;
        this.inputStream = is;
    }

    private String readWhile(Condition condition) {
        StringBuilder output = new StringBuilder();
        while (!this.inputStream.eof() && condition.check(this.inputStream.peek())) {
            output.append(this.inputStream.next());
        }
        return output.toString();
    }

    private String readEscaped(char end) {
        boolean escaped = false;
        StringBuilder output = new StringBuilder();

        this.inputStream.next();

        while (!this.inputStream.eof()) {
            char c = this.inputStream.next();

            if (escaped) {
                output.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == end) {
                break;
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }

    private Token tokenFromString() {
        return new Token(this.readEscaped('"'), TokenType.STR);
    }

    private Token tokenFromNumber() {
        String number = "";

        if (this.inputStream.peek() == '-') {
            number += "-";
            this.inputStream.next();
        }

        AtomicBoolean hasDot = new AtomicBoolean(false);
        // this might still not be thread safe
        number += this.readWhile((char c) -> {
            if (c == '.') {
                if (hasDot.get()) {
                    return false; // two dots in a row, not a number
                }

                hasDot.set(true);
                return true;
            }

            return Tokenizer.isDigit(c);
        });

        return new Token(number, TokenType.NUM);
    }

    private Token tokenFromIdent() {
        final String id = this.readWhile(Tokenizer::isIdent);
        return new Token(id, Tokenizer.isKeyword(id) ? TokenType.KW : TokenType.VAR);
    }

    // dispatch read
    private Token readNextToken() {
        this.readWhile(Tokenizer::isWhitespace);

        if (this.inputStream.eof()) {
            return null;
        }

        char c = this.inputStream.peek();

        // TODO skip // comment

        // TODO skip /**/ comment

        if (c == '"' || c == '\'') {
            final Token t = this.tokenFromString();
            // this.insertAutoSemi();
            return t;
        }

        if (Tokenizer.isDigit(c) || c == '-') {
            final Token t = this.tokenFromNumber();
            // this.insertAutoSemi();
            return t;
        }

        if (Tokenizer.isIdentStart(c)) {
            final Token t = this.tokenFromIdent();
            // this.insertAutoSemi();
            return t;
        }

        if (Tokenizer.isPunc(c)) {
            char p = this.inputStream.next();

            /*
            if(p == '}' || p == ')' || p == ']') {
                this.insertAutoSemi();
            }
            */
            return new Token("" + p, TokenType.PUNC);
        }

        if (Tokenizer.isOp(c)) {
            return new Token(this.readWhile(Tokenizer::isOp), TokenType.OP);
        }

        // TODO make error handling more informative, use proper Exceptions,
        //  show line/col numbers
        System.err.println("Error: cannot handle character: " + c);
        System.exit(1);
        return null;
    }


    /*
    private void insertAutoSemi() {

    }
    */

    public Token peek() {
        if (this.currentToken != null) {
            return this.currentToken;
        } else {
            return (this.currentToken = this.readNextToken());
        }
    }

    public Token next() {
        final Token token = this.currentToken;
        this.currentToken = null;
        if (token != null) {
            return token;
        } else {
            return this.readNextToken();
        }
    }

    public boolean eof() {
        return this.peek() == null;
    }

    private static boolean isKeyword(String word) {
        for (Keyword kw : Keyword.values()) {
            if (kw.name().toLowerCase().equals(word)) {
                return true;
            }
        }

        return false;
    }

    // alias for confirm(Token t)
    public Token confirm(String value, TokenType type) {
        return this.confirm(new Token(value, type));
    }

    public Token confirm(Token t) {
        if (this.eof()) {
            return null;
        }

        final Token next = this.peek();

        if (t.equals(next)) {
            return next;
        } else {
            return null;
        }
    }

    // confirm token type without checking value
    public Token confirm(TokenType tt) {
        if (this.eof()) {
            return null;
        }

        final Token next = this.peek();

        if (next.tokentype != tt) {
            return null;
        }

        return next;
    }

    // alias for consume(Token t)
    public void consume(String value, TokenType type) {
        this.consume(new Token(value, type));
    }

    // combines confirm + next
    public void consume(Token t) {
        if (this.confirm(t) != null) {
            this.next();
        } else {
            final Token next = this.peek();
            System.err.println("Expecting " + t.toString() + " but got " + next.toString() + " at " + this.inputStream.line() + "," + this.inputStream.col());
            System.exit(1);
        }
    }

    public void consume(TokenType tt) {
        if (this.confirm(tt) != null) {
            this.next();
        } else {
            final Token next = this.peek();
            System.err.println("Expecting " + tt.toString() + " but got " + next.toString() + " at " + this.inputStream.line() + "," + this.inputStream.col());
            System.exit(1);
        }
    }

    private static boolean isDigit(char c) {
        return ("" + c).matches("[0-9]");
    }

    private static boolean isIdentStart(char c) {
        return ("" + c).matches("(?i)[a-z_]");
    }

    private static boolean isIdent(char c) {
        return isIdentStart(c) || "_0123456789".indexOf(c) >= 0;
    }

    // the tokenizer treats ops and puncs differently
    // in the parser ops and puncs are treated the same, as Symbols

    // from the tokenizer's perspective ops are symbols that may be followed
    // by other symbols, e.g. '+='
    private static boolean isOp(char c) {
        return "+-*/%=&|<>!~".indexOf(c) >= 0;
    }

    // from the tokenizer's perspective puncs are symbols that only appear alone
    private static boolean isPunc(char c) {
        return ",;(){}[]:.".indexOf(c) >= 0;
    }

    private static boolean isWhitespace(char c) {
        return " \t\n".indexOf(c) >= 0;
    }
}