package common

import java.util.*

fun String.swapAtIndex(idx1: Int, idx2: Int): String {
    require(idx1 >= 0 && idx1 < this.length)
    require(idx2 >= 0 && idx2 < this.length)

    return if (idx1 == idx2) {
        this
    } else if (idx1 < idx2) {
        this.substring(0, idx1) + this[idx2] + this.substring(idx1 + 1, idx2) + this[idx1] + this.substring(idx2 + 1)
    } else {
        this.substring(0, idx2) + this[idx1] + this.substring(idx2 + 1, idx1) + this[idx2] + this.substring(idx1 + 1)
    }
}

fun String.swapFirstLetters(ch1: Char, ch2: Char): String {
    val fromIdx = this.indexOf(ch1)
    val toIdx = this.indexOf(ch2)

    require(fromIdx >= 0)
    require(toIdx >= 0)

    return if (ch1 == ch2) {
        this
    } else {
        swapAtIndex(fromIdx, toIdx)
    }
}

fun String.shiftRight(offset: Int): String {
    require(offset >= 0)
    return when (offset) {
        0 -> this
        else -> this.mapIndexed { index, ch -> Pair(this.adjustIndex(index + offset), ch) }.sortedBy { it.first }.map { it.second }.joinToString("")
    }
}

fun String.shiftLeft(offset: Int): String {
    require(offset >= 0)
    return when (offset) {
        0 -> this
        else -> {
            this.mapIndexed { index, ch -> Pair(this.adjustIndex(index - offset), ch) }.sortedBy { it.first }.map { it.second }.joinToString("")
        }
    }
}

fun String.reverse(fromIdx: Int, toIdx: Int): String {
    require(fromIdx >= 0 && fromIdx < this.length)
    require(toIdx >= 0 && toIdx < this.length)
    require(fromIdx <= toIdx)

    return this.substring(0, fromIdx) + this.substring(fromIdx, toIdx + 1).reversed() + this.substring(toIdx + 1)
}

fun String.move(fromIdx: Int, toIdx: Int): String {
    require(fromIdx >= 0 && fromIdx < this.length)
    require(toIdx >= 0 && toIdx < this.length)

    return when {
        fromIdx == toIdx -> this
        fromIdx < toIdx -> this.substring(0, fromIdx) + this.substring(fromIdx + 1.. toIdx) + this[fromIdx] + this.substring(toIdx + 1)
        else -> this.substring(0, toIdx) + this[fromIdx] + this.substring(toIdx, fromIdx) + this.substring(fromIdx + 1)
    }
}

fun String.adjustIndex(idx: Int): Int {
    if (idx >= 0 && idx < this.length) {
        return idx
    } else if (idx >= this.length) {
        return idx % this.length
    } else {
        var tmpIndex = idx
        while (tmpIndex < 0)
            tmpIndex += this.length
        return tmpIndex
    }
}

fun String.replaceOne(from: String, to: String): List<String> {
    var startIdx = 0
    val results = LinkedList<String>()
    do {
        val replacementIdx = this.indexOf(from, startIdx)
        if (replacementIdx >= 0) {
            val replaced = this.replaceRange(replacementIdx, replacementIdx + from.length, to)
            results.add(replaced)
            startIdx = replacementIdx + 1
        }
    } while (replacementIdx >= 0)

    return results
}

fun String.replaceAll(from: String, to: String): Pair<String, Int> {
    var currentString = this
    var count = 0
    do {
        val replacedString = currentString.replaceFirst(from, to)
        if (replacedString != currentString) {
            currentString = replacedString
            count++
        } else {
            break
        }
    } while (true)

    return Pair(currentString, count)
}
