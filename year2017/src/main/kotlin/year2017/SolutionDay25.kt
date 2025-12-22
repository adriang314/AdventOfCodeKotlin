package year2017

import common.BaseSolution

fun main() = println(SolutionDay25().result())

class SolutionDay25 : BaseSolution() {

    override val day = 25

    override fun task1(): String {
        val machine = Machine()
        val result = machine.execute(12172063)
        return result.toString()
    }

    override fun task2(): String {
        return ""
    }

    private class Machine() {
        private val tape = Tape()
        private var currentState: State = StateA

        fun execute(steps: Int): Int {
            repeat(steps) {
                currentState = currentState.nextState(tape)
            }

            return tape.data.count { it.value }
        }
    }

    private data class Tape(var cursorPosition: Int = 0, val data: MutableMap<Int, Boolean> = mutableMapOf()) {
        fun currentValue(): Boolean = data.getOrDefault(cursorPosition, false)
        fun setCurrentValue(value: Boolean) = let { data[cursorPosition] = value }
    }

    private interface State {
        fun nextState(tape: Tape): State
    }

    private object StateA : State {
        override fun nextState(tape: Tape): State {
            if (!tape.currentValue()) {
                tape.setCurrentValue(true)
                tape.cursorPosition++
                return StateB
            } else {
                tape.setCurrentValue(false)
                tape.cursorPosition--
                return StateC
            }
        }
    }

    private object StateB : State {
        override fun nextState(tape: Tape): State {
            if (!tape.currentValue()) {
                tape.setCurrentValue(true)
                tape.cursorPosition--
                return StateA
            } else {
                tape.setCurrentValue(true)
                tape.cursorPosition--
                return StateD
            }
        }
    }

    private object StateC : State {
        override fun nextState(tape: Tape): State {
            if (!tape.currentValue()) {
                tape.setCurrentValue(true)
                tape.cursorPosition++
                return StateD
            } else {
                tape.setCurrentValue(false)
                tape.cursorPosition++
                return StateC
            }
        }
    }

    private object StateD : State {
        override fun nextState(tape: Tape): State {
            if (!tape.currentValue()) {
                tape.setCurrentValue(false)
                tape.cursorPosition--
                return StateB
            } else {
                tape.setCurrentValue(false)
                tape.cursorPosition++
                return StateE
            }
        }
    }

    private object StateE : State {
        override fun nextState(tape: Tape): State {
            if (!tape.currentValue()) {
                tape.setCurrentValue(true)
                tape.cursorPosition++
                return StateC
            } else {
                tape.setCurrentValue(true)
                tape.cursorPosition--
                return StateF
            }
        }
    }

    private object StateF : State {
        override fun nextState(tape: Tape): State {
            if (!tape.currentValue()) {
                tape.setCurrentValue(true)
                tape.cursorPosition--
                return StateE
            } else {
                tape.setCurrentValue(true)
                tape.cursorPosition++
                return StateA
            }
        }
    }
}