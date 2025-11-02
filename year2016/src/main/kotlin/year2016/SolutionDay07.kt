package year2016

import common.BaseSolution

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7

    private val ipAddresses = input().split("\r\n").map { IPAddress(it) }

    override fun task1(): String {
        return ipAddresses.count { it.supportsTransportLayerSnooping }.toString()
    }

    override fun task2(): String {
        return ipAddresses.count { it.supportsSuperSecretListening }.toString()
    }

    private data class IPAddress(val value: String) {

        val supportsTransportLayerSnooping = run {
            var index = 0
            var hasABBAOutsideBrackets = false
            var insideBrackets = false
            while (index < value.length - 3) {
                val char = value[index]
                when (char) {
                    '[' -> insideBrackets = true
                    ']' -> insideBrackets = false
                    else -> {
                        val segment = value.substring(index, index + 4)
                        if (segment[0] != segment[1] && segment[0] == segment[3] && segment[1] == segment[2]) {
                            if (insideBrackets) {
                                return@run false
                            } else {
                                hasABBAOutsideBrackets = true
                            }
                        }
                    }
                }
                index++
            }

            hasABBAOutsideBrackets
        }

        val supportsSuperSecretListening = run {
            var index = 0
            val abasOutsideBrackets = mutableSetOf<String>()
            val babsInsideBrackets = mutableSetOf<String>()
            var insideBrackets = false
            while (index < value.length - 2) {
                val char = value[index]
                when (char) {
                    '[' -> insideBrackets = true
                    ']' -> insideBrackets = false
                    else -> {
                        val segment = value.substring(index, index + 3)
                        if (segment[0] == segment[2] && segment[0] != segment[1]) {
                            if (insideBrackets) {
                                babsInsideBrackets.add(segment)
                            } else {
                                abasOutsideBrackets.add(segment)
                            }
                        }
                    }
                }
                index++
            }

            abasOutsideBrackets.any { aba ->
                val bab = "${aba[1]}${aba[0]}${aba[1]}"
                babsInsideBrackets.contains(bab)
            }
        }
    }
}