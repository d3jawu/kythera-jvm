package io.kwu.kythera.parser;

public class ParserException extends Exception {
    public ParserException(String message) {
        System.err.println("Error: " + message);
    }

    public ParserException(String message, int line, int col) {
        System.err.println("Error: " + message + " at " + line + "," + col);
    }
}
