package year2022

import common.BaseSolution

fun main() = println(SolutionDay19().result())

class SolutionDay19 : BaseSolution() {

    override val day = 19
    
    override fun task1(): String {
        val maxPerBluePrint = playGame(blueprints, 24)
        val result = maxPerBluePrint.sumOf { it.first.id * it.second }
        return result.toString()
    }

    override fun task2(): String {
        val maxPerBluePrint = playGame(blueprints.take(3), 32)
        val result = maxPerBluePrint.map { it.second }.reduce { acc: Int, i: Int -> acc * i }
        return result.toString()
    }

    private var blueprints: List<Blueprint>

    private fun playGame(blueprints: List<Blueprint>, turns: Int): List<Pair<Blueprint, Int>> {
        return blueprints.map { blueprint ->
            var currTurns: Collection<TurnInfo> = listOf(TurnInfo(0, Resources(blueprint)))
            print("playing blueprint ${blueprint.id} ... ")
            for (i in 1..turns) {
                print(" $i[${currTurns.size}] ")
                currTurns = currTurns.map { it.next() }.flatten()
                currTurns = cutPoorTurns(currTurns)
            }

            val max = currTurns.maxOf { it.resources.geode }
            println("game over - max for ${blueprint.id} $max")
            Pair(blueprint, max)
        }
    }

    private fun cutPoorTurns(currTurns: List<TurnInfo>) =
        currTurns.groupBy { it.resources.availableRobotsIndex() }
            .mapValues { entry ->
                entry.value
                    .sortedByDescending { it.resources.availableResourcesIndex() }
                    .takeLast(if (entry.value.size > 10000) entry.value.size / 100 else entry.value.size)
            }
            .values.flatten()

    init {
        val blueprintRegex = Regex("Blueprint (\\d+):")
        val oreRobotRegex = Regex("Each ore robot costs (\\d+) ore.")
        val clayRobotRegex = Regex("Each clay robot costs (\\d+) ore.")
        val obsidianRobotRegex = Regex("Each obsidian robot costs (\\d+) ore and (\\d+) clay.")
        val geodeRobotRegex = Regex("Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
        blueprints = input().split("\r\n").map {

            val (id) = blueprintRegex.find(it)!!.destructured
            val (oreRobotOreCost) = oreRobotRegex.find(it)!!.destructured
            val (clayRobotOreCost) = clayRobotRegex.find(it)!!.destructured
            val (obsidianRobotOreCost, obsidianRobotClayCost) = obsidianRobotRegex.find(it)!!.destructured
            val (geodeRobotOreCost, geodeRobotObsidianCost) = geodeRobotRegex.find(it)!!.destructured

            Blueprint(
                id.toInt(),
                OreRobotCost(oreRobotOreCost.toInt()),
                ClayRobotCost(clayRobotOreCost.toInt()),
                ObsidianRobotCost(obsidianRobotOreCost.toInt(), obsidianRobotClayCost.toInt()),
                GeodeRobotCost(geodeRobotOreCost.toInt(), geodeRobotObsidianCost.toInt())
            )
        }
    }

    class TurnInfo(private val number: Int, val resources: Resources) {

        fun next(): List<TurnInfo> {
            val nextNumber = number + 1
            return mutableListOf<TurnInfo>().apply {
                this.add(TurnInfo(nextNumber, resources.copy().collect()))
                resources.canBuild().forEach {
                    val nextTurnResources = resources.copy().collect()
                    when (it) {
                        CanBuild.OreRobot -> nextTurnResources.tryBuildOreRobot()
                        CanBuild.ClayRobot -> nextTurnResources.tryBuildClayRobot()
                        CanBuild.ObsidianRobot -> nextTurnResources.tryBuildObsidianRobots()
                        CanBuild.GeodeRobot -> nextTurnResources.tryBuildGeodeRobot()
                    }

                    this.add(TurnInfo(nextNumber, nextTurnResources))
                }
            }
        }
    }

    enum class CanBuild { OreRobot, ClayRobot, ObsidianRobot, GeodeRobot; }

    data class Resources(
        val blueprint: Blueprint,
        var ore: Int = 0,
        var oreRobots: Int = 1,
        var clay: Int = 0,
        var clayRobots: Int = 0,
        var obsidian: Int = 0,
        var obsidianRobots: Int = 0,
        var geode: Int = 0,
        var geodeRobots: Int = 0,
    ) {
        fun availableRobotsIndex() = (1000 * geodeRobots) + (100 * obsidianRobots) + (10 * clayRobots) + oreRobots
        fun availableResourcesIndex() = (1000 * geode) + (100 * obsidian) + (10 * clay) + ore

        fun collect(): Resources {
            ore += oreRobots
            clay += clayRobots
            obsidian += obsidianRobots
            geode += geodeRobots
            return this
        }

        fun canBuild(): List<CanBuild> {
            val available = mutableListOf<CanBuild>()
            if (canBuildOreRobot())
                available.add(CanBuild.OreRobot)
            if (canBuildClayRobot())
                available.add(CanBuild.ClayRobot)
            if (canBuildObsidianRobot())
                available.add(CanBuild.ObsidianRobot)
            if (canBuildGeodeRobot())
                available.add(CanBuild.GeodeRobot)
            return available
        }

        private fun canBuildOreRobot() =
            ore >= blueprint.oreRobotCost.ore

        private fun canBuildClayRobot() =
            ore >= blueprint.clayRobotCost.ore

        private fun canBuildObsidianRobot() =
            ore >= blueprint.obsidianRobotCost.ore && clay >= blueprint.obsidianRobotCost.clay

        private fun canBuildGeodeRobot() =
            ore >= blueprint.geodeRobotCost.ore && obsidian >= blueprint.geodeRobotCost.obsidian

        fun tryBuildOreRobot() {
            if (canBuildOreRobot()) {
                ore -= blueprint.oreRobotCost.ore
                oreRobots++
            }
        }

        fun tryBuildClayRobot() {
            if (canBuildClayRobot()) {
                ore -= blueprint.clayRobotCost.ore
                clayRobots++
            }
        }

        fun tryBuildObsidianRobots() {
            if (canBuildObsidianRobot()) {
                ore -= blueprint.obsidianRobotCost.ore
                clay -= blueprint.obsidianRobotCost.clay
                obsidianRobots++
            }
        }

        fun tryBuildGeodeRobot() {
            if (canBuildGeodeRobot()) {
                ore -= blueprint.geodeRobotCost.ore
                obsidian -= blueprint.geodeRobotCost.obsidian
                geodeRobots++
            }
        }
    }

    data class Blueprint(
        val id: Int,
        val oreRobotCost: OreRobotCost,
        val clayRobotCost: ClayRobotCost,
        val obsidianRobotCost: ObsidianRobotCost,
        val geodeRobotCost: GeodeRobotCost
    )

    data class OreRobotCost(val ore: Int)

    data class ClayRobotCost(val ore: Int)

    data class ObsidianRobotCost(val ore: Int, val clay: Int)

    data class GeodeRobotCost(val ore: Int, val obsidian: Int)
}