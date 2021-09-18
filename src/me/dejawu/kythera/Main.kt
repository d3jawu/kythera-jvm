@file:JvmName("Main")

package me.dejawu.kythera

import me.dejawu.kythera.stages.*
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
        "main.ky"
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
                throw Exception("Invalid target platform: $targetPlatform")
            }
        }
    }

    try {
        val content = Files.readString(Paths.get("./$entryPoint"))
        println("Generating initial AST")
        val parser = Parser(content)
        var ast = parser.parse()

        println("Desugaring")
        val desugarer = Desugarer(ast)
        ast = desugarer.visit()

        println("Final AST:")
        for (st in ast) {
            println(st.toString())
        }

        return
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(1)
    }
}