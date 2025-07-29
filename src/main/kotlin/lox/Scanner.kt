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

            '/' ->
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) {
                        advance()
                    }

                    return
                } else {
                    TokenType.SLASH
                }

            ' ', '\r', '\t' -> return // Ignore whitespace.

            '\n' -> { line ++; return }

            '"' -> {
                string()
                return
            }

            else -> {
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                } else {
                    Lox.error(
                        line = line,
                        message = "Unexpected character $c."
                    )
                }
                return
            }
        }

        addToken(type)
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) {
            advance()
        }

        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type = type)
    }

    private fun number() {
        while (isDigit(peek())) {
            advance()
        }

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance()

            while (isDigit(peek())) {
                advance()
            }
        }

        addToken(
            type = TokenType.NUMBER,
            literal = source.substring(start, current).toDouble()
        )
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++
            advance()
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        // The closing ".
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(type = TokenType.STRING, literal = value)
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd())
            return false

        if (source[current] != expected)
            return false

        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd())
            return '\u0000' // '\0' or NUL character

        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length)
            return '\u0000' // '\0' or NUL character

        return source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' ||
                c in 'A'..'Z' ||
                c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
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

    companion object {
        private val keywords = mapOf(
            "and" to TokenType.AND,
            "class" to TokenType.CLASS,
            "else" to TokenType.ELSE,
            "false" to TokenType.FALSE,
            "for" to TokenType.FOR,
            "fun" to TokenType.FUN,
            "if" to TokenType.IF,
            "nil" to TokenType.NIL,
            "or" to TokenType.OR,
            "print" to TokenType.PRINT,
            "return" to TokenType.RETURN,
            "super" to TokenType.SUPER,
            "this" to TokenType.THIS,
            "true" to TokenType.TRUE,
            "var" to TokenType.VAR,
            "while" to TokenType.WHILE,
        )
    }

}