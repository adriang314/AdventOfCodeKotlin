package common

import kotlin.system.measureTimeMillis

abstract class BaseSolution {

    protected fun input() = BaseSolution::class.java.getResource("/year$year/inputDay$day")!!.readText()

    abstract val day: Int

    open val year: Int = 2023

    fun result() = "DAY $day: ${measureTask(::task1)}, ${measureTask(::task2)}"

    abstract fun task1(): String

    abstract fun task2(): String

    private fun measureTask(task: () -> String): String {
        var result: String
        val time = measureTimeMillis { result = task() }
        return "$result in ${time}ms"
    }
}