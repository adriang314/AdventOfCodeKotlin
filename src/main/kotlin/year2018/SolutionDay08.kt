package year2018

import common.BaseSolution

fun main() = println(SolutionDay08().result())

class SolutionDay08 : BaseSolution() {

    override val day = 8
    override val year = 2018

    private val license = LicenseData(input().split(" ").map { it.toInt() })

    override fun task1(): String {
        return license.sumOfMeta().toString()
    }

    override fun task2(): String {
        return license.valueOfRoot().toString()
    }

    private data class LicenseData(val data: List<Int>) {
        private val root = Node()

        init {
            readNode(0, null, root)
        }

        fun valueOfRoot() = root.value()

        fun sumOfMeta(): Int {
            val meta = mutableListOf<Int>()
            getAllMeta(root, meta)
            return meta.sum()
        }

        private fun getAllMeta(node: Node, meta: MutableList<Int>) {
            meta.addAll(node.meta)
            node.childNodes.forEach { getAllMeta(it, meta) }
        }

        private fun readNode(offset: Int, parentNode: Node?, currNode: Node? = null): Int {
            var currOffset = offset
            val childNodeCount = data[currOffset++]
            val metaCount = data[currOffset++]
            val node = currNode ?: Node()

            parentNode?.childNodes?.add(node)

            repeat(childNodeCount) {
                currOffset = readNode(currOffset, node)
            }

            repeat(metaCount) {
                node.meta.add(data[currOffset++])
            }

            return currOffset
        }
    }

    private class Node {
        val childNodes = mutableListOf<Node>()
        val meta = mutableListOf<Int>()

        fun value(): Int {
            if (childNodes.isEmpty())
                return meta.sum()
            return meta.filter { it > 0 }.sumOf { childNodes.getOrNull(it - 1)?.value() ?: 0 }
        }
    }
}