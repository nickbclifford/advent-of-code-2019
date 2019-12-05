package utils.intcode

import utils.math.digitAt
import kotlin.properties.Delegates

abstract class IntcodeMachine(private val inputInstructions: List<Int>) {
    abstract val opcodes: Map<Int, Instruction>

    private var stopped = false
    protected var program = inputInstructions.toMutableList()

    protected var input = 0
    protected var output = 0

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

    fun run(programInput: Int): Int {
        reset()
        input = programInput

        execute()

        return output
    }

    private fun reset() {
        program = inputInstructions.toMutableList()
        stopped = false
        input = 0
        output = 0
        ip = 0
        ipChanged = false
    }

    protected fun stop() {
        stopped = true
    }
}
