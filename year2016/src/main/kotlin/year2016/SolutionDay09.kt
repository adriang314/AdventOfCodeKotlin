package year2016

import common.BaseSolution

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9

    private val compressedData = input()
    private val compressedDataDecoder = CompressedDataDecoder()

    override fun task1(): String {
        val result = compressedDataDecoder.decompressV1(compressedData)
        return result.toString()
    }

    override fun task2(): String {
        val result = compressedDataDecoder.decompressV2(compressedData)
        return result.toString()
    }

    private class CompressedDataDecoder() {

        fun decompressV1(target: String): Long = decompress(target) { dataToRepeat -> dataToRepeat.length.toLong() }

        fun decompressV2(target: String): Long = decompress(target) { dataToRepeat -> decompressV2(dataToRepeat) }

        private fun decompress(target: String, decompressRepeatedData: (String) -> Long): Long {
            var readingIndex = 0
            var length = 0L
            while (readingIndex < target.length) {
                val marker = Marker.find(target, readingIndex)
                if (marker?.startsAtBeginning == true) {
                    val dataToRepeat = target.substring(marker.startOfDataIndex, marker.startOfDataIndex + marker.charsToTake)
                    length += decompressRepeatedData(dataToRepeat) * marker.repeatCount
                    readingIndex = marker.startOfDataIndex + marker.charsToTake
                } else {
                    length++
                    readingIndex++
                }
            }

            return length
        }
    }

    private data class Marker(val charsToTake: Int, val repeatCount: Int, val startOfDataIndex: Int, val startsAtBeginning: Boolean) {
        companion object {
            private val markerRegex = """\((\d+)x(\d+)\)""".toRegex()

            fun find(data: String, fromIndex: Int): Marker? =
                markerRegex.find(data, fromIndex)?.let { match ->
                    val (charsToTake, repeatCount) = match.destructured
                    return Marker(charsToTake.toInt(), repeatCount.toInt(), match.range.last + 1, match.range.first == fromIndex)
                }
        }
    }
}