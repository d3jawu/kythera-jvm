@file:JvmName("Main")
package me.dejawu.kythera

import me.dejawu.kythera.stages.*
import me.dejawu.kythera.stages.generators.*
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val entryPoint: String = if (args.size != 1) {
        "Scratch"
    } else {
        args[0]
    }
    try {
        val content = Files.readString(Paths.get("./$entryPoint.ky"))
        println("Generating initial AST")
        val parser = Parser(content)
        var ast = parser.parse()
        println("Desugaring")
        val desugarer = Desugarer(ast)
        ast = desugarer.visit()

        // typeExps on ExpressionNodes may still be null at this point

        // TODO link types to expressions (no null typeExps)
        println("Resolving types")
        val resolver = Resolver(ast)
        ast = resolver.visit()

        // TODO mark types as dynamic or statically known
        println("Type-checking")
        val typeChecker = TypeChecker(ast)
        ast = typeChecker.visit()

        // TODO check and verify scopes; identify capturing lambdas

        // TODO attach struct as first parameter to member methods
        println("Final AST:")
        for (st in ast) {
            st.print(0, System.out)
        }

        // TODO optimize constants and reuse literals

        // TODO optimize statically known types into pre-defined classes

        // TODO optimize KytheraValues for primitives into JVM primitives

        // TODO optimize bytecode
        println("Generating output")
        //            Generator generator = new JvmGenerator(ast, entryPoint);
        val generator: Generator = JsGenerator(ast)
        val output = generator.compile()
        when (generator) {
            is JvmGenerator -> {
                println("Writing to: $entryPoint.class")
                val fos = FileOutputStream("out/production/kythera/$entryPoint.class")
                fos.write(output)
                fos.close()
            }
            is JsGenerator -> {
                println("Writing to: $entryPoint.js")
                val fos = FileOutputStream("js/out.js")
                fos.write(output)
                fos.close()
            }
            else -> {
                System.err.println("No code generator is available.")
                exitProcess(1)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(1)
    }
}

fun printlnWithIndent(message: String, indent: Int, stream: PrintStream) {
    val output = "\t".repeat(0.coerceAtLeast(indent)) + message
    stream.println(output)
}
