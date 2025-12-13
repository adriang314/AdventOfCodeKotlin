package year2015

import com.google.common.primitives.Ints.min
import common.BaseSolution
import common.Combinatorics
import kotlin.math.max

fun main() = println(SolutionDay21().result())

class SolutionDay21 : BaseSolution() {

    override val day = 21

    private val boss = input().split("\r\n").let { lines -> Player("boss", lines[0].substringAfter(": ").toInt(), lines[1].substringAfter(": ").toInt(), lines[2].substringAfter(": ").toInt()) }

    private val weapons = setOf(
        Weapon("Dagger", 8, 4),
        Weapon("Shortsword", 10, 5),
        Weapon("Warhammer", 25, 6),
        Weapon("Longsword", 40, 7),
        Weapon("Greataxe", 74, 8)
    )

    private val armors = setOf(
        Armor("None", 0, 0),
        Armor("Leather", 13, 1),
        Armor("Chainmail", 31, 2),
        Armor("Splintmail", 53, 3),
        Armor("Bandedmail", 75, 4),
        Armor("Platemail", 102, 5)
    )

    private val rings = setOf(
        Ring("Damage +1", 25, 1, 0),
        Ring("Damage +2", 50, 2, 0),
        Ring("Damage +3", 100, 3, 0),
        Ring("Defense +1", 20, 0, 1),
        Ring("Defense +2", 40, 0, 2),
        Ring("Defense +3", 80, 0, 3),
    )

    private val ringsToWear = listOf(setOf(Ring("None", 0, 0, 0))).plus(rings.map { setOf(it) }).plus(Combinatorics.combinations(rings, 2))

    override fun task1(): String {
        var minGOldSpentToWin = Int.MAX_VALUE
        for (weapon in weapons) {
            for (armor in armors) {
                for (rings in ringsToWear) {
                    val totalDamage = weapon.damage + rings.sumOf { it.damage }
                    val totalArmor = armor.armor + rings.sumOf { it.armor }
                    if (Battle(Player("me", 100, totalDamage, totalArmor), boss.copy()).player1Wins())
                        minGOldSpentToWin = min(minGOldSpentToWin, weapon.cost + armor.cost + rings.sumOf { it.cost })
                }
            }
        }

        return minGOldSpentToWin.toString()
    }

    override fun task2(): String {
        var maxGoldSpentAndStillLoose = 0
        for (weapon in weapons) {
            for (armor in armors) {
                for (rings in ringsToWear) {
                    val totalDamage = weapon.damage + rings.sumOf { it.damage }
                    val totalArmor = armor.armor + rings.sumOf { it.armor }
                    if (!Battle(Player("me", 100, totalDamage, totalArmor), boss.copy()).player1Wins())
                        maxGoldSpentAndStillLoose = max(maxGoldSpentAndStillLoose, weapon.cost + armor.cost + rings.sumOf { it.cost })
                }
            }
        }

        return maxGoldSpentAndStillLoose.toString()
    }

    private class Battle(val player1: Player, val player2: Player) {

        fun player1Wins(): Boolean {
            var attacker = player1
            var defender = player2
            while (player1.isAlive() && player2.isAlive()) {
                defender.suffer(attacker.damage)
                if (attacker === player1) {
                    attacker = player2
                    defender = player1
                } else {
                    attacker = player1
                    defender = player2
                }
            }

            return player1.isAlive()
        }
    }

    private data class Player(val name: String, var hitPoints: Int, val damage: Int, val armor: Int) {
        fun suffer(damage: Int) {
            hitPoints -= max(1, damage - armor)
        }

        fun isAlive() = hitPoints > 0
    }

    private data class Weapon(val name: String, val cost: Int, val damage: Int)

    private data class Armor(val name: String, val cost: Int, val armor: Int)

    private data class Ring(val name: String, val cost: Int, val damage: Int, val armor: Int)
}