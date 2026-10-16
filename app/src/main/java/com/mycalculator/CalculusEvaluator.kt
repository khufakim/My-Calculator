package com.mycalculator

import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser
import java.util.Locale

object CalculusEvaluator {

    fun evaluateDerivative(functionStr: String, xVal: String): String {
        if (functionStr.isBlank() || xVal.isBlank()) return "Error: Missing input"

        val formattedFunc = cleanExpression(functionStr)
        val formattedX = cleanExpression(xVal)

        // Syntax in mXparser: der( f(x), x, x0 )
        val query = "der($formattedFunc, x, $formattedX)"
        mXparser.setDegreesMode()
        val expr = Expression(query)
        val result = expr.calculate()

        return if (result.isNaN()) "Error" else formatResult(result)
    }

    fun evaluateIntegral(functionStr: String, lowerBound: String, upperBound: String): String {
        if (functionStr.isBlank() || lowerBound.isBlank() || upperBound.isBlank()) return "Error: Missing input"

        val formattedFunc = cleanExpression(functionStr)
        val a = cleanExpression(lowerBound)
        val b = cleanExpression(upperBound)

        // Syntax in mXparser: int( f(x), x, a, b )
        val query = "int($formattedFunc, x, $a, $b)"
        mXparser.setDegreesMode()
        val expr = Expression(query)
        val result = expr.calculate()

        return if (result.isNaN()) "Error" else formatResult(result)
    }

    fun evaluateFunction(functionStr: String, xVal: String): String {
        if (functionStr.isBlank() || xVal.isBlank()) return "Error: Missing input"

        val formattedFunc = cleanExpression(functionStr)
        val formattedX = cleanExpression(xVal)

        // Replace x with (formattedX)
        val query = "($formattedFunc)".replace("x", "($formattedX)")
        mXparser.setDegreesMode()
        val expr = Expression(query)
        val result = expr.calculate()

        return if (result.isNaN()) "Error" else formatResult(result)
    }

    private fun cleanExpression(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
            .replace("√", "sqrt")
    }

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            String.format(Locale.US, "%.6f", value).trimEnd('0').trimEnd('.')
        }
    }
}