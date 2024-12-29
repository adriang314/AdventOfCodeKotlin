package year2018

import common.*

fun main() = println(SolutionDay15().result())

class SolutionDay15 : BaseSolution() {

    override val day = 15

    override fun task1(): String {
        return simulateCombat(3).toString()
    }

    override fun task2(): String {
        return simulateCombat(25).toString()
    }

    private fun simulateCombat(elfAttackPower: Int): Long {
        val map = Grid(input()) { c, position -> Square(position, c, { Elf(elfAttackPower) }, { Goblin() }) }
        val combat = Combat(map)
        while (!combat.isOver()) {
            combat.nextRound()
        }
        return combat.score()
    }

    private class Combat(private val map: Grid<Square>) {
        private var rounds = 0L
        private var squaresWithCreature = findCreatures()

        fun score(): Long {
            return squaresWithCreature.sumOf { it.getCreature().hp } * (rounds - 1L)
        }

//        fun printSummary() {
//            val goblins = squaresWithCreature.filter { square -> square.hasGoblin() }.map { it.getCreature() }
//            val elves = squaresWithCreature.filter { square -> square.hasElf() }.map { it.getCreature() }
//            println("[$rounds] elves: ${elves.size}, hp: ${elves.sumOf { it.hp }} goblins: ${goblins.size}, hp: ${goblins.sumOf { it.hp }} ")
//        }

        fun isOver(): Boolean {
            val goblins = squaresWithCreature.filter { it.hasGoblin() }.size
            val elves = squaresWithCreature.filter { it.hasElf() }.size
            return goblins == 0 || elves == 0
        }

        fun nextRound() {
            squaresWithCreature.asSequence().filter { it.hasCreature() }.forEach { squareWithCreature ->
                val attacked = tryAttack(squareWithCreature)
                if (!attacked) {
                    // move
                    val pathToNextTarget = selectedPathToChosenTarget(squareWithCreature)
                    if (pathToNextTarget != null) {
                        val nextSquare = pathToNextTarget.cells[1]
                        nextSquare.placeCreature(squareWithCreature.removeCreature())
                        tryAttack(nextSquare)
                    } else {
                        // no way to move, skipping round
                    }
                }
            }

            squaresWithCreature = findCreatures()
            rounds++

            //printSummary()
        }

        private fun tryAttack(squareWithCreature: Square): Boolean {
            val targetSquareWithCreature = targetToAttack(squareWithCreature)
            if (targetSquareWithCreature != null) {
                val attackedCreature = targetSquareWithCreature.getCreature()
                squareWithCreature.getCreature().attack(attackedCreature)
                if (attackedCreature.isDead())
                    targetSquareWithCreature.removeCreature()
                return true
            }

            return false
        }

        private fun findCreatures() = map.cells.filter { it.hasCreature() }.sortedBy { it.position }

        private fun targetsInRangeToAttack(squareWithCreature: Square): Set<Square> =
            squareWithCreature.neighbours().filter { neighbour ->
                when {
                    squareWithCreature.hasElf() -> neighbour.hasGoblin()
                    squareWithCreature.hasGoblin() -> neighbour.hasElf()
                    else -> throw RuntimeException("Cannot set targets in range to attack")
                }
            }.toSet()

        private fun targetToAttack(squareWithCreature: Square): Square? {
            val targetsToAttack = targetsInRangeToAttack(squareWithCreature)
            if (targetsToAttack.isEmpty())
                return null
            val minHp = targetsToAttack.minOf { it.getCreature().hp }
            return targetsToAttack.filter { it.getCreature().hp == minHp }.minByOrNull { it.position }
        }

        private fun targetsOutOfRangeToAttack(squareWithCreature: Square): Set<Square> =
            map.cells.filter { target ->
                when {
                    squareWithCreature.hasElf() -> target.hasGoblin()
                    squareWithCreature.hasGoblin() -> target.hasElf()
                    else -> throw RuntimeException("Cannot set targets")
                }
            }.toSet()

        private fun inRange(squareWithCreature: Square): Set<Square> = targetsOutOfRangeToAttack(squareWithCreature)
            .flatMap { it.neighbours() }
            .filter { !it.hasCreature() }
            .toSet()

        private fun nearestTargets(squareWithCreature: Square): Pair<Set<Square>, Int> {
            val distanceMap = squareWithCreature.distanceMap { neighbourSquare -> !neighbourSquare.hasCreature() }
            val reachable = inRange(squareWithCreature).intersect(distanceMap.keys)
            if (reachable.isEmpty())
                return Pair(emptySet(), 0)
            val nearestDistance = distanceMap.filter { reachable.contains(it.key) }.minOf { it.value }
            return Pair(
                distanceMap.filter { reachable.contains(it.key) && it.value == nearestDistance }.keys,
                nearestDistance
            )
        }

        private fun chosenTarget(squareWithCreature: Square): Pair<Square?, Int> {
            val (nearestTargets, distance) = nearestTargets(squareWithCreature)
            return Pair(nearestTargets.minByOrNull { it.position }, distance)
        }

        private fun pathsToChosenTarget(squareWithCreature: Square): List<Path<Square>> {
            val (chosenTarget, distance) = chosenTarget(squareWithCreature)

            val pathsToTarget =
                chosenTarget?.let { target -> squareWithCreature.shortestPaths(target, distance) { !it.hasCreature() } }
                    ?: emptyList()

            return pathsToTarget
        }

        private fun selectedPathToChosenTarget(squareWithCreature: Square): Path<Square>? {
            return pathsToChosenTarget(squareWithCreature).minByOrNull { it }
        }
    }

    private interface Creature {
        var hp: Int
        var attackPower: Int
        fun isDead() = hp <= 0
        fun attack(other: Creature) {
            if ((this is Goblin && other is Goblin) || (this is Elf && other is Elf))
                throw RuntimeException("Cannot attack similar creature")
            other.hp -= attackPower
        }
    }

    private class Goblin(override var attackPower: Int = 3) : Creature {
        override var hp = 200
        override fun toString() = "Goblin: hp=$hp"
    }

    private class Elf(override var attackPower: Int = 3) : Creature {
        override var hp = 200
        override fun toString() = "Elf: hp=$hp"
    }

    private class Square(pos: Position, c: Char, elf: () -> Elf, goblin: () -> Goblin) : Cell<Square>(pos, c) {
        private val isSpace = value != '#'
        private var creature: Creature? = if (c == 'E') elf() else if (c == 'G') goblin() else null

        fun getCreature(): Creature {
            if (!hasCreature())
                throw RuntimeException("Cannot get creature")
            return creature!!
        }

        fun removeCreature(): Creature {
            if (!hasCreature())
                throw RuntimeException("Cannot remove creature")
            val result = creature!!
            creature = null
            return result
        }

        fun placeCreature(creature: Creature) {
            if (hasCreature())
                throw RuntimeException("Cannot place creature")
            this.creature = creature
        }

        fun hasCreature() = creature != null

        fun hasGoblin() = creature is Goblin

        fun hasElf() = creature is Elf

        override fun canGoN() = n?.isSpace == true
        override fun canGoS() = s?.isSpace == true
        override fun canGoW() = w?.isSpace == true
        override fun canGoE() = e?.isSpace == true

        override fun toString() = "$position ${if (hasCreature()) getCreature().toString() else ""}"
    }
}
