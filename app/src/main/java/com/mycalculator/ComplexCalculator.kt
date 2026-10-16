package com.mycalculator

import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser

object ComplexCalculator {

    fun evaluate(expressionString: String): String {
        if (expressionString.isBlank()) return "0"

        var formatted = expressionString
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
            .replace("√", "sqrt")

        // Auto balance parentheses
        val openParens = formatted.count { it == '(' }
        val closeParens = formatted.count { it == ')' }
        if (openParens > closeParens) {
            formatted += ")".repeat(openParens - closeParens)
        }

        mXparser.setDegreesMode()
        val expr = Expression(formatted)
        val result = expr.calculate()

        return if (result.isNaN()) {
            "Error"
        } else {
            formatNumber(result)
        }
    }

    fun computeMagnitude(a: Double, b: Double): Double {
        return Math.hypot(a, b)
    }

    fun computePhaseDegrees(a: Double, b: Double): Double {
        return Math.toDegrees(Math.atan2(b, a))
    }

    private fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }
}