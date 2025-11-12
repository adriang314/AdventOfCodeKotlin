package year2016

import common.BaseSolution

fun main() = println(SolutionDay10().result())

class SolutionDay10 : BaseSolution() {

    override val day = 10

    private val simulation = Simulation()

    override fun task1(): String {
        val targetComparison = simulation.transfers.firstOrNull { it.lowerChip.id == 17 && it.higherChip.id == 61 }
        return targetComparison!!.fromBot.id.toString()
    }

    override fun task2(): String {
        val output0Chip = simulation.getOutput(0).chips.first()
        val output1Chip = simulation.getOutput(1).chips.first()
        val output2Chip = simulation.getOutput(2).chips.first()
        return (output1Chip.id * output2Chip.id * output0Chip.id).toString()
    }

    private inner class Simulation {
        private val bots = mutableMapOf<Int, Bot>()
        private val outputs = mutableMapOf<Int, Output>()
        val transfers = mutableListOf<ChipTransfer>()

        private val initCommands = input().split("\r\n").mapNotNull {
            Regex("""value (\d+) goes to bot (\d+)""").find(it)?.let { initMatch ->
                InitCommand(initMatch.groupValues[2].toInt(), initMatch.groupValues[1].toInt())
            }
        }

        private val transferCommands = input().split("\r\n").mapNotNull {
            Regex("""bot (\d+) gives low to (bot|output) (\d+) and high to (bot|output) (\d+)""").find(it)?.let { transferMatch ->
                val fromBotId = transferMatch.groupValues[1].toInt()
                val lowToBotId = if (transferMatch.groupValues[2] == "bot") transferMatch.groupValues[3].toInt() else null
                val lowToOutputId = if (transferMatch.groupValues[2] == "output") transferMatch.groupValues[3].toInt() else null
                val highToBotId = if (transferMatch.groupValues[4] == "bot") transferMatch.groupValues[5].toInt() else null
                val highToOutputId = if (transferMatch.groupValues[4] == "output") transferMatch.groupValues[5].toInt() else null

                TransferCommand(fromBotId, lowToBotId, lowToOutputId, highToBotId, highToOutputId)
            }
        }

        init {
            initCommands.forEach { getBot(it.botId).chips.add(Chip(it.chipId)) }

            var progress: Boolean
            do {
                progress = false
                transferCommands.forEach { command ->
                    tryTransfer(command)?.let {
                        transfers.add(it)
                        progress = true
                    }
                }
            } while (progress)
        }

        fun getBot(id: Int): Bot = bots.getOrPut(id) { Bot(id) }

        fun getOutput(id: Int): Output = outputs.getOrPut(id) { Output(id) }

        private fun tryTransfer(command: TransferCommand): ChipTransfer? {
            val bot = getBot(command.fromBotId)
            if (bot.chips.size > 2)
                throw IllegalStateException("Bot ${bot.id} has more than 2 chips!")
            if (bot.chips.size < 2)
                return null

            val lowerChip = bot.chips.first()
            val higherChip = bot.chips.last()
            command.lowToOutputId?.let { getOutput(it).chips.add(lowerChip) }
            command.lowToBotId?.let { getBot(it).chips.add(lowerChip) }
            command.highToOutputId?.let { getOutput(it).chips.add(higherChip) }
            command.highToBotId?.let { getBot(it).chips.add(higherChip) }
            bot.chips.clear()

            return ChipTransfer(bot, lowerChip, higherChip)
        }
    }

    private data class InitCommand(val botId: Int, val chipId: Int)

    private data class TransferCommand(
        val fromBotId: Int,
        val lowToBotId: Int?,
        val lowToOutputId: Int?,
        val highToBotId: Int?,
        val highToOutputId: Int?
    )

    private data class ChipTransfer(val fromBot: Bot, val lowerChip: Chip, val higherChip: Chip)

    private data class Bot(val id: Int, val chips: MutableSet<Chip> = sortedSetOf())

    private data class Chip(val id: Int) : Comparable<Chip> {
        override fun compareTo(other: Chip): Int = this.id.compareTo(other.id)
    }

    private data class Output(val id: Int, val chips: MutableSet<Chip> = sortedSetOf())
}