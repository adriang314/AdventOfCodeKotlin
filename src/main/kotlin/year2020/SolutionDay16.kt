package year2020

import common.BaseSolution

fun main() = println(SolutionDay16().result())

class SolutionDay16 : BaseSolution() {

    override val day = 16
    override val year = 2020

    override fun task1(): String {
        val scanningErrorRate = nearbyTickets.sumOf { ticket ->
            ticket.values.filter { value -> fields.all { !it.contains(value) } }.sum()
        }
        return scanningErrorRate.toString()
    }

    override fun task2(): String {
        val fieldMapping = mutableMapOf<Field, MutableList<Int>>()
        fields.forEach { field ->
            for (i in fields.indices) {
                val fieldValues = validNearbyTickets.map { ticket -> ticket.values[i] }
                if (field.contains(fieldValues)) {
                    fieldMapping[field] = fieldMapping[field].orEmpty().plus(i).toMutableList()
                }
            }
        }

        do {
            fieldMapping.filter { it.value.size == 1 }.forEach { singleChoiceMapping ->
                val mapping = singleChoiceMapping.value.first()
                fieldMapping.filter { it != singleChoiceMapping }.forEach { it.value.remove(mapping) }
            }
        } while (fieldMapping.filter { it.value.size > 1 }.isNotEmpty())

        val departureFields = fields.filter { it.name.startsWith("departure") }
        val departureFieldIndices = fieldMapping.filter { departureFields.contains(it.key) }.map { it.value.first() }
        val myTicketDeparture = myTicket.values.filterIndexed { index, _ -> departureFieldIndices.contains(index) }
        val result = myTicketDeparture.scan(1L) { acc: Long, i: Int -> acc * i }.last()
        return result.toString()
    }

    private var fields: List<Field>
    private val myTicket: Ticket
    private val nearbyTickets: List<Ticket>
    private val validNearbyTickets: List<Ticket>

    init {
        val parts = input().split("\r\n\r\n")
        fields = parts[0].split("\r\n").map { field ->
            val fieldParts = field.split(": ")
            val fieldName = fieldParts[0]
            val fieldRanges = fieldParts[1].split(" or ")
            val fieldRange1Parts = fieldRanges[0].split("-")
            val fieldRange1 = fieldRange1Parts[0].toInt()..fieldRange1Parts[1].toInt()
            val fieldRange2Parts = fieldRanges[1].split("-")
            val fieldRange2 = fieldRange2Parts[0].toInt()..fieldRange2Parts[1].toInt()
            Field(fieldName, fieldRange1, fieldRange2)
        }

        val myTicketParts = parts[1].split("\r\n").drop(1).first().split(",").map { it.toInt() }
        myTicket = Ticket(myTicketParts)

        val nearbyTicketParts = parts[2].split("\r\n").drop(1)
        nearbyTickets = nearbyTicketParts.map { values -> Ticket(values.split(",").map { it.toInt() }) }

        validNearbyTickets =
            nearbyTickets.filter { ticket -> ticket.values.all { value -> fields.any { it.contains(value) } } }
    }

    data class Ticket(val values: List<Int>)

    data class Field(val name: String, val range1: IntRange, val range2: IntRange) {
        fun contains(value: Int) = value in range1 || value in range2
        fun contains(values: List<Int>) = values.all { contains(it) }
    }
}