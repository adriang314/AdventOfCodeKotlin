package year2020

import common.BaseSolution

fun main() = println(SolutionDay02().result())

class SolutionDay02 : BaseSolution() {

    override val day = 2
    override val year = 2020

    override fun task1(): String {
        return passwords.count { it.valid1 }.toString()
    }

    override fun task2(): String {
        return passwords.count { it.valid2 }.toString()
    }

    private var passwords: List<Password>

    init {
        val regEx = Regex("^(\\d+)-(\\d+) (\\w): (\\w+)$")
        passwords = input().split("\r\n").map { line ->
            val (min, max, include, value) = regEx.find(line)!!.destructured
            val policy = PasswordPolicy(include.first(), min.toInt()..max.toInt())
            Password(value, policy)
        }
    }

    data class PasswordPolicy(val char: Char, val range: IntRange)

    data class Password(val value: String, val policy: PasswordPolicy) {
        val valid1 = value.count { it == policy.char } in policy.range

        val valid2 = (value[policy.range.first - 1] == policy.char) xor
                (value[policy.range.last - 1] == policy.char)
    }
}