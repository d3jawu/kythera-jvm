package io.kwu.kythera.parser.tokenizer;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.kwu.kythera.parser.tokenizer.TokenType.*;

public final class Tokenizer {
    interface Condition {
        boolean check(char c);
    }

    private Token currentToken;
    private InputStream inputStream;

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

    private void readToNewLine() {
        this.readWhile((char c) -> c != '\n');
        this.inputStream.next();
    }

    private Token tokenFromString() {
        return new Token(this.readEscaped('"'), STR);
    }

    private Token tokenFromNumber() {
        AtomicBoolean hasDot = new AtomicBoolean(false);
        // this might still not be thread safe
        String number = this.readWhile((char c) -> {
            if (c == '.') {
                if (hasDot.get()) {
                    return false; // two dots in a row, not a number
                }

                hasDot.set(true);
                return true;
            }

            return Tokenizer.isDigit(c);
        });

        return new Token(number, NUM);
    }

    private Token tokenFromIdent() {
        final String id = this.readWhile(Tokenizer::isIdent);
        return new Token(id, Tokenizer.isKeyword(id) ? KW : VAR);
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

        if (Tokenizer.isDigit(c)) {
            final Token t = this.tokenFromNumber();
            // this.insertAutoSemi();
            return t;
        }

        if (Tokenizer.isIdentStart(c)) {
            final Token t = this.tokenFromIdent();
            // this.insertAutoSemi();
            return t;
        }

        if(Tokenizer.isPunc(c)) {
            char p = this.inputStream.next();

            /*
            if(p == '}' || p == ')' || p == ']') {
                this.insertAutoSemi();
            }
            */
            return new Token(""+p, PUNC);
        }

        if(Tokenizer.isOp(c)) {
            return new Token(this.readWhile(Tokenizer::isOp), OP);
        }

        // TODO make error handling more informative, use proper Exceptions, show line/col numbers
        System.err.println("Error: cannot handle character: " + c);
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
        return Arrays.asList(Keyword.values()).contains(Keyword.valueOf(word.toUpperCase()));
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

    private static boolean isOp(char c) {
        return "+-*/%=&|<>!~".indexOf(c) >= 0;
    }

    private static boolean isPunc(char c) {
        return ",;(){}[]:.".indexOf(c) >= 0;
    }

    private static boolean isWhitespace(char c) {
        return " \t\n".indexOf(c) >= 0;
    }
}