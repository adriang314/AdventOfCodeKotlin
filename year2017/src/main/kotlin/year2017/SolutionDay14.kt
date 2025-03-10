package year2017

import common.BaseSolution

fun main() = println(SolutionDay14().result())

class SolutionDay14 : BaseSolution() {

    override val day = 14

    private val map = Map.fromString(input())

    override fun task1(): String {
        return map.usedSquares.toString()
    }

    override fun task2(): String {
        return map.regionCount.toString()
    }

    private class Map(private val squares: Array<Array<Boolean>>) {
        val regionCount = calcRegions()
        val usedSquares = squares.sumOf { row -> row.count { it } }

        private fun calcRegions(): Int {
            val visited = Array(MAP_SIZE) { Array(MAP_SIZE) { false } }

            fun floodFill(x: Int, y: Int) {
                if (x !in 0..<MAP_SIZE || y !in 0..<MAP_SIZE || !squares[x][y] || visited[x][y])
                    return

                visited[x][y] = true
                floodFill(x + 1, y)
                floodFill(x - 1, y)
                floodFill(x, y + 1)
                floodFill(x, y - 1)
            }

            var regionCount = 0
            for (i in 0..<MAP_SIZE) {
                for (j in 0..<MAP_SIZE) {
                    if (squares[i][j] && !visited[i][j]) {
                        regionCount++
                        floodFill(i, j)
                    }
                }
            }

            return regionCount
        }

        companion object {
            private const val MAP_SIZE = 128

            fun fromString(key: String): Map {
                val squares = Array(MAP_SIZE) { Array(MAP_SIZE) { false } }

                for (i in 0..<MAP_SIZE) {
                    val hashInput = "$key-$i"
                    val knotHash = knotHash(hashInput)
                    val binaryString = knotHash.hexToBinary()
                    for (j in binaryString.indices) {
                        squares[i][j] = binaryString[j] == '1'
                    }
                }

                return Map(squares)
            }

            private fun String.hexToBinary(): String {
                return this.map { it.toString().toInt(16) }
                    .joinToString("") { it.toString(2).padStart(4, '0') }
            }

            private fun knotHash(input: String): String {
                val lengths = input.map { it.code } + listOf(17, 31, 73, 47, 23)
                val sparseHash = runRounds(lengths)
                val denseHash = computeDenseHash(sparseHash)
                return denseHash.joinToString("") { "%02x".format(it) }
            }

            private fun computeDenseHash(sparseHash: List<Int>): List<Int> {
                return sparseHash.chunked(16).map { block -> block.reduce { acc, num -> acc xor num } }
            }

            private fun runRounds(lengths: List<Int>): List<Int> {
                val listSize = 256
                val list = (0..<listSize).toMutableList()
                var currentPosition = 0
                var skipSize = 0

                repeat(64) {
                    for (length in lengths) {
                        if (length > listSize) continue

                        // Reverse the section of the list
                        val sublist = (0..<length).map { list[(currentPosition + it) % listSize] }.reversed()
                        for (i in 0..<length) {
                            list[(currentPosition + i) % listSize] = sublist[i]
                        }

                        // Move the current position forward and increase the skip size
                        currentPosition = (currentPosition + length + skipSize) % listSize
                        skipSize++
                    }
                }

                return list
            }
        }
    }
}