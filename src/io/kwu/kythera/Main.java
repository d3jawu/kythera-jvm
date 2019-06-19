package io.kwu.kythera;

import io.kwu.kythera.parser.InputStream;

public class Main {

    public static void main(String[] args) {
        InputStream is = new InputStream("hello world\nhow are you");

        while(!is.eof()) {
            System.out.println(">" + is.next());
        }
    }
}
