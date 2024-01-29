package year2020

import common.BaseSolution

fun main() = println(SolutionDay06().result())

class SolutionDay06 : BaseSolution() {

    override val day = 6
    override val year = 2020

    override fun task1(): String {
        val totalAnswers = groups.sumOf { it.answers1.size }
        return totalAnswers.toString()
    }

    override fun task2(): String {
        val totalAnswers = groups.sumOf { it.answers2.size }
        return totalAnswers.toString()
    }

    private val groups = input().split("\r\n\r\n").map { group ->
        Group(group.split("\r\n").map { Person(it) })
    }

    data class Group(val persons: List<Person>) {
        val answers1 = persons.map { it.answers }.flatten().toSet()
        val answers2 = persons.map { it.answers }.reduce { acc, chars -> acc.intersect(chars) }
    }

    data class Person(val answersTotal: String) {
        val answers = answersTotal.toSet()
    }
}