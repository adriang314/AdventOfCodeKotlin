package common

import kotlin.system.measureTimeMillis

abstract class BaseSolution {

    private val creationTimestamp = System.currentTimeMillis()

    protected fun input() = BaseSolution::class.java.getResource("/inputDay$day")!!.readText()

    abstract val day: Int

    fun result(): String {
        val startTimestamp = System.currentTimeMillis()
        val creationMillis = startTimestamp - creationTimestamp

        return "DAY $day: ${measureTask(creationMillis) { task1() }}, ${measureTask(creationMillis) { task2() }}"
    }

    abstract fun task1(): String

    abstract fun task2(): String

    private fun measureTask(creationMillis: Long, task: () -> String): String {
        var result: String
        val time = creationMillis + measureTimeMillis { result = task() }
        return "$result in ${time}ms"
    }
}