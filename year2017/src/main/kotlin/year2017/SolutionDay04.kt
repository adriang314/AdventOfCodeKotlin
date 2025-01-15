package year2017

import common.BaseSolution

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4

    private val passphrases = input().lines().map { line -> Passphrase(line.split(" ").map { Word(it) }) }

    override fun task1(): String {
        return passphrases.count { it.wordsAreNotEqual }.toString()
    }

    override fun task2(): String {
        return passphrases.count { it.wordsUseDifferentLetters }.toString()
    }

    private data class Passphrase(private val words: List<Word>) {
        val wordsAreNotEqual = words.groupBy { it }.all { it.value.size == 1 }
        val wordsUseDifferentLetters = words.map { it.letters }.groupBy { it }.all { it.value.size == 1 }
    }

    private data class Word(private val text: String) {
        val letters = text.toList().sorted()
    }
}