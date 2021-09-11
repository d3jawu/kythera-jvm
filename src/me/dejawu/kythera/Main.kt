@file:JvmName("Main")

package me.dejawu.kythera

import me.dejawu.kythera.stages.*
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val argMap = args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
        if (elem.startsWith("-")) Pair(map + (elem to emptyList()), elem)
        else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
    }.first

    val entryPoint = if (argMap[""] != null) {
        argMap[""]?.joinToString(" ")
    } else {
        "main"
    }

    val targetPlatform = if (argMap["-p"] != null) {
        argMap["-p"]?.joinToString(" ")
    } else {
        "none"
    }

    val outputPath = if (argMap["-o"] != null) {
        argMap["-o"]?.joinToString(" ")
    } else {
        when (targetPlatform) {
            "js" -> "js/out/out.js"
            "jvm" -> "out/production/kythera/$entryPoint.class"
            "none" -> ""
            "json" -> "out.json"
            else -> {
                System.err.println("Invalid target platform: $targetPlatform")
                exitProcess(1)
            }
        }
    }

    try {
        val content = Files.readString(Paths.get("./$entryPoint.ky"))
        println("Generating initial AST")
        val parser = Parser(content)
        var ast = parser.parse()

        for (st in ast) {
            st.print(0, System.out)
        }

        return

        /*
        println("Desugaring")
        val desugarer = Desugarer(ast)
        ast = desugarer.visit()

        // typeExps on AstNodes may still be null at this point

        // TODO link types to expressions (no null typeExps)
        println("Resolving types")
        val resolver = Resolver(ast)
        ast = resolver.visit()

        // type checking would happen here, but that won't be implemented in this version

        println("Final AST:")
        for (st in ast) {
            st.print(0, System.out)
        }

        // TODO optimize constants and reuse literals

        // TODO optimize statically known types into pre-defined classes

        // TODO optimize KytheraValues for primitives into JVM primitives

        // TODO optimize bytecode

        println("Generating output")
        val generator: Generator = when (targetPlatform) {
//            "js" -> JsGenerator(ast) // generate JS script to be bundled with JS runtime
//            "jvm" -> JvmGenerator(ast, entryPoint) // emits JVM Class file
            "none" -> { // generates an AST then stops.
                println("No target platform specified, exiting.")
                exitProcess(0)
            }
            else -> {
                System.err.println("Invalid platform: $targetPlatform")
                exitProcess(1)
            }
        }

        val output = generator.generate()

        println("Writing to output file: $outputPath")

        val fos = FileOutputStream(outputPath)
        fos.write(output)
        fos.close()

         */
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(1)
    }
}

fun printlnWithIndent(message: String, indent: Int, stream: PrintStream) {
    val output = "\t".repeat(0.coerceAtLeast(indent)) + message
    stream.println(output)
}
