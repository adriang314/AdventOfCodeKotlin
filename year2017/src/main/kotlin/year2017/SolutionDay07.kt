package year2017

import common.BaseSolution

fun main() = println(SolutionDay07().result())

class SolutionDay07 : BaseSolution() {

    override val day = 7

    private val disks: List<Disk>
    private val rootDisk: Disk

    init {
        val diskList = mutableListOf<Disk>()
        val regex = Regex("^(\\w+) \\((\\d+)\\)")
        input().lines().forEach { line ->
            val (name, weight) = regex.find(line)!!.destructured
            diskList.add(Disk(name, weight.toLong()))
        }

        input().lines().filter { it.contains("->") }.forEach { line ->
            val (name) = regex.find(line)!!.destructured
            val disk = diskList.single { it.name == name }
            val childDisks = line.split("->").last().trim().split(", ")
                .map { childName -> diskList.single { it.name == childName }.also { it.parent = disk } }
            disk.child.addAll(childDisks)
        }

        disks = diskList
        rootDisk = diskList.single { it.parent == null }
        rootDisk.updateLevel(0)
    }

    override fun task1(): String {
        return rootDisk.name
    }

    override fun task2(): String {
        var currentLevel = disks.maxOf { it.level }
        while (true) {
            val currentLevelParentPrograms = disks.filter { it.level == currentLevel }.map { it.parent!! }.toSet()
            val isLevelBalanced = currentLevelParentPrograms.all { parent -> parent.isBalanced() }
            if (isLevelBalanced) {
                currentLevel--
            } else {
                val unbalanced = currentLevelParentPrograms.single { parent -> !parent.isBalanced() }
                val unbalancedWeights = unbalanced.child.groupBy { it.totalWeight() }
                val desiredWeight = unbalancedWeights.maxBy { it.value.size }.key
                val undesiredWeight = unbalancedWeights.minBy { it.value.size }.key
                val weightDiff = desiredWeight - undesiredWeight

                val unbalancedProgram = unbalanced.child.single { it.totalWeight() == undesiredWeight }
                return (unbalancedProgram.weight + weightDiff).toString()
            }
        }
    }

    private class Disk(val name: String, val weight: Long) {
        val child: MutableList<Disk> = mutableListOf()
        var parent: Disk? = null
        var level: Int = 0

        fun isBalanced() = child.minOf { it.totalWeight() } == child.maxOf { it.totalWeight() }

        fun updateLevel(value: Int) {
            level = value
            child.forEach { it.updateLevel(value + 1) }
        }

        fun totalWeight(): Long = weight + child.sumOf { it.totalWeight() }

        override fun toString() = "$name (${totalWeight()}) [lvl:$level]"
    }
}