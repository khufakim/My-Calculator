package com.mycalculator

import java.util.Locale

object MatrixCalculator {

    fun determinant2x2(m: Array<DoubleArray>): Double {
        return m[0][0] * m[1][1] - m[0][1] * m[1][0]
    }

    fun determinant3x3(m: Array<DoubleArray>): Double {
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1]) -
                m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0]) +
                m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0])
    }

    fun transpose(m: Array<DoubleArray>): Array<DoubleArray> {
        val rows = m.size
        val cols = m[0].size
        val result = Array(cols) { DoubleArray(rows) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[j][i] = m[i][j]
            }
        }
        return result
    }

    fun add(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray>? {
        if (a.size != b.size || a[0].size != b[0].size) return null
        val rows = a.size
        val cols = a[0].size
        val result = Array(rows) { DoubleArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i][j] = a[i][j] + b[i][j]
            }
        }
        return result
    }

    fun subtract(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray>? {
        if (a.size != b.size || a[0].size != b[0].size) return null
        val rows = a.size
        val cols = a[0].size
        val result = Array(rows) { DoubleArray(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                result[i][j] = a[i][j] - b[i][j]
            }
        }
        return result
    }

    fun multiply(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray>? {
        if (a[0].size != b.size) return null
        val rowsA = a.size
        val colsA = a[0].size
        val colsB = b[0].size
        val result = Array(rowsA) { DoubleArray(colsB) }
        for (i in 0 until rowsA) {
            for (j in 0 until colsB) {
                var sum = 0.0
                for (k in 0 until colsA) {
                    sum += a[i][k] * b[k][j]
                }
                result[i][j] = sum
            }
        }
        return result
    }

    fun inverse2x2(m: Array<DoubleArray>): Array<DoubleArray>? {
        val det = determinant2x2(m)
        if (det == 0.0) return null
        return arrayOf(
            doubleArrayOf(m[1][1] / det, -m[0][1] / det),
            doubleArrayOf(-m[1][0] / det, m[0][0] / det)
        )
    }

    fun formatMatrix(m: Array<DoubleArray>): String {
        val sb = StringBuilder()
        for (row in m) {
            sb.append("[ ")
            for (value in row) {
                val formatted = if (value % 1.0 == 0.0) {
                    value.toLong().toString()
                } else {
                    String.format(Locale.US, "%.4f", value).trimEnd('0').trimEnd('.')
                }
                sb.append(formatted).append("\t")
            }
            sb.append("]\n")
        }
        return sb.toString().trimEnd()
    }
}