package com.kloxlang.lox

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.io.path.Path
import kotlin.io.path.readBytes
import kotlin.system.exitProcess

var hadError = false

@Throws(IOException::class)
fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: klox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        runFile(args.first())
    } else {
        runPrompt()
    }
    println("Hello Klox!")
}

@Throws(IOException::class)
private fun runFile(path: String) {
    val bytes = Path(path).readBytes()
    run(String(bytes, Charset.defaultCharset()))
    // Indicate an error in the exit code.
    if (hadError)
        exitProcess(65)
}

@Throws(IOException::class)
private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")
        val line = reader.readLine() ?: break
        run(line)
        hadError = false
    }
}

private fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    tokens.forEach { token ->
        println(token)
    }
}

object Lox {
    fun error(line: Int, message: String) {
        report(line, "", message)
    }
}

private fun report(line: Int, where: String, message: String) {
    println("[Line $line] Error $where: $message")
    hadError = true
}
