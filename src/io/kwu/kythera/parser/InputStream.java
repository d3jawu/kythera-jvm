package io.kwu.kythera.parser;

/**
 * An input stream that also keeps track of line and column position.
 * Ported almost verbatim from the JS implementation, can probably be
 * made more Java-idiomatic
 */
public final class InputStream {
    private String input;

    private int pos;

    private int line;
    private int col;

    public InputStream(String input) {
        this.input = input;

        this.pos = 0;
        this.line = 0;
        this.col = 0;
    }

    public char next() {
        final char c = this.input.charAt(this.pos);
        this.pos += 1;

        if(c == '\n') {
            this.line += 1;
            this.col = 0;
        } else {
            this.col += 1;
        }

        return c;
    }

    public char peek() {
        return this.input.charAt(this.pos);
    }

    public boolean eof() {
        return this.pos == this.input.length();
    }

    public void err(String message) {
        System.err.println(message + " at " + Integer.toString(line) + ":" + Integer.toString(col));
    }
}
