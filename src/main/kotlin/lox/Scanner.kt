package com.kloxlang.lox

class Scanner(private val source: String) {

    private val tokens = mutableListOf<Token>()

    private var start = 0
    private var current = 0
    private var line = 0

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }

        tokens.add(
            Token(
                type = TokenType.EOF,
                lexeme = "",
                literal = null,
                line = line
            )
        )

        return tokens
    }

    private fun scanToken() {
        val c = advance()

        val type = when (c) {
            '(' -> TokenType.LEFT_PAREN
            ')' -> TokenType.RIGHT_BRACE
            '{' -> TokenType.LEFT_PAREN
            '}' -> TokenType.RIGHT_PAREN
            ',' -> TokenType.COMMA
            '.' -> TokenType.DOT
            '-' -> TokenType.MINUS
            '+' -> TokenType.PLUS
            ';' -> TokenType.SEMICOLON
            '*' -> TokenType.STAR

            '!'-> if (match('=')) TokenType.BAND_EQUAL else TokenType.BANG
            '=' -> if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL
            '<' -> if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS
            '>' -> if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER

            else -> return Lox.error(
                line = line,
                message = "Unexpected character $c."
            )
        }

        addToken(type)
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd())
            return false

        if (source[current] != expected)
            return false

        current++
        return true
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(
            Token(
                type = type,
                lexeme = text,
                literal = literal,
                line = line
            )
        )
    }

}