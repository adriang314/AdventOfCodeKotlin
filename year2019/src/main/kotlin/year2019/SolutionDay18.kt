package year2019

import common.BaseSolution
import java.util.*

fun main() = println(SolutionDay18().result())

class SolutionDay18 : BaseSolution() {

    override val day = 18
    
    companion object {
        private fun convertToTiles(text: String, modifyMark: (Char, Int, Int) -> Char) =
            text.split("\r\n").mapIndexed { rowIdx, line ->
                line.mapIndexed { colIdx, mark ->
                    modifyMark(mark, rowIdx, colIdx).let { modifiedMark ->
                        Tile(Position(rowIdx, colIdx), TileType.from(modifiedMark), modifiedMark)
                    }
                }
            }.flatten()
    }

    private val tiles = Tiles(convertToTiles(input()) { mark, _, _ -> mark })

    private val tileQuarters: List<Tiles>

    init {

        //  ...      @#@
        //  .@.  =>  ###
        //  ...      @#@

        val entryPos = Position(
            tiles.entries.map { it.pos.rowIdx }.average().toInt(),
            tiles.entries.map { it.pos.colIdx }.average().toInt()
        )
        val entryRowIdx = entryPos.rowIdx
        val entryColIdx = entryPos.colIdx
        val maxRowIdx = tiles.map.maxOf { it.pos.rowIdx }
        val maxColIdx = tiles.map.maxOf { it.pos.colIdx }

        fun updateTileMark(mark: Char, rowIdx: Int, colIdx: Int) =
            if (tiles.entries.size == 4) mark
            else if (rowIdx == entryRowIdx && colIdx == entryColIdx) '#'
            else if (rowIdx == entryRowIdx && colIdx == entryColIdx + 1) '#'
            else if (rowIdx == entryRowIdx && colIdx == entryColIdx - 1) '#'
            else if (rowIdx == entryRowIdx - 1 && colIdx == entryColIdx) '#'
            else if (rowIdx == entryRowIdx - 1 && colIdx == entryColIdx + 1) '@'
            else if (rowIdx == entryRowIdx - 1 && colIdx == entryColIdx - 1) '@'
            else if (rowIdx == entryRowIdx + 1 && colIdx == entryColIdx) '#'
            else if (rowIdx == entryRowIdx + 1 && colIdx == entryColIdx + 1) '@'
            else if (rowIdx == entryRowIdx + 1 && colIdx == entryColIdx - 1) '@'
            else mark

        val tilesTopLeft = Tiles(convertToTiles(input(), ::updateTileMark)
            .filter { it.pos.rowIdx in 0..entryPos.rowIdx && it.pos.colIdx in 0..entryPos.colIdx })

        val tilesTopRight = Tiles(convertToTiles(input(), ::updateTileMark)
            .filter { it.pos.rowIdx in 0..entryPos.rowIdx && it.pos.colIdx in entryPos.colIdx..maxColIdx })

        val tilesBottomLeft = Tiles(convertToTiles(input(), ::updateTileMark)
            .filter { it.pos.rowIdx in entryPos.rowIdx..maxRowIdx && it.pos.colIdx in 0..entryPos.colIdx })

        val tilesBottomRight = Tiles(convertToTiles(input(), ::updateTileMark)
            .filter { it.pos.rowIdx in entryPos.rowIdx..maxRowIdx && it.pos.colIdx in entryPos.colIdx..maxColIdx })

        tileQuarters = listOf(tilesTopLeft, tilesTopRight, tilesBottomLeft, tilesBottomRight)
    }

    override fun task1(): String {
        val journeys = Journeys(tiles.entries.single(), tiles.keys)
        journeys.findBest()
        journeys.printStatus()
        val result = journeys.best!!.steps.toString()
        return result
    }

    override fun task2(): String {
        val journeys = RobotJourneys(tileQuarters)
        val result = journeys.findBest()
        return result.toString()
    }

//    #################################################################################
//    #.###.............##.##...#.#.....#####.#####j......................#.....###.###
//    #####.###########.#####.#.###.###.#######################.#########.#.###.#######
//    ###...#########.#.......#...#.#.#.###.######.############...##.##...#.#...#######
//    ###.#######################.#.###.#########################.#####.###.#.#########
//    #...#.#######.###.#.#...#...#.#...#.#####################...###.#.#p..#...#...###
//    #.###################.#.#.###.#.#########################.#######.#######.#.#.###
//    #...#####.#########r..#...#...#.......###################...#.###...##.##.#.#...#
//    ###.###############.#######.#########.#.###################.#######.#####.#.###.#
//    #...###....i..#####.......#....e###.#.###################.#.......#.......#...#.#
//    #.#####.#####.###########.###########.###########################.###########.#.#
//    #...###.##.##.#######.....##.########...###.....#####.#...#.#.....#.#.......#.#.#
//    ###.###.#####Y#######.#################.#.#.###.#######.#.###.#######.#####.#.#.#
//    #d#...#.#####...#.###...#############...###.#...#...#...#...#.##.##...##.##...#.#
//    #.###.#.#######.#######.#############.#####.#.###.#.#.#####.#.#####.###########.#
//    #...#.#.......#...#.#...#######.###...###...#.....#...#...#...#...#.#.#.####.##.#
//    ###.#.#######.###.###.#############.#####.#############.#.#####.#.#.#####.#####.#
//    #.#.#.###.###.#...#...###.#########.###.#.....##.###.##.#.##.##.#...#######..a..#
//    ###.#.#######.#.###.###############.#####.###.#########.#.#####.###########.#####
//    #...#.......#.#...#.#.#######.......#...#.#.#.....##.##.#.#.....###########.....#
//    #.#########.#.###.#U#########.#######.#.#.#######.#####.#.#.###################.#
//    #...#.......#q..#...#########.#.#.#...#.#.....#.#.#.....#...###.########.####...#
//    #.#.#.#########.#############.#####.###.#####.###.#.#########################.###
//    #.#...######.##.....###v#...#...#.#...#.#####g..#.#.#.#.....###########...###.#.#
//    #.#####.#.#########.###.#.#.###.#####.#.#.#####.#.#.###.###.###########.#.#.#.###
//    #.....#####...#...#...#...#...#.#.....#.#######.#...#...###.###########.#.###...#
//    #####.#####.#.#.#.###.#######.#.#.#####.#######.#####.#####.###########.#.#####.#
//    #.....#...#f#.H.#.....#.#...#n#...#.....#######.......###.#.#######.....#.###...#
//    #.#####.#.#.#############Z#.#.#####.#######################.#####.#.#####.###.###
//    #...#.G.#.#.....#.##.##...#...#####...#######.#####.........#..h###.#.#...#...###
//    ###.#.###.#####.#######.#############.#############.#########.#####.###.###.#####
//    #...#...#.##.##...##.##.#############.######.######...#.......#...#...#.....##.##
//    #.#####.#.#######.#####.#.###########.#.#########.###.#.#######.#.###.###########
//    #.#...#.#.....#.#...#...#####.###.###.###.#########...#.#.#.#...#.#.#.###.#######
//    #.#.#.#.#####.#####.#.###############.#############.###Q#####.###.###.#########.#
//    #.#.#.#...#...#...#...#.......#...#...#######...###.###...#.#...#...#.........###
//    #.#.#.###L#.###.#.#####.#.###.#.#.#.#########.#.###.#####.#####.###.#########.###
//    #...#...#.#.#...#...###t#...#...#.#.....#.....#...#.#.###.....#.###.#.#.....#..u#
//    #######.#.#.#.#####.#######.#####.#####.#.#######.#.#########.#.#.#.###.###.###.#
//    ###.###...#...##.##.........##.##......@#@#######...##.###.##...###....y#.#.....#
//    #################################################################################
//    #.....####.#####.##...........#########@#@........##.##...#####...###...##.##...#
//    #.###.#############.#####.###.#########.#########.#####.#C#####.#.#.#.#.#####.#.#
//    #.#...#...#####.....###...#.#.###.......#########.......#.#####.#.###.#.......#.#
//    #.#.###.#.#.###.#####.#.#####.###.#######################.###.#.#.###.#########.#
//    #.#.....#.#####.....###.#.#.#..c#.##.##.#####.######.##...#####.#...#...#####...#
//    #.#######.#########.###.#######.#.#####################.#######.###.###.#####.###
//    #.#####...#....x....###.......#...#...#.#.#####.#####...#.#...#.#l..###.#.###...#
//    #.#.###.###.#################.#####.#.###############.#####.#.#.#.###.#.#######.#
//    #.#####...#.##############.##...#...#...####.########.#.....#.#.#...###.###...#.#
//    #.#######.#.###################.#.#####.#############.#.#####.#.###.###.###.#.#.#
//    #.#####.#...#.#######.##.####.#...#####.#########...#.T.###.#.#.#.#.#...###.#...#
//    #.#####################################.#########.#.#########.#X###.#.#####.#####
//    #.###...##.##.######################.##.#########.#...#...###...#...#...###...###
//    #.###.#.###############.###.###########.#####.###.###.#.#.#######.#####.#####.###
//    #.###w#...#.#######.###################.#######...#.#.#.#.....#.#..........s#.###
//    #.#######.###########.#########.#######.#######.#####.#.#####.###############D###
//    #...S...#...###########################.###.....#.#...#.....#...#.....#######.###
//    #######.###.###########################.#.#.#######.#######.###.#.###.#######.###
//    #####.#.....###.####################.##.###.......#.#.#.#.#.#.#...#...#.....#.###
//    #######################################.#########.#.#######.#######.###.###.#.###
//    ###########.#####################.......#.....#...#...#.....#####.#.....#...#...#
//    #################################.#######.###.#.#####.#.#################.#####.#
//    ###.###########################.#.......#z###...#...#...#.###.#####...#.#.#####.#
//    #####################.#################.#.#######.#.###############.#.###.#.###.#
//    #########.##.##################.....#...#...#.#...#...#############.#.....#####.#
//    ###############################.###.#.#####.###.#####.#############.###########.#
//    #.#########.#############.......#.#.#.#####.#...#.....####.#####.##...#.#.....#.#
//    #########################.#########.#.#####.#.###.###################.###.###.#.#
//    ###.#...#######.#########.#######...#.#.###...###.##.########k#####.#...#.#.#...#
//    #####.#A###.#############.#.###.#.###.###########.###########.#########.#.#######
//    ###...#...#########.#####.#######.#...########.##.#######.B...#####.....#.......#
//    ###.#####.###############.###.###.#.#############.#######.#########.###########.#
//    ###.#.###.....###.#.....#...#####.#.#.#######.....###.....#########.........#.#.#
//    ###.#########.#####.###.###.#####.#.#########.#######.#####################.###.#
//    #.#.###.......#.....#.#.#...#####...#########.V.....#b..######.########.#.#.....#
//    ###.###.#######.#######.#.#########################.###.#########################
//    ###...#.......#.##.####.#.#.#####################..o#.O.#.#############.#########
//    #####.#######.#.#######.#.#############.#########.###.###########################
//    #####....m###...####.##...#######################.....#.####.##################.#
//    #################################################################################

    private class Robots(
        val robot1: Journey,
        val robot2: Journey,
        val robot3: Journey,
        val robot4: Journey,
    ) {
        fun totalKeys() =
            robot1.collectedKeys.plus(robot2.collectedKeys).plus(robot3.collectedKeys).plus(robot4.collectedKeys)
    }

    private class RobotJourneys(private val tileQuarters: List<Tiles>) {

        fun findBest(): Int {

            val robot1Journey = RobotJourney()
            val robot2Journey = RobotJourney()
            val robot3Journey = RobotJourney()
            val robot4Journey = RobotJourney()

            val robot1StartTile = tileQuarters[0].entries.single()
            val robot2StartTile = tileQuarters[1].entries.single()
            val robot3StartTile = tileQuarters[2].entries.single()
            val robot4StartTile = tileQuarters[3].entries.single()

            val robot1Start =
                Journey(robot1StartTile, 0, listOf(robot1StartTile), emptySet(), tileQuarters[0].keys)
            val robot2Start =
                Journey(robot2StartTile, 0, listOf(robot2StartTile), emptySet(), tileQuarters[1].keys)
            val robot3Start =
                Journey(robot3StartTile, 0, listOf(robot3StartTile), emptySet(), tileQuarters[2].keys)
            val robot4Start =
                Journey(robot4StartTile, 0, listOf(robot4StartTile), emptySet(), tileQuarters[3].keys)

            val robotsInit = Robots(robot1Start, robot2Start, robot3Start, robot4Start)
            val list = LinkedList(listOf(robotsInit))

            while (list.isNotEmpty()) {

                val current = list.pop()

                val totalCollectedKeys = current.totalKeys()

                val r1 = robot1Journey.moveUntilDoors(current.robot1, totalCollectedKeys)
                val r2 = robot2Journey.moveUntilDoors(current.robot2, totalCollectedKeys)
                val r3 = robot3Journey.moveUntilDoors(current.robot3, totalCollectedKeys)
                val r4 = robot4Journey.moveUntilDoors(current.robot4, totalCollectedKeys)

                if (r1.size == 1 && r1.single() === current.robot1 &&
                    r2.size == 1 && r2.single() === current.robot2 &&
                    r3.size == 1 && r3.single() === current.robot3 &&
                    r4.size == 1 && r4.single() === current.robot4
                ) {
                    continue
                }

                r1.forEach { rob1 ->
                    r2.forEach { rob2 ->
                        r3.forEach { rob3 ->
                            r4.forEach { rob4 ->
                                if (rob1 == current.robot1 && rob2 == current.robot2 &&
                                    rob3 == current.robot3 && rob4 == current.robot4
                                ) {
                                    // nothing changed skip
                                } else {
                                    list.add(Robots(rob1, rob2, rob3, rob4))
                                }
                            }
                        }
                    }

                }
            }

            val totalSteps =
                robot1Journey.best!!.steps +
                        robot2Journey.best!!.steps +
                        robot3Journey.best!!.steps +
                        robot4Journey.best!!.steps

            return totalSteps
        }
    }

    private class RobotJourney {

        var best: Journey? = null

        fun moveUntilDoors(startJourney: Journey, totalCollectedKeys: Set<Char>): List<Journey> {

            if (startJourney.hasAllKeys())
                return listOf(startJourney)

            val list = LinkedList(listOf(startJourney))

            val listBockedDoors = LinkedList<Journey>()

            while (list.isNotEmpty()) {

                val currentJourney = list.pop()

                val nextSteps = currentJourney.nextSteps()

                nextSteps.forEach { nextJourney ->

                    if (nextJourney.currentTile.type == TileType.DOOR &&
                        !totalCollectedKeys.contains(nextJourney.currentTile.mark.lowercaseChar()) &&
                        !nextJourney.collectedKeys.contains(nextJourney.currentTile.mark.lowercaseChar())
                    ) {
                        if (!listBockedDoors.contains(currentJourney))
                            listBockedDoors.add(currentJourney)
                    } else if (nextJourney.hasAllKeys()) {
                        reportCompletedJourney(nextJourney)
                        listBockedDoors.add(nextJourney)
                    } else {
                        list.add(nextJourney)
                    }
                }
            }

            val samePosition = listBockedDoors.groupBy { it.currentTile }
            val y = samePosition.mapValues { samePos ->
                samePos.value.filter { item ->

                    item.steps == samePos.value.filter { it.collectedKeys.size == item.collectedKeys.size }
                        .minOf { it.steps }
                }
            }

            val result = y.values.flatten()
            return result

        }

        fun reportCompletedJourney(journey: Journey) {
            if (best == null || best!!.steps > journey.steps)
                best = journey
        }
    }


    private class Journeys(entry: Tile, requiredKeys: Int) {
        val reports = mutableMapOf<Position, List<JourneyReport>>()
        val list = LinkedList(listOf(Journey(entry, 0, listOf(entry), emptySet(), requiredKeys, this)))
        var best: Journey? = null
        var iterations = 0L

        fun findBest() {
            while (hasAny()) {

                val currentJourney = getNext()

                currentJourney.nextSteps().forEach { nextJourney ->
                    if (nextJourney.hasAllKeys()) {
                        reportCompletedJourney(nextJourney)
                    } else if (nextJourney.currentTile.type == TileType.DOOR &&
                        !nextJourney.collectedKeys.contains(nextJourney.currentTile.mark.lowercaseChar())
                    ) {
                        // skip
                    } else {
                        addNew(nextJourney)
                    }
                }
            }
        }

        fun hasAny() = list.isNotEmpty()

        fun addNew(journey: Journey) = list.add(journey)

        fun getNext(): Journey {
            // print status
            if (++iterations % 5_000_000L == 0L)
                printStatus()

            return list.pop()
        }

        fun printStatus() =
            println("Size: ${list.size}, best: ${best?.steps}, ($iterations), ${list.minOfOrNull { it.steps }}")

        fun reportCompletedJourney(journey: Journey) {
            if (best == null || best!!.steps > journey.steps)
                best = journey
        }

        fun hasBetterJourney(position: Position, collectedKeys: Set<Char>, steps: Int): Boolean {
            return reports[position]?.any { it.steps <= steps && it.keys.containsAll(collectedKeys) } ?: false
        }

        fun report(position: Position, collectedKeys: Set<Char>, steps: Int) {
            reports.compute(position) { _, curr ->
                curr?.plus(JourneyReport(collectedKeys, steps)) ?: listOf(JourneyReport(collectedKeys, steps))
            }
        }
    }

    private data class JourneyReport(val keys: Set<Char>, val steps: Int)

    private class Journey(
        val currentTile: Tile,
        val steps: Int,
        private val visitedTiles: List<Tile>,
        val collectedKeys: Set<Char>,
        private val requiredKeys: Int,
        private val journeys: Journeys? = null,
    ) {
        override fun toString() = "$currentTile keys: $collectedKeys steps: $steps"

        private fun nextPositions(): List<Tile> {
            return currentTile.availableMoves()
                // cannot move to visited tiles until key is found
                .filter { tile -> !visitedTiles.any { it.pos == tile.pos } }
                .toList()
        }


        fun hasAllKeys() = collectedKeys.size == requiredKeys

        fun nextSteps(): List<Journey> {
            val nextPositions = nextPositions()

            if (nextPositions.isEmpty())
                return emptyList()

            return nextPositions.mapNotNull { nextTile ->
                val newKeyFound = nextTile.type == TileType.KEY && !collectedKeys.contains(nextTile.mark)
                // updating key collection with new key
                val newCollectedKeys = if (newKeyFound) collectedKeys.plus(nextTile.mark) else collectedKeys

                // updating key total collection with new key
                // val newTotalKeys = if (newKeyFound) totalCollectedKeys.plus(nextTile.mark) else totalCollectedKeys
                // zeroing visited tiles once key is found
                val newVisitedTiles = if (newKeyFound) listOf(nextTile) else visitedTiles.plus(nextTile)

                val hasBetterJourney =
                    journeys?.hasBetterJourney(nextTile.pos, newCollectedKeys, steps + 1) ?: false

                if (newKeyFound && hasBetterJourney)
                    null
                else {
                    if (newKeyFound)
                        journeys?.report(nextTile.pos, newCollectedKeys, steps + 1)
                    Journey(
                        nextTile,
                        steps + 1,
                        newVisitedTiles,
                        newCollectedKeys,
                        requiredKeys,
                        journeys
                    )
                }
            }
        }


        override fun equals(other: Any?): Boolean {
            return if (other is Journey)
                currentTile == other.currentTile && steps == other.steps && collectedKeys.size == other.collectedKeys.size
            else
                false
        }

        override fun hashCode(): Int {
            var result = currentTile.hashCode()
            result = 31 * result + steps
            result = 31 * result + collectedKeys.hashCode()
            return result
        }
    }

    private enum class TileType {
        WALL, SPACE, ENTRY, KEY, DOOR;

        companion object {
            fun from(c: Char): TileType {
                if (CharRange('a', 'z').contains(c))
                    return KEY
                else if (CharRange('A', 'Z').contains(c))
                    return DOOR
                return when (c) {
                    '#' -> WALL
                    '.' -> SPACE
                    '@' -> ENTRY
                    else -> throw RuntimeException("Unknown tile type")
                }
            }
        }
    }

    private data class Tiles(val map: List<Tile>) {
        init {
            // connect tiles
            map.forEach { entry ->
                entry.westTile =
                    map.firstOrNull { it.pos.rowIdx == entry.pos.rowIdx && it.pos.colIdx == entry.pos.colIdx - 1 }
                entry.eastTile =
                    map.firstOrNull { it.pos.rowIdx == entry.pos.rowIdx && it.pos.colIdx == entry.pos.colIdx + 1 }
                entry.northTile =
                    map.firstOrNull { it.pos.rowIdx == entry.pos.rowIdx + 1 && it.pos.colIdx == entry.pos.colIdx }
                entry.southTile =
                    map.firstOrNull { it.pos.rowIdx == entry.pos.rowIdx - 1 && it.pos.colIdx == entry.pos.colIdx }
            }
            // simplify crossroads
            var crossroadCount: Int? = null
            do {
                val crossroads = map.filter { it.isCrossroad() }
                if (crossroadCount == crossroads.size)
                    break

                crossroadCount = crossroads.size

                crossroads.forEach { crossroad ->
                    val availableMoves = crossroad.availableMoves().toList()
                    availableMoves.forEach { nextTile ->
                        if (isDeadEnd(crossroad, nextTile)) {
                            nextTile.changeToWall()
                        }
                    }
                }
            } while (true)

            // remove dead ends
            do {
                val deadEnds = map.filter {
                    it.availableMoves().count() == 1 && (it.type == TileType.SPACE || it.type == TileType.DOOR)
                }
                deadEnds.forEach { it.changeToWall() }

            } while (deadEnds.isNotEmpty())
        }

//        fun printMap() {
//            val minX = map.minOf { it.pos.rowIdx }
//            val minY = map.minOf { it.pos.colIdx }
//            val maxX = map.maxOf { it.pos.rowIdx }
//            val maxY = map.maxOf { it.pos.colIdx }
//
//            for (x in minX..maxX) {
//                for (y in minY..maxY) {
//                    val tile = map.single { it.pos.rowIdx == x && it.pos.colIdx == y }
//                    print(tile.mark)
//                }
//                println()
//            }
//        }

        private fun isDeadEnd(tile: Tile, nextTile: Tile): Boolean {
            if (nextTile.type == TileType.KEY || nextTile.type == TileType.DOOR || nextTile.type == TileType.ENTRY)
                return false

            val nextTileMoves = nextTile.availableMoves().filter { it != tile }.toList()
            if (nextTileMoves.isEmpty())
                return true
            if (nextTileMoves.size > 1)
                return false

            return isDeadEnd(nextTile, nextTileMoves.first())
        }

        val keys = map.count { it.type == TileType.KEY }

        val entries = map.filter { it.type == TileType.ENTRY }
    }

    private data class Tile(val pos: Position, var type: TileType, var mark: Char) {
        var northTile: Tile? = null
        var southTile: Tile? = null
        var eastTile: Tile? = null
        var westTile: Tile? = null

        fun changeToWall() {
            mark = '#'
            type = TileType.WALL
        }

        fun isCrossroad() = availableMoves().count() > 2

        fun availableMoves(): Sequence<Tile> =
            sequenceOf(northTile, southTile, westTile, eastTile).filterNotNull().filter { it.type != TileType.WALL }

        override fun toString() = "[${pos.rowIdx},${pos.colIdx}] $mark"
    }

    private data class Position(val rowIdx: Int, val colIdx: Int)
}