package year2022

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import year2022.SolutionDay20.DecryptedFile
import year2022.SolutionDay20.Number
import java.util.*
import kotlin.test.assertEquals

class Solution20Test {

    @ParameterizedTest
    @CsvSource(
        value = [
            "4 5 6 1 7 8 9,1,4 5 6 7 1 8 9",
            "4 -2 5 6 7 8 9,-2,4 5 6 7 8 -2 9",
            "1 2 -3 3 -2 0 4,1,2 1 -3 3 -2 0 4",
            "2 1 -3 3 -2 0 4,2,1 -3 2 3 -2 0 4",
            "1 -3 2 3 -2 0 4,-3,1 2 3 -2 -3 0 4",
            "1 2 3 -2 -3 0 4,3,1 2 -2 -3 0 3 4",
            "1 2 -2 -3 0 3 4,-2,1 2 -3 0 3 4 -2",
            "1 2 -3 0 3 4 -2,0,1 2 -3 0 3 4 -2",
            "1 2 -3 0 3 4 -2,4,1 2 -3 4 0 3 -2",

            "1 2 3 4,1,2 1 3 4",
            "1 2 3 4,2,1 3 4 2",
            "1 2 3 4,3,1 2 3 4",
            "1 2 3 4,4,1 4 2 3",

            "-1 -2 -3 -4,-1,-2 -3 -1 -4",
            "-1 -2 -3 -4,-2,-1 -3 -2 -4",
            "-1 -2 -3 -4,-3,-1 -2 -3 -4",
            "-1 -2 -3 -4,-4,-1 -2 -4 -3",

            "-4 -3 -2 -1,-1,-4 -3 -1 -2",
            "-4 -3 -2 -1,-2,-4 -3 -1 -2",
            "-4 -3 -2 -1,-3,-4 -3 -2 -1",
            "-4 -3 -2 -1,-4,-3 -2 -4 -1",
        ]
    )
    fun test(input: String, numberToMove: String, output: String) {
        val list = LinkedList(input.split(" ").mapIndexed { idx, i -> Number(idx, i.toLong()) })
        val file = DecryptedFile(list)
        val number = list.first { it.value == numberToMove.toLong() }
        file.move(number)

        assertEquals(output, file.toString())
    }
}