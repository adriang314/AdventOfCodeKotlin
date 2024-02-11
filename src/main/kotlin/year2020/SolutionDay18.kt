package year2020

import common.BaseSolution

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18
    override val year = 2020

    override fun task1(): String {
        val result = expressions.sumOf { it.eval(false) }
        return result.toString()
    }

    override fun task2(): String {
        val result = expressions.sumOf { it.eval(true) }
        return result.toString()
    }

    private val expressions = input().split("\r\n").map { Expression(it) }

    data class Expression(val formula: String) {
        private val cleanedFormula = formula.replace(" ", "")

        fun eval(additionFirst: Boolean): Long {
            val formulaEvaluator = if (additionFirst) ::evalNoParenthesesAdditionFirst else ::evalNoParentheses

            val matches = anyFormulaInParenthesis.find(cleanedFormula) ?: return formulaEvaluator(cleanedFormula)
            val innerFormula = matches.groups[1]!!.value
            val cleanedInnerFormula = innerFormula.replace("(", "").replace(")", "")
            val innerFormulaValue = formulaEvaluator(cleanedInnerFormula)
            val newFormula = cleanedFormula.replaceFirst(innerFormula, innerFormulaValue.toString())
            return Expression(newFormula).eval(additionFirst)
        }

        companion object {

            private val anyFormulaInParenthesis = Regex("(\\([^()]*\\))")
            private val anyFormula = Regex("(\\d+)([+*])(\\d+)")
            private val addFormula = Regex("(\\d+)\\+(\\d+)")

            private fun evalNoParentheses(formula: String): Long {
                var currFormula = formula
                while (true) {
                    val matches = anyFormula.find(currFormula) ?: return currFormula.toLong()
                    val (arg1, operator, arg2) = matches.destructured
                    val innerFormulaValue = calc(arg1, arg2, operator.first())
                    currFormula = currFormula.replaceFirst("$arg1$operator$arg2", innerFormulaValue.toString())
                }
            }

            private fun evalNoParenthesesAdditionFirst(formula: String): Long {
                var currFormula = formula
                while (true) {
                    val matches = addFormula.find(currFormula) ?: return evalNoParentheses(currFormula)
                    val (arg1, arg2) = matches.destructured
                    val innerFormulaValue = calc(arg1, arg2, '+')
                    currFormula = currFormula.replaceFirst("$arg1+$arg2", innerFormulaValue.toString())
                }
            }

            private fun calc(num1: String, num2: String, operator: Char): Long {
                return when (operator) {
                    '+' -> num1.toLong() + num2.toLong()
                    '*' -> num1.toLong() * num2.toLong()
                    else -> throw Exception()
                }
            }
        }
    }
}