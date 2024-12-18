package year2024

import common.BaseSolution

fun main() = println(SolutionDay09().result())

class SolutionDay09 : BaseSolution() {

    override val day = 9
    
    private val diskMap = input()

    override fun task1(): String {
        val disk = Disk(diskMap)
        val result = disk.compress1()
        return result.toString()
    }

    override fun task2(): String {
        val disk = Disk(diskMap)
        val result = disk.compress2()
        return result.toString()
    }

    private data class Disk(val map: String) {
        private val blocks = map.mapIndexed { idx, size -> Block(size.digitToInt(), idx / 2, idx % 2 == 0) }
        private val blockSize = blocks.sumOf { it.size }

        fun compress1(): Long {
            val dataToCompress = blocks.asSequence().filter { it.hasData }
                .map { block -> createNumberSequence(block.size, block.data) }.flatten().toList()

            val compressedData = IntArray(dataToCompress.size)

            var writingIdx = 0
            var compressionIdx = dataToCompress.size - 1

            for (block in blocks) {
                var blockIdx = 0
                while (writingIdx < compressedData.size && blockIdx++ < block.size) {
                    compressedData[writingIdx++] = if (block.hasData) block.data else dataToCompress[compressionIdx--]
                }
            }

            return checksum(compressedData)
        }

        fun compress2(): Long {
            val compressedData = IntArray(blockSize)
            val freeSpaces = mutableSetOf<FreeSpace>()
            val filesToCompress = mutableListOf<File>()

            // fill initial data
            var positionIdx = 0
            for (block in blocks) {
                if (block.hasData) {
                    filesToCompress.add(File(block.size, block.data, positionIdx))
                    repeat(block.size) {
                        compressedData[positionIdx++] = block.data
                    }
                } else {
                    freeSpaces.add(FreeSpace(block.size, positionIdx))
                    positionIdx += block.size
                }
            }

            // compress data
            for (file in filesToCompress.reversed()) {
                freeSpaces
                    .filter { it.size >= file.size && it.positionIdx < file.positionIdx }
                    .minByOrNull { it.positionIdx }?.let { freeSpace ->
                        // move file to available free space
                        positionIdx = freeSpace.positionIdx
                        repeat(file.size) {
                            compressedData[positionIdx++] = file.data
                        }

                        // adjust free space
                        freeSpaces.remove(freeSpace)
                        val spaceLeft = freeSpace.size - file.size
                        if (spaceLeft >= 0)
                            freeSpaces.add(FreeSpace(spaceLeft, positionIdx))

                        // erase old place
                        var oldPositionIdx = file.positionIdx
                        repeat(file.size) {
                            compressedData[oldPositionIdx++] = 0
                        }
                    }
            }

            return checksum(compressedData)
        }

        private fun checksum(array: IntArray) = array.mapIndexed { index, v -> 1L * index * v }.sum()

        private fun createNumberSequence(size: Int, number: Int) =
            when (size) {
                0 -> throw RuntimeException("Cannot create empty sequence")
                1 -> sequenceOf(number)
                2 -> sequenceOf(number, number)
                3 -> sequenceOf(number, number, number)
                4 -> sequenceOf(number, number, number, number)
                5 -> sequenceOf(number, number, number, number, number)
                6 -> sequenceOf(number, number, number, number, number, number)
                7 -> sequenceOf(number, number, number, number, number, number, number)
                8 -> sequenceOf(number, number, number, number, number, number, number, number)
                9 -> sequenceOf(number, number, number, number, number, number, number, number, number)
                else -> throw RuntimeException("Not supported")
            }
    }

    private data class FreeSpace(val size: Int, val positionIdx: Int)

    private data class Block(val size: Int, val data: Int, val hasData: Boolean)

    private data class File(val size: Int, val data: Int, val positionIdx: Int) {
        init {
            require(size > 0)
            require(data >= 0)
            require(positionIdx >= 0)
        }
    }
}