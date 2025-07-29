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
        TODO()
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

}