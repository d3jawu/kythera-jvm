package me.dejawu.kythera;

import me.dejawu.kythera.backend.Compiler;
import me.dejawu.kythera.frontend.Desugarer;
import me.dejawu.kythera.frontend.Parser;
import me.dejawu.kythera.frontend.node.StatementNode;

import java.io.FileOutputStream;
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

            // generate initial AST
            Parser parser = new Parser(content);
            List<StatementNode> ast = parser.parse();
            for (StatementNode st : ast) {
                st.print(0, System.out);
            }

            // remove syntactic sugar
            Desugarer desugarer = new Desugarer(ast);
            ast = desugarer.desugar();

            // TODO type check on final AST

            // TODO optimize constants

            // TODO optimize statically known types

            // TODO optimize primitives

            // generate bytecode
            Compiler compiler = new Compiler(ast, entryPoint);
            byte[] output = compiler.compile();

            System.out.println("Code generation succeeded, writing to: " + entryPoint + ".class");
            FileOutputStream fos = new FileOutputStream("out/" + entryPoint + ".class");
            fos.write(output);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void printlnWithIndent(String message, int indent, PrintStream stream) {
        String output = "\t".repeat(Math.max(0, indent)) + message;
        stream.println(output);
    }
}
