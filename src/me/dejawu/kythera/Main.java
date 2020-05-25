package me.dejawu.kythera;

import me.dejawu.kythera.passes.CodeGenerator;
import me.dejawu.kythera.passes.Desugarer;
import me.dejawu.kythera.passes.Parser;
import me.dejawu.kythera.passes.TypeChecker;
import me.dejawu.kythera.ast.StatementNode;

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

            System.out.println("Generating initial AST");
            Parser parser = new Parser(content);
            List<StatementNode> ast = parser.parse();

            System.out.println("Desugaring");
            Desugarer desugarer = new Desugarer(ast);
            ast = desugarer.visit();

            // typeExps on ExpressionNodes may still be null at this point

            // TODO link types to expressions (no null typeExps)

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
            CodeGenerator codeGenerator = new CodeGenerator(ast, entryPoint);
            byte[] output = codeGenerator.compile();

            System.out.println("Writing to: " + entryPoint + ".class");
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
