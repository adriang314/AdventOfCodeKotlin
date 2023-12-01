package solution

abstract class BaseSolution {

    protected fun input() = BaseSolution::class.java.getResource("/inputDay$day")!!.readText()

    abstract val day: Int

    override fun toString() = "DAY $day ${task1()} ${task2()}"

    abstract fun task1(): String

    abstract fun task2(): String
}