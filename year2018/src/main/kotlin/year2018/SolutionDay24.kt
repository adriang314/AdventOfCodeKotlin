package year2018

import common.*

fun main() = println(SolutionDay24().result())

class SolutionDay24 : BaseSolution() {

    override val day = 24

    private val immuneSystemArmy: Army
    private val infectionArmy: Army

    private val regex =
        Regex("^(\\d+) units each with (\\d+) hit points (\\(.*\\))? ?with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)$")

    init {
        val lines = input().split("Immune System:\r\n", "Infection:\r\n").filter { it.isNotEmpty() }
        val immuneSystemLines = lines[0].split("\r\n").filter { it.isNotEmpty() }
        val infectionLines = lines[1].split("\r\n").filter { it.isNotEmpty() }

        immuneSystemArmy = Army(immuneSystemLines.mapIndexed { idx, line ->
            val (units, hp, skills, attack, attackType, init) = regex.find(line)!!.destructured
            Brigade(
                "Immune System",
                "Group ${idx + 1}",
                units.toLong(),
                hp.toLong(),
                attack.toLong(),
                attackType,
                init.toLong(),
                skills,
            )
        })

        infectionArmy = Army(infectionLines.mapIndexed { idx, line ->
            val (units, hp, skills, attack, attackType, init) = regex.find(line)!!.destructured
            Brigade(
                "Infection",
                "Group ${idx + 1}",
                units.toLong(),
                hp.toLong(),
                attack.toLong(),
                attackType,
                init.toLong(),
                skills
            )
        })
    }

    override fun task1(): String {
        val result = startWar(0)
        return result.remainingUnits.toString()
    }

    override fun task2(): String {
        var attackBoostRange = (0..500)
        while (attackBoostRange.length() > 1) {
            val midValue = attackBoostRange.midValue()
            val warResult = startWar(midValue)
            attackBoostRange = if (warResult.winner == War.Winner.Immunes) {
                attackBoostRange.first..midValue
            } else {
                midValue + 1..attackBoostRange.last
            }
        }

        val warResult = startWar(attackBoostRange.first)
        return warResult.remainingUnits.toString()
    }

    private fun startWar(immuneArmyAttackBoost: Int = 0): War.Result {
        val immuneSystemArmy = immuneSystemArmy withBoost (immuneArmyAttackBoost)
        val infectionArmy = infectionArmy withBoost (0)
        return War(immuneSystemArmy, infectionArmy).accomplish()
    }

    private class War(val immuneSystem: Army, val infection: Army) {

        data class Result(val winner: Winner, val remainingUnits: Long)

        enum class Winner { Immunes, Infection, Tie }

        fun accomplish(): Result {
            while (true) {
                val immuneSystemStrikes = immuneSystem prepareStrikesAgainst infection
                val infectionStrikes = infection prepareStrikesAgainst immuneSystem
                val unitsLost = strike(immuneSystemStrikes, infectionStrikes)
                when {
                    unitsLost == 0L -> return Result(Winner.Tie, immuneSystem.unitsAlive() + infection.unitsAlive())
                    immuneSystem.hasNoUnits() -> return Result(Winner.Infection, infection.unitsAlive())
                    infection.hasNoUnits() -> return Result(Winner.Immunes, immuneSystem.unitsAlive())
                }
            }
        }

        private fun strike(immuneSystemStrikes: Strikes, infectionStrikes: Strikes): Long {
            val totalUnitLost = (infectionStrikes.list union immuneSystemStrikes.list)
                .sortedWith(compareByDescending { it.attacking.initiative })
                .fold(0L) { total, the ->
                    val damage = the.attacking damages the.defending
                    val currentLost = the.defending suffer damage
                    //println("${the.attacking} attacks ${the.defending}, killing currentLost units")
                    total + currentLost
                }

            immuneSystem.removeDestroyedBrigades()
            infection.removeDestroyedBrigades()
            return totalUnitLost
        }
    }

    private class Army(brigades: List<Brigade>) {
        private val brigades = brigades.toMutableList()

        fun unitsAlive() = if (!hasNoUnits()) brigades.sumOf { it.aliveUnits() } else 0L

        fun hasNoUnits() = !brigades.any { brigade -> brigade.hasUnits() }

        fun removeDestroyedBrigades() = brigades.removeIf { !it.hasUnits() }

        infix fun prepareStrikesAgainst(enemy: Army): Strikes {
            return brigades
                .sortedWith(compareByDescending<Brigade> { it.damages() }.thenByDescending { it.initiative })
                .fold(Strikes()) { strikes, attackingBrigade ->
                    val defendingBrigade = attackingBrigade selectEnemy enemy.brigades.minus(strikes.defending())
                    if (defendingBrigade != null) {
                        strikes plus Strike(attackingBrigade, defendingBrigade)
                    } else {
                        strikes
                    }
                }
        }

        infix fun withBoost(attackBoost: Int) = Army(brigades.map { it.copy(attackBoost = attackBoost) })
    }

    private class Strikes(val list: Set<Strike> = emptySet()) {
        fun defending() = list.map { it.defending }.toSet()

        infix fun plus(strike: Strike) = Strikes(list.plus(strike))
    }

    private class Strike(val attacking: Brigade, val defending: Brigade)

    private data class DamageToEnemy(val enemy: Brigade, val value: Long) {
        fun isReal() = value > 0
    }

    private data class Brigade(
        val armyName: String,
        val groupName: String,
        private var units: Long,
        private val hitPoints: Long,
        private val damagePerUnit: Long,
        private val attackType: String,
        val initiative: Long,
        private val skills: String,
        var attackBoost: Int = 0
    ) {
        override fun toString() = "$armyName $groupName"

        fun damages() = units * (damagePerUnit + attackBoost)

        fun aliveUnits() = if (hasUnits()) units else 0L

        fun hasUnits() = units > 0L

        infix fun damages(enemy: Brigade) = when {
            enemy.immunes.contains(attackType) -> 0L
            enemy.weaknesses.contains(attackType) -> damages() * 2
            else -> damages()
        }

        infix fun suffer(damage: Long): Long {
            val unitLost = damage / hitPoints
            units -= unitLost
            return unitLost
        }

        infix fun selectEnemy(enemies: List<Brigade>): Brigade? {
            return enemies
                .asSequence()
                .map { DamageToEnemy(it, damages(it)) }
                .filter(DamageToEnemy::isReal)
                .fold(emptyList<DamageToEnemy>()) { bestDamages, damage ->
                    val currentBestDamage = (bestDamages.firstOrNull()?.value ?: 0)
                    when {
                        currentBestDamage == damage.value -> bestDamages.plus(damage)
                        currentBestDamage < damage.value -> listOf(damage)
                        else -> bestDamages
                    }
                }
                .map(DamageToEnemy::enemy)
                .sortedWith(compareByDescending<Brigade> { it.damages() }.thenByDescending { it.initiative })
                .firstOrNull()
        }

        private val immunes = skills
            .replace("(", "").replace(")", "")
            .split(";").firstOrNull { it.contains("immune to") }
            ?.let { it.substring(it.indexOf("immune to ") + 10) }?.split(", ") ?: emptyList()

        private val weaknesses = skills
            .replace("(", "").replace(")", "")
            .split(";").firstOrNull { it.contains("weak to") }
            ?.let { it.substring(it.indexOf("weak to ") + 8) }?.split(", ") ?: emptyList()
    }
}
