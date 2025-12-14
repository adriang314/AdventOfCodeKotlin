package year2015

import common.BaseSolution
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() = println(SolutionDay22().result())

class SolutionDay22 : BaseSolution() {

    override val day = 22

    private val boss = input().split("\r\n").let { lines -> Boss(lines[0].substringAfter(": ").toInt(), lines[1].substringAfter(": ").toInt()) }
    private val wizard = Wizard(50, 500, 0, 0)

    override fun task1(): String {
        return Battle().findBestScore(wizard, boss, 0).toString()
    }

    override fun task2(): String {
        return Battle().findBestScore(wizard, boss, 1).toString()
    }

    private class Battle() {

        fun findBestScore(wizard: Wizard, boss: Boss, wizardVulnerability: Int): Int {
            val statuses = LinkedList(listOf(BattleStatus(wizard, boss, ActiveSpells(emptyMap(), wizardVulnerability))))
            var minManaUsedForWizardVictory = Int.MAX_VALUE

            fun registerBattleResult(battleStatus: BattleStatus) {
                minManaUsedForWizardVictory = if (battleStatus.wizardWins()) min(minManaUsedForWizardVictory, battleStatus.wizard.used) else minManaUsedForWizardVictory
            }

            do {
                val battleStatus = statuses.removeFirst()

                // spells before wizard attack
                val battleStatusAfterFirstSpells = battleStatus.executeSpells()
                if (battleStatusAfterFirstSpells.isOver()) {
                    registerBattleResult(battleStatusAfterFirstSpells)
                    continue
                }

                // wizard picks a spell
                battleStatusAfterFirstSpells.nextSpells().forEach nextSpellLoop@{ nextSpell ->

                    // wizard attacks
                    val battleStatusAfterWizardAttack = battleStatusAfterFirstSpells.wizardAttacks(nextSpell)
                    if (battleStatusAfterWizardAttack.isOver()) {
                        registerBattleResult(battleStatusAfterWizardAttack)
                        return@nextSpellLoop
                    }

                    // spells before boss attack
                    val battleStatusAfterSecondSpells = battleStatusAfterWizardAttack.executeSpells()
                    if (battleStatusAfterSecondSpells.isOver()) {
                        registerBattleResult(battleStatusAfterSecondSpells)
                        return@nextSpellLoop
                    }

                    // boss attacks
                    val battleStatusAfterBossAttack = battleStatusAfterSecondSpells.bossAttacks()
                    if (battleStatusAfterBossAttack.isOver()) {
                        registerBattleResult(battleStatusAfterBossAttack)
                        return@nextSpellLoop
                    }

                    // continuing battle if it makes sense
                    if (minManaUsedForWizardVictory > battleStatusAfterBossAttack.wizard.used) {
                        statuses.add(battleStatusAfterBossAttack)
                    }
                }
            } while (statuses.isNotEmpty())

            return minManaUsedForWizardVictory
        }
    }

    private class BattleStatus(val wizard: Wizard, val boss: Boss, val activeSpells: ActiveSpells) {

        fun bossAttacks(): BattleStatus = boss.attacks(wizard, activeSpells)

        fun wizardAttacks(spell: Spell): BattleStatus = wizard.attacks(boss, spell, activeSpells)

        fun executeSpells(): BattleStatus = activeSpells.apply(this)

        fun nextSpells() = activeSpells.nextSpells(wizard.mana)

        fun isOver() = wizard.isDead() || boss.isDead()

        fun wizardWins() = !wizard.isDead() && boss.isDead()
    }

    private data class ActiveSpells(private val map: Map<Spell, Int>, private val wizardVulnerability: Int) {

        fun apply(battleStatus: BattleStatus): BattleStatus {
            val damage = map.keys.sumOf { it.damage }
            val armor = map.keys.sumOf { it.armorIncrease }
            val manaIncrease = map.keys.sumOf { it.manaIncrease }
            val wizard = battleStatus.wizard
            val boss = battleStatus.boss
            return BattleStatus(
                wizard = Wizard(wizard.hp - wizardVulnerability, wizard.mana + manaIncrease, armor, wizard.used),
                boss = Boss(boss.hp - damage, boss.damage),
                activeSpells = ActiveSpells(map.mapValues { it.value - 1 }.filterValues { it > 0 }, wizardVulnerability)
            )
        }

        fun add(spell: Spell): ActiveSpells = if (spell.immediate) this else ActiveSpells(map.plus(spell to spell.last), wizardVulnerability)

        fun nextSpells(mana: Int) = allSpells.filter { spell -> spell.cost <= mana && !map.containsKey(spell) }

        companion object {
            private val allSpells = listOf(
                Spell("Magic Missile", 53, 4, 0, 0, 0, 0),
                Spell("Drain", 73, 2, 0, 0, 2, 0),
                Spell("Shield", 113, 0, 7, 0, 0, 6),
                Spell("Poison", 173, 3, 0, 0, 0, 6),
                Spell("Recharge", 229, 0, 0, 101, 0, 5)
            )
        }
    }

    private data class Spell(val name: String, val cost: Int, val damage: Int, val armorIncrease: Int, val manaIncrease: Int, val hpIncrease: Int, val last: Int) {
        val immediate = last == 0

        override fun toString(): String = name
    }

    private data class Wizard(val hp: Int, val mana: Int, val armor: Int, val used: Int) {

        fun attacks(boss: Boss, spell: Spell, activeSpells: ActiveSpells): BattleStatus =
            BattleStatus(Wizard(hp + if (spell.immediate) spell.hpIncrease else 0, mana - spell.cost, armor, used + spell.cost), boss.defends(if (spell.immediate) spell.damage else 0), activeSpells.add(spell))

        fun defends(damage: Int): Wizard =
            Wizard(hp - max(1, damage - armor), mana, armor, used)

        fun isDead() = hp <= 0
    }

    private data class Boss(val hp: Int, val damage: Int) {

        fun attacks(wizard: Wizard, activeSpells: ActiveSpells): BattleStatus = BattleStatus(wizard.defends(damage), this, activeSpells)

        fun defends(damage: Int): Boss = if (damage == 0) this else Boss(hp - max(1, damage), this.damage)

        fun isDead() = hp <= 0
    }
}