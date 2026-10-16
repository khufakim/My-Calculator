package com.mycalculator

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser

class MainActivity : AppCompatActivity() {

    private var currentExpression = ""
    private var isDegreeMode = true
    private var isResultDisplayed = false

    private lateinit var spinnerMode: Spinner
    private lateinit var tvExpression: TextView
    private lateinit var tvDisplay: TextView

    // Layout containers
    private lateinit var layoutScientific: View
    private lateinit var layoutComplex: View
    private lateinit var layoutMatrix: View
    private lateinit var layoutCalculus: View

    // Matrix input fields
    private lateinit var etA11: EditText
    private lateinit var etA12: EditText
    private lateinit var etA21: EditText
    private lateinit var etA22: EditText
    private lateinit var etB11: EditText
    private lateinit var etB12: EditText
    private lateinit var etB21: EditText
    private lateinit var etB22: EditText

    // Calculus input fields
    private lateinit var etFunction: EditText
    private lateinit var etXValue: EditText
    private lateinit var etLowerBound: EditText
    private lateinit var etUpperBound: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupSpinner()
        setupScientificKeypad()
        setupComplexKeypad()
        setupMatrixKeypad()
        setupCalculusKeypad()
    }

    private fun initViews() {
        spinnerMode = findViewById(R.id.spinnerMode)
        tvExpression = findViewById(R.id.tvExpression)
        tvDisplay = findViewById(R.id.tvDisplay)

        layoutScientific = findViewById(R.id.layoutScientific)
        layoutComplex = findViewById(R.id.layoutComplex)
        layoutMatrix = findViewById(R.id.layoutMatrix)
        layoutCalculus = findViewById(R.id.layoutCalculus)

        // Matrix
        etA11 = findViewById(R.id.etA11)
        etA12 = findViewById(R.id.etA12)
        etA21 = findViewById(R.id.etA21)
        etA22 = findViewById(R.id.etA22)
        etB11 = findViewById(R.id.etB11)
        etB12 = findViewById(R.id.etB12)
        etB21 = findViewById(R.id.etB21)
        etB22 = findViewById(R.id.etB22)

        // Calculus
        etFunction = findViewById(R.id.etFunction)
        etXValue = findViewById(R.id.etXValue)
        etLowerBound = findViewById(R.id.etLowerBound)
        etUpperBound = findViewById(R.id.etUpperBound)
    }

    private fun setupSpinner() {
        val modes = arrayOf("Scientific", "Complex", "Matrix", "Calculus")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, modes)
        spinnerMode.adapter = adapter

        spinnerMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                switchMode(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun switchMode(position: Int) {
        layoutScientific.visibility = if (position == 0) View.VISIBLE else View.GONE
        layoutComplex.visibility = if (position == 1) View.VISIBLE else View.GONE
        layoutMatrix.visibility = if (position == 2) View.VISIBLE else View.GONE
        layoutCalculus.visibility = if (position == 3) View.VISIBLE else View.GONE

        currentExpression = ""
        tvExpression.text = ""
        tvDisplay.text = "0"
    }

    // ===== 1. SCIENTIFIC KEYPAD =====
    private fun setupScientificKeypad() {
        val numberButtons = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2", R.id.btn3 to "3",
            R.id.btn4 to "4", R.id.btn5 to "5", R.id.btn6 to "6", R.id.btn7 to "7",
            R.id.btn8 to "8", R.id.btn9 to "9", R.id.btnDot to "."
        )
        numberButtons.forEach { (id, value) ->
            findViewById<Button>(id)?.setOnClickListener { onButtonClick(value) }
        }

        findViewById<Button>(R.id.btnAdd)?.setOnClickListener { onButtonClick("+") }
        findViewById<Button>(R.id.btnSub)?.setOnClickListener { onButtonClick("−") }
        findViewById<Button>(R.id.btnMul)?.setOnClickListener { onButtonClick("×") }
        findViewById<Button>(R.id.btnDiv)?.setOnClickListener { onButtonClick("÷") }
        findViewById<Button>(R.id.btnPct)?.setOnClickListener { onButtonClick("%") }

        findViewById<Button>(R.id.btnSin)?.setOnClickListener { onButtonClick("sin") }
        findViewById<Button>(R.id.btnCos)?.setOnClickListener { onButtonClick("cos") }
        findViewById<Button>(R.id.btnTan)?.setOnClickListener { onButtonClick("tan") }
        findViewById<Button>(R.id.btnLog)?.setOnClickListener { onButtonClick("log") }
        findViewById<Button>(R.id.btnLn)?.setOnClickListener { onButtonClick("ln") }
        findViewById<Button>(R.id.btnSqrt)?.setOnClickListener { onButtonClick("√") }
        findViewById<Button>(R.id.btnSq)?.setOnClickListener { onButtonClick("x²") }
        findViewById<Button>(R.id.btnPow)?.setOnClickListener { onButtonClick("xʸ") }
        findViewById<Button>(R.id.btnPi)?.setOnClickListener { onButtonClick("π") }
        findViewById<Button>(R.id.btnE)?.setOnClickListener { onButtonClick("e") }

        findViewById<Button>(R.id.btnOpenParen)?.setOnClickListener { onButtonClick("(") }
        findViewById<Button>(R.id.btnCloseParen)?.setOnClickListener { onButtonClick(")") }

        findViewById<Button>(R.id.btnClear)?.setOnClickListener { onButtonClick("C") }
        findViewById<Button>(R.id.btnBackspace)?.setOnClickListener { onButtonClick("⌫") }
        findViewById<Button>(R.id.btnEq)?.setOnClickListener { onButtonClick("=") }

        val btnMode = findViewById<Button>(R.id.btnMode)
        btnMode?.setOnClickListener {
            isDegreeMode = !isDegreeMode
            btnMode.text = if (isDegreeMode) "DEG" else "RAD"
        }
    }

    private fun onButtonClick(value: String) {
        if (isResultDisplayed && !isOperator(value)) {
            currentExpression = ""
        }
        isResultDisplayed = false

        when (value) {
            "C" -> currentExpression = ""
            "⌫" -> if (currentExpression.isNotEmpty()) currentExpression = currentExpression.dropLast(1)
            "=" -> {
                calculateScientificResult()
                return
            }
            "sin", "cos", "tan", "log", "ln", "√" -> currentExpression += "$value("
            "π" -> currentExpression += "π"
            "e" -> currentExpression += "e"
            "x²" -> currentExpression += "^2"
            "xʸ" -> currentExpression += "^"
            "+", "−", "×", "÷" -> {
                if (currentExpression.isNotEmpty() && isOperator(currentExpression.last().toString())) {
                    currentExpression = currentExpression.dropLast(1) + value
                } else if (currentExpression.isNotEmpty()) {
                    currentExpression += value
                }
            }
            else -> currentExpression += value
        }
        updateDisplay()
    }

    private fun isOperator(value: String): Boolean {
        return value == "+" || value == "−" || value == "×" || value == "÷" || value == "^"
    }

    private fun updateDisplay() {
        tvExpression.text = currentExpression
        tvDisplay.text = if (currentExpression.isEmpty()) "0" else currentExpression
    }

    private fun calculateScientificResult() {
        if (currentExpression.isEmpty()) return

        var mathExpression = currentExpression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
            .replace("√", "sqrt")

        if (isDegreeMode) {
            mXparser.setDegreesMode()
        } else {
            mXparser.setRadiansMode()
        }

        val openParens = mathExpression.count { it == '(' }
        val closeParens = mathExpression.count { it == ')' }
        if (openParens > closeParens) {
            mathExpression += ")".repeat(openParens - closeParens)
        }

        val expression = Expression(mathExpression)
        val result = expression.calculate()

        if (result.isNaN() || result.isInfinite()) {
            tvDisplay.text = "Error"
            currentExpression = ""
        } else {
            val formattedResult = if (result % 1.0 == 0.0) {
                result.toLong().toString()
            } else {
                String.format("%.8f", result).trimEnd('0').trimEnd('.')
            }

            tvDisplay.text = formattedResult
            currentExpression = formattedResult
            isResultDisplayed = true
        }
    }

    // ===== 2. COMPLEX KEYPAD =====
    private fun setupComplexKeypad() {
        val numberButtons = mapOf(
            R.id.btnCpx0 to "0", R.id.btnCpx1 to "1", R.id.btnCpx2 to "2", R.id.btnCpx3 to "3",
            R.id.btnCpx4 to "4", R.id.btnCpx5 to "5", R.id.btnCpx6 to "6", R.id.btnCpx7 to "7",
            R.id.btnCpx8 to "8", R.id.btnCpx9 to "9", R.id.btnCpxDot to "."
        )
        numberButtons.forEach { (id, value) ->
            findViewById<Button>(id)?.setOnClickListener { onCpxButtonClick(value) }
        }

        findViewById<Button>(R.id.btnCpxAdd)?.setOnClickListener { onCpxButtonClick("+") }
        findViewById<Button>(R.id.btnCpxSub)?.setOnClickListener { onCpxButtonClick("−") }
        findViewById<Button>(R.id.btnCpxMul)?.setOnClickListener { onCpxButtonClick("×") }
        findViewById<Button>(R.id.btnCpxDiv)?.setOnClickListener { onCpxButtonClick("÷") }
        findViewById<Button>(R.id.btnCpxPct)?.setOnClickListener { onCpxButtonClick("%") }

        findViewById<Button>(R.id.btnI)?.setOnClickListener { onCpxButtonClick("i") }
        findViewById<Button>(R.id.btnRe)?.setOnClickListener { onCpxButtonClick("re") }
        findViewById<Button>(R.id.btnIm)?.setOnClickListener { onCpxButtonClick("im") }
        findViewById<Button>(R.id.btnAbs)?.setOnClickListener { onCpxButtonClick("abs") }
        findViewById<Button>(R.id.btnArg)?.setOnClickListener { onCpxButtonClick("arg") }
        findViewById<Button>(R.id.btnConj)?.setOnClickListener { onCpxButtonClick("conj") }

        findViewById<Button>(R.id.btnCpxOpen)?.setOnClickListener { onCpxButtonClick("(") }
        findViewById<Button>(R.id.btnCpxClose)?.setOnClickListener { onCpxButtonClick(")") }

        findViewById<Button>(R.id.btnCpxClear)?.setOnClickListener { onCpxButtonClick("C") }
        findViewById<Button>(R.id.btnCpxBack)?.setOnClickListener { onCpxButtonClick("⌫") }
        findViewById<Button>(R.id.btnCpxEq)?.setOnClickListener { onCpxButtonClick("=") }
    }

    private fun onCpxButtonClick(value: String) {
        if (isResultDisplayed && !isOperator(value)) {
            currentExpression = ""
        }
        isResultDisplayed = false

        when (value) {
            "C" -> currentExpression = ""
            "⌫" -> if (currentExpression.isNotEmpty()) currentExpression = currentExpression.dropLast(1)
            "=" -> {
                val res = ComplexCalculator.evaluate(currentExpression)
                tvDisplay.text = res
                tvExpression.text = currentExpression
                if (res != "Error") {
                    currentExpression = res
                    isResultDisplayed = true
                }
                return
            }
            "re", "im", "abs", "arg", "conj" -> currentExpression += "$value("
            else -> currentExpression += value
        }
        updateDisplay()
    }

    // ===== 3. MATRIX KEYPAD =====
    private fun setupMatrixKeypad() {
        findViewById<Button>(R.id.btnMatDetA)?.setOnClickListener {
            val a = readMatrixA()
            if (a != null) {
                val det = MatrixCalculator.determinant2x2(a)
                tvExpression.text = "det(A)"
                tvDisplay.text = if (det % 1.0 == 0.0) det.toLong().toString() else det.toString()
            } else {
                tvDisplay.text = "Error: Invalid Input"
            }
        }

        findViewById<Button>(R.id.btnMatTransA)?.setOnClickListener {
            val a = readMatrixA()
            if (a != null) {
                val t = MatrixCalculator.transpose(a)
                tvExpression.text = "Aᵀ"
                tvDisplay.text = MatrixCalculator.formatMatrix(t)
            } else {
                tvDisplay.text = "Error: Invalid Input"
            }
        }

        findViewById<Button>(R.id.btnMatInvA)?.setOnClickListener {
            val a = readMatrixA()
            if (a != null) {
                val inv = MatrixCalculator.inverse2x2(a)
                if (inv != null) {
                    tvExpression.text = "A⁻¹"
                    tvDisplay.text = MatrixCalculator.formatMatrix(inv)
                } else {
                    tvDisplay.text = "Error: Singular Matrix"
                }
            } else {
                tvDisplay.text = "Error: Invalid Input"
            }
        }

        findViewById<Button>(R.id.btnMatAdd)?.setOnClickListener {
            val a = readMatrixA()
            val b = readMatrixB()
            if (a != null && b != null) {
                val res = MatrixCalculator.add(a, b)
                if (res != null) {
                    tvExpression.text = "A + B"
                    tvDisplay.text = MatrixCalculator.formatMatrix(res)
                }
            } else {
                tvDisplay.text = "Error: Invalid Input"
            }
        }

        findViewById<Button>(R.id.btnMatSub)?.setOnClickListener {
            val a = readMatrixA()
            val b = readMatrixB()
            if (a != null && b != null) {
                val res = MatrixCalculator.subtract(a, b)
                if (res != null) {
                    tvExpression.text = "A − B"
                    tvDisplay.text = MatrixCalculator.formatMatrix(res)
                }
            } else {
                tvDisplay.text = "Error: Invalid Input"
            }
        }

        findViewById<Button>(R.id.btnMatMul)?.setOnClickListener {
            val a = readMatrixA()
            val b = readMatrixB()
            if (a != null && b != null) {
                val res = MatrixCalculator.multiply(a, b)
                if (res != null) {
                    tvExpression.text = "A × B"
                    tvDisplay.text = MatrixCalculator.formatMatrix(res)
                }
            } else {
                tvDisplay.text = "Error: Invalid Input"
            }
        }
    }

    private fun readMatrixA(): Array<DoubleArray>? {
        return try {
            val a11 = etA11.text.toString().toDouble()
            val a12 = etA12.text.toString().toDouble()
            val a21 = etA21.text.toString().toDouble()
            val a22 = etA22.text.toString().toDouble()
            arrayOf(doubleArrayOf(a11, a12), doubleArrayOf(a21, a22))
        } catch (e: Exception) {
            null
        }
    }

    private fun readMatrixB(): Array<DoubleArray>? {
        return try {
            val b11 = etB11.text.toString().toDouble()
            val b12 = etB12.text.toString().toDouble()
            val b21 = etB21.text.toString().toDouble()
            val b22 = etB22.text.toString().toDouble()
            arrayOf(doubleArrayOf(b11, b12), doubleArrayOf(b21, b22))
        } catch (e: Exception) {
            null
        }
    }

    // ===== 4. CALCULUS KEYPAD =====
    private fun setupCalculusKeypad() {
        findViewById<Button>(R.id.btnDerive)?.setOnClickListener {
            val fn = etFunction.text.toString()
            val xVal = etXValue.text.toString()
            tvExpression.text = "d/dx [$fn] at x=$xVal"
            tvDisplay.text = CalculusEvaluator.evaluateDerivative(fn, xVal)
        }

        findViewById<Button>(R.id.btnIntegrate)?.setOnClickListener {
            val fn = etFunction.text.toString()
            val a = etLowerBound.text.toString()
            val b = etUpperBound.text.toString()
            tvExpression.text = "∫ [$fn] dx from $a to $b"
            tvDisplay.text = CalculusEvaluator.evaluateIntegral(fn, a, b)
        }

        findViewById<Button>(R.id.btnEvalFunc)?.setOnClickListener {
            val fn = etFunction.text.toString()
            val xVal = etXValue.text.toString()
            tvExpression.text = "f($xVal) for f(x)=$fn"
            tvDisplay.text = CalculusEvaluator.evaluateFunction(fn, xVal)
        }
    }
}