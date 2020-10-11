package me.dejawu.kythera;

import me.dejawu.kythera.ast.StatementNode;
import me.dejawu.kythera.stages.*;
import me.dejawu.kythera.stages.generators.*;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String entryPoint;

        if (args.length != 1) {
            entryPoint = "Scratch";
        } else {
            entryPoint = args[0];
        }

        try {
            String content = Files.readString(Paths.get("./" + entryPoint + ".ky"));

            System.out.println("Generating initial AST");
            Parser parser = new Parser(content);
            List<StatementNode> ast = parser.parse();

            System.out.println("Desugaring");
            Desugarer desugarer = new Desugarer(ast);
            ast = desugarer.visit();

            // typeExps on ExpressionNodes may still be null at this point

            // TODO link types to expressions (no null typeExps)
            System.out.println("Resolving types");
            Resolver resolver = new Resolver(ast);
            ast = resolver.visit();

            // TODO mark types as dynamic or statically known

            System.out.println("Type-checking");
            TypeChecker typeChecker = new TypeChecker(ast);
            ast = typeChecker.visit();

            // TODO check and verify scopes; identify capturing lambdas

            // TODO attach struct as first parameter to member methods

            System.out.println("Final AST:");
            for (StatementNode st : ast) {
                st.print(0, System.out);
            }

            // TODO optimize constants and reuse literals

            // TODO optimize statically known types into pre-defined classes

            // TODO optimize KytheraValues for primitives into JVM primitives

            // TODO optimize bytecode

            System.out.println("Generating bytecode");
//            Generator generator = new JvmGenerator(ast, entryPoint);
            Generator generator = new JsGenerator(ast);
            byte[] output = generator.compile();

            if(generator instanceof JvmGenerator) {
                System.out.println("Writing to: " + entryPoint + ".class");
                FileOutputStream fos = new FileOutputStream("out/production/kythera/" + entryPoint + ".class");
                fos.write(output);
                fos.close();
            } else if(generator instanceof JsGenerator) {
                System.out.println("Writing to: " + entryPoint + ".js");
                FileOutputStream fos = new FileOutputStream("js/out.js");
                fos.write(output);
                fos.close();
            } else {
                System.err.println("No code generator is available.");
                System.exit(1);
            }

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
