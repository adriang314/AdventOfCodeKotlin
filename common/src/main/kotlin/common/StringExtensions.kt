package common

import java.util.*

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
