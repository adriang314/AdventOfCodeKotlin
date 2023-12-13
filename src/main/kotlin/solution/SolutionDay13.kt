package solution

import java.lang.RuntimeException
import kotlin.math.min

class SolutionDay13 : BaseSolution() {

    override val day = 13

    override fun task1(): String {
        val result = mirrors.sumOf { it.value }
        return result.toString()
    }

    override fun task2(): String {
        val mirrorsWithSmudges = mirrors.map { it.findSmudge() }
        val result = mirrorsWithSmudges.sumOf { it.value }
        return result.toString()
    }

    private var mirrors: List<Mirror>

    init {
        val rawLines = input().split("\r\n", "\n")
        val currMirrors = mutableListOf<Mirror>()
        var currMirror = mutableListOf<String>()
        for (line in rawLines) {
            if (line != "") {
                currMirror.add(line)
            } else {
                currMirrors.add(Mirror(currMirror))
                currMirror = mutableListOf()
            }
        }
        if (currMirror.size > 0)
            currMirrors.add(Mirror(currMirror))

        mirrors = currMirrors
    }

    class Mirror(private val lines: List<String>, private val smudge: Pair<Int, Int>? = null) {
        private var horizontalReflection: Int?
        private var verticalReflection: Int?
        private var height: Int = lines.size
        private val length: Int = lines.first.length
        val value: Int

        init {
            var verticalOptions = listOf<Int>()
            for (idx in lines.indices) {
                verticalOptions = findVerticalOptions(lines[idx], verticalOptions, idx > 0)
            }
            verticalOptions = cleanVerticalOptions(lines, verticalOptions)
            if (smudge != null)
                verticalOptions = isSmudgeInVerticalReflection(verticalOptions)
            verticalReflection = verticalOptions.firstOrNull()

            var horizontalOptions = findHorizontalOptions(lines)
            horizontalOptions = cleanHorizontalOptions(lines, horizontalOptions)
            if (smudge != null)
                horizontalOptions = isSmudgeInHorizontalReflection(horizontalOptions)
            horizontalReflection = horizontalOptions.firstOrNull()

            val verticalValue = verticalReflection?.let { it + 1 } ?: 0
            val horizontalValue = horizontalReflection?.let { (it + 1) * 100 } ?: 0
            value = verticalValue + horizontalValue
        }

        fun findSmudge(): Mirror {
            for (i in 0..<height) {
                for (j in 0..<length) {
                    val linesWithSmudge = lines.mapIndexed { currIdx, currLine ->
                        if (currIdx == i) {
                            val currItem = currLine[j]
                            val smudge = if (currItem == '.') "#" else "."
                            currLine.replaceRange(j, j + 1, smudge)
                        } else currLine
                    }

                    val smudgedMirror = Mirror(linesWithSmudge, Pair(i, j))
                    if (smudgedMirror.hasReflection()) {
                        return smudgedMirror
                    }
                }
            }

            throw RuntimeException("Cannot find smudge")
        }

        private fun isSmudgeInVerticalReflection(options: List<Int>) =
            options.filter { isSmudgeInVerticalReflection(it) }

        private fun isSmudgeInVerticalReflection(idx: Int): Boolean {
            val diff = min(length - idx - 1, idx + 1)
            return smudge!!.second in idx - diff + 1..idx + diff
        }

        private fun isSmudgeInHorizontalReflection(options: List<Int>) =
            options.filter { isSmudgeInHorizontalReflection(it) }

        private fun isSmudgeInHorizontalReflection(idx: Int): Boolean {
            val diff = min(height - idx - 1, idx + 1)
            return smudge!!.first in idx - diff + 1..idx + diff
        }

        private fun hasReflection() = horizontalReflection != null || verticalReflection != null

        private fun cleanVerticalOptions(lines: List<String>, potentialReflections: List<Int>) =
            potentialReflections.filter { cleanVerticalOptions(lines, it) }

        private fun cleanVerticalOptions(lines: List<String>, idx: Int): Boolean {
            for (line in lines) {
                val thisItem = line.substring(0, idx + 1)
                val nextItem = line.substring(idx + 1, min(idx + 1 + thisItem.length, line.length)).reversed()
                if (thisItem.length == nextItem.length && thisItem != nextItem)
                    return false
                if (thisItem.length > nextItem.length && !thisItem.endsWith(nextItem)) {
                    return false
                }
            }
            return true
        }

        private fun cleanHorizontalOptions(lines: List<String>, potentialReflections: List<Int>) =
            potentialReflections.filter { cleanHorizontalOptions(lines, it) }

        private fun cleanHorizontalOptions(lines: List<String>, idx: Int): Boolean {
            for (i in 0..lines.size) {
                val thisItem = lines.getOrNull(idx - i)
                val nextItem = lines.getOrNull(idx + i + 1)
                if (thisItem == null || nextItem == null)
                    return true
                if (thisItem != nextItem)
                    return false
            }
            return true
        }

        private fun findVerticalOptions(line: String, allowedIdx: List<Int>, check: Boolean): List<Int> {
            val options = mutableListOf<Int>()
            for (i in 0..<line.length - 1) {
                val thisItem = line[i]
                val nextItem = line[i + 1]
                if (thisItem == nextItem && (!check || allowedIdx.contains(i))) {
                    options.add(i)
                }
            }
            return options
        }

        private fun findHorizontalOptions(lines: List<String>): List<Int> {
            val options = mutableListOf<Int>()
            for (i in 0..<lines.size - 1) {
                val thisItem = lines[i]
                val nextItem = lines[i + 1]
                if (thisItem == nextItem) {
                    options.add(i)
                }
            }
            return options
        }
    }
}
