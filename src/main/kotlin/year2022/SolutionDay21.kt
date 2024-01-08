package year2022

import common.BaseSolution
import java.lang.Exception

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {
    override val day = 21
    override val year = 2022

    override fun task1(): String {
        val root = monkeys.first { it.name == "root" }
        executePossibleOperations(monkeys)
        return root.number.toString()
    }

    override fun task2(): String {
        val root = monkeysWithHuman.first { it.name == "root" }
        executePossibleOperations(monkeysWithHuman)
        val monkeysWithoutNumber = monkeysWithHuman.filter { it.number == null }
        val equations = monkeysWithoutNumber.mapNotNull { it.equation() }.joinToString(", ")
        val args = monkeysWithoutNumber.filter { it != root }.joinToString(",") { it.name }
        return "3740214169961 = Solve[{$equations}, {$args}]"
    }

    private var monkeys: List<Monkey>
    private var monkeysWithHuman: List<Monkey>
    private val regex = Regex("(\\w+): (.*)$")

    init {
        monkeys = getMonkeys()
        monkeysWithHuman = getMonkeys()
        monkeysWithHuman.first { it.name == "root" }.changeOperationToEquality()
        monkeysWithHuman.first { it.name == "humn" }.changeToFormula("humn = humn")
        mapMonkeys(monkeys)
        mapMonkeys(monkeysWithHuman)
    }

    private fun executePossibleOperations(monkeys: List<Monkey>) {
        while (true) {
            val availableOperations = monkeys.filter { it.canExecuteOperation() }
            if (availableOperations.isEmpty())
                return

            availableOperations.forEach { it.executeOperation() }
        }
    }

    private fun mapMonkeys(monkeys: List<Monkey>) {
        monkeys.forEach {
            if (!it.leftName.isNullOrEmpty())
                it.left = monkeys.first { monkey -> monkey.name == it.leftName }
            if (!it.rightName.isNullOrEmpty())
                it.right = monkeys.first { monkey -> monkey.name == it.rightName }
        }
    }

    private fun getMonkeys() = input().split("\r\n")
        .map {
            val (name, formula) = regex.find(it)!!.destructured
            Monkey(name, formula.toLongOrNull(), formula)
        }

    data class Monkey(val name: String, var number: Long?, var formula: String) {
        private var operation: Char?
        val leftName: String?
        val rightName: String?
        var left: Monkey? = null
        var right: Monkey? = null

        init {
            if (number == null) {
                leftName = formula.substring(0..<4)
                rightName = formula.substring(7..<11)
                operation = formula[5]
            } else {
                leftName = null
                rightName = null
                operation = null
            }
        }

        fun changeOperationToEquality() {
            formula = formula.replace(operation!!, '=')
            operation = '='
        }

        fun changeToFormula(newFormula: String) {
            formula = newFormula
            number = null
        }

        fun equation(): String? {
            if (number != null)
                return null

            var equation = when (name) {
                "root" -> formula.replace(operation!!.toString(), "==")
                "humn" -> null
                else -> "$name == $formula"
            }

            val leftNumber = left?.number
            if (leftNumber != null)
                equation = equation?.replace(leftName!!, leftNumber.toString())

            val rightNumber = right?.number
            if (rightNumber != null)
                equation = equation?.replace(rightName!!, rightNumber.toString())

            return equation
        }

        fun canExecuteOperation() = number == null && operation != '=' &&
                left?.number != null && right?.number != null

        fun executeOperation() {
            number = when (operation) {
                '+' -> left!!.number!! + right!!.number!!
                '-' -> left!!.number!! - right!!.number!!
                '*' -> left!!.number!! * right!!.number!!
                '/' -> left!!.number!! / right!!.number!!
                else -> throw Exception()
            }
        }
    }
}