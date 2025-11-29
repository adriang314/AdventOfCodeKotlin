package year2015

import common.BaseSolution

fun main() = println(SolutionDay11().result())

class SolutionDay11 : BaseSolution() {

    override val day = 11

    private val password1 = PasswordGenerator().nextPassword(input())
    private val password2 = PasswordGenerator().nextPassword(password1)

    override fun task1(): String {
        return password1
    }

    override fun task2(): String {
        return password2
    }

    private class PasswordGenerator {

        fun nextPassword(current: String): String {
            val password = current.toCharArray()
            do {
                incrementPassword(password)
            } while (!isValidPassword(password))
            return String(password)
        }

        private fun isValidPassword(password: CharArray): Boolean {
            return hasIncreasingStraight(password) && !containsInvalidChars(password) && hasTwoDifferentNonOverlappingPairs(password)
        }

        private fun incrementPassword(password: CharArray) {
            for (i in password.size - 1 downTo 0) {
                if (password[i] == 'z') {
                    password[i] = 'a'
                } else {
                    password[i]++
                    break
                }
            }
        }

        private fun hasIncreasingStraight(password: CharArray): Boolean {
            for (i in 0 until password.size - 2) {
                if (password[i + 1] == password[i] + 1 && password[i + 2] == password[i] + 2) {
                    return true
                }
            }
            return false
        }

        private fun containsInvalidChars(password: CharArray): Boolean {
            return password.any { it == 'i' || it == 'o' || it == 'l' }
        }

        private fun hasTwoDifferentNonOverlappingPairs(password: CharArray): Boolean {
            var pairCount = 0
            var i = 0
            while (i < password.size - 1) {
                if (password[i] == password[i + 1]) {
                    pairCount++
                    i += 2 // Skip the next character to avoid overlapping
                } else {
                    i++
                }
            }
            return pairCount >= 2
        }
    }
}