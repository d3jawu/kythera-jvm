package io.kwu.kythera;

import io.kwu.kythera.backend.Compiler;
import io.kwu.kythera.frontend.Parser;
import io.kwu.kythera.frontend.node.StatementNode;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String entryPoint;

        if (args.length != 1) {
            entryPoint = "scratch";
        } else {
            entryPoint = args[0];
        }

        try {
            String content = Files.readString(Paths.get("./" + entryPoint + ".ky"));
            Parser parser = new Parser(content);
            List<StatementNode> program = parser.parse();
            for (StatementNode st : program) {
                st.print(0, System.out);
            }

            Compiler compiler = new Compiler(program, entryPoint);
            byte[] output = compiler.compile();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void printlnWithIndent(String message, int indent,
                                         PrintStream stream) {
        String output = "\t".repeat(Math.max(0, indent)) +
            message;
        stream.println(output);
    }
}
