package solution

import org.paukov.combinatorics3.Generator

// 46778017609071 no
private val cache = mutableMapOf<String, Long>()

class SolutionDay12 : BaseSolution() {
    override val day = 12

    override fun task1(): String {
        return ""
    }

    override fun task2(): String {
        val rawLines = input().split("\r\n", "\n")
        val lines = rawLines.map {
            val split = it.split(" ")
            val types = split[0]
            val groups = split[1]
            val newTypes = "$types?$types?$types?$types?$types"
            val newGroups = """${groups},${groups},${groups},${groups},${groups}"""
            Line("$newTypes $newGroups")
           // Line(it)
        }

        val x = lines.map { it.result }
        return x.sum().toString()
    }


    data class Group(val index: Int, val size: Int)


    class Line(val line: String) {

        private var groupLine: String
        private var typesLine: String
        private val numberRegex = Regex("(\\d+)")
        private val groups: List<Group>
        val result: Long

        init {
            val split = line.split(" ")
            typesLine = split[0]
            groupLine = split[1]
            groups = numbers(numberRegex, groupLine).mapIndexed { index, i -> Group(index, i) }
            result = countForGroup(0, 0, 0)
            cache[line] = result
        }

        private fun Char?.damaged() = this == '#'
        private fun Char?.operational() = this == '.'
        private fun Char?.unknown() = this == '?'

        private fun countForGroup(
            currentTypeIdx: Int,
            currentGroupIdx: Int,
            damaged: Int
        ): Long {
            val currentType = typesLine.getOrNull(currentTypeIdx)
            val currentGroup = groups.getOrNull(currentGroupIdx)

            if (currentType == null && currentGroup == null)
                return 1
            if (currentType == null)
                return 0

            // check cache
          //  val line = typesLine.substring(currentTypeIdx)
          //  val group = if (groupLine.length >= currentGroupIdx * 2) groupLine.substring(currentGroupIdx * 2) else ""
          //  val cachedKey = "$line $group"

            //if (cache.containsKey(cachedKey) && damaged == 0)
             //   return cache[cachedKey]!!

            if (currentType.damaged()) {
                return handleDamaged(currentTypeIdx, currentGroupIdx, currentGroup, damaged)
            }

            if (currentType.operational()) {
                return handleOperational(currentTypeIdx, currentGroupIdx, damaged)
            }

            val ifDamaged = handleDamaged(currentTypeIdx, currentGroupIdx, currentGroup, damaged)
            val ifOperational = handleOperational(currentTypeIdx, currentGroupIdx, damaged)
            return ifDamaged + ifOperational
        }

        private fun handleDamaged(
            currentTypeIdx: Int,
            currentGroupIdx: Int,
            currentGroup: Group?,
            damaged: Int
        ): Long {
            if (currentGroup == null)
                return 0L
            if (damaged > currentGroup.size)
                return 0L
            if (damaged == currentGroup.size - 1) {

                val nextType = typesLine.getOrNull(currentTypeIdx + 1)
                if (nextType.operational() || nextType.unknown())
                    return countForGroup(currentTypeIdx + 2, currentGroupIdx + 1, 0)
                if (nextType.damaged())
                    return 0

                return countForGroup(currentTypeIdx + 1, currentGroupIdx + 1, 0)
            } else
                return countForGroup(currentTypeIdx + 1, currentGroupIdx, damaged + 1)
        }

        private fun handleOperational(
            currentTypeIdx: Int,
            currentGroupIdx: Int,
            damaged: Int
        ): Long {
            if (damaged > 0)
                return 0L

            return countForGroup(currentTypeIdx + 1, currentGroupIdx, damaged)
        }

        private fun numbers(regex: Regex, line: String): List<Int> {
            val match = regex.findAll(line)
            return match.toList().stream().map { it.groupValues[1].toInt() }.toList()
        }
    }
}