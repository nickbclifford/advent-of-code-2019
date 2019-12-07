package utils.intcode

import utils.math.digitAt
import kotlin.properties.Delegates

abstract class IntcodeMachine(private val inputInstructions: List<Int>) {
    abstract val opcodes: Map<Int, Instruction>

    private var stopped = false
    protected var program = inputInstructions.toMutableList()

    private var allInputs = mutableListOf<Int>()

    var haltOnOutput = false
    var haltedByOpcode = false
        private set
    protected var output by Delegates.observable(0) { _, _, _ ->
        if (haltOnOutput) {
            stop()
            haltedByOpcode = false
        }
    }

    protected var ip by Delegates.observable(0) { _, _, _ -> ipChanged = true }
    private var ipChanged = false

    private fun execute() {
        while (!stopped) {
            val fullOpcode = program[ip]

            // get instruction, step pointer
            val opcode = fullOpcode % 100
            val instruction = opcodes[opcode] ?: throw NotImplementedError()
            ip++
            ipChanged = false // don't trigger the observable

            val args = program.subList(ip, ip + instruction.arity).mapIndexed { idx, arg ->
                if (instruction.ignoreModeIndices.contains(idx)) {
                    arg
                } else {
                    // ignore the last two digits
                    when (fullOpcode.digitAt(2 + idx)) {
                        0 -> program[arg]
                        1 -> arg
                        else -> throw NotImplementedError()
                    }
                }
            }

            instruction.operation.invoke(this, args)

            if (!ipChanged) {
                ip += instruction.arity
            }
            ipChanged = false
        }
    }

    fun run(noun: Int, verb: Int): Int {
        // reset program to initial state
        reset()
        program[1] = noun
        program[2] = verb

        execute()

        return program[0]
    }

    fun run(programInputs: List<Int>): Int {
        if (haltOnOutput) {
            haltOnOutputReset()
        } else {
            reset()
        }
        allInputs = programInputs.toMutableList()

        execute()

        return output
    }

    private fun reset() {
        haltOnOutputReset()
        program = inputInstructions.toMutableList()
        output = 0
        ip = 0
        ipChanged = false
    }

    private fun haltOnOutputReset() {
        stopped = false
        allInputs = mutableListOf()
        haltedByOpcode = false
    }

    protected fun stop() {
        stopped = true
        haltedByOpcode = true
    }

    protected fun input() = allInputs.removeAt(0)
}
