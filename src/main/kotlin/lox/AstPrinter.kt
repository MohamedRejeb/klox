package com.kloxlang.lox

import kotlin.math.exp

class AstPrinter: Expr.Visitor<String> {

    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        if (expr.value == null)
            return "nil"

        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        return buildString {
            append("(")
            append(name)

            exprs.forEach { expr ->
                append(" ")
                append(expr.accept(this@AstPrinter))
            }

            append(")")
        }
    }

}

fun main() {
    val expression = Expr.Binary(
        left = Expr.Unary(
            operator = Token(TokenType.MINUS, "-", null, 1),
            right = Expr.Literal(123)
        ),
        operator = Token(TokenType.STAR, "*", null, 1),
        right = Expr.Grouping(
            expression = Expr.Literal(45.67)
        )
    )

    println(AstPrinter().print(expression))
}