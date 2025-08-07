package com.kloxlang.tool

import java.io.IOException
import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("args: ${args.toList()}")

    if (args.size != 1) {
        println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }


    val outputDir = args[0]

    defineAst(
        outputDir = outputDir,
        baseName = "Expr",
        types = listOf(
            "Binary: left Expr, operator Token, right Expr",
            "Grouping: expression Expr",
            "Literal: value Object",
            "Unary: operator Token, right Expr"
        )
    )
}

@Throws(IOException::class)
private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("package com.kloxlang.lox")
    writer.println()

    writer.println("interface $baseName {")
    writer.println()

    defineVisitor(writer, baseName, types)
    writer.println()

    // The AST classes.
    types.forEach { type ->
        val className = type.split(':')[0].trim()
        val fields = type.split(':')[1].trim()
        defineType(writer, baseName, className, fields)
        writer.println()
    }

    // The base accept method
    writer.println("    fun <R> accept(visitor: Visitor<R>): R")
    writer.println()

    writer.println("}")
    writer.close()
}

private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
    writer.println("    interface Visitor<R> {")

    types.forEach { type ->
        val typeName = type.split(':')[0].trim()
        writer.println("        fun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
    }

    writer.println("    }")
}

private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String) {
    writer.println("    data class $className(")
    val fields = fieldList.split(", ")
    fields.forEach { field ->
        val name = field.split(' ')[0]
        val type = field.split(' ')[1]
        writer.println("        val $name: $type,")
    }
    writer.println("    ): $baseName {")
    // Visitor pattern
    writer.println()
    writer.println("        override fun <R> accept(visitor: Visitor<R>): R {")
    writer.println("            return visitor.visit$className$baseName(this)")
    writer.println("        }")
    writer.println("")
    writer.println("    }")
}