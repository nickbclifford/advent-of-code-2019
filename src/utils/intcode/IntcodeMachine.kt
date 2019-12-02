package utils.intcode

abstract class IntcodeMachine(private val input: List<Int>) {
    abstract val opcodes: Map<Int, Instruction>

    private var stopped = false
    protected var program = input.toMutableList()

    fun run(noun: Int, verb: Int): Int {
        // reset program to initial state
        reset()
        program[1] = noun
        program[2] = verb

        var ip = 0

        while (!stopped) {
            // get instruction, step pointer
            val opcode = program[ip]
            val instruction = opcodes[opcode] ?: throw NotImplementedError()
            ip++

            // invoke instruction
            val args = program.subList(ip, ip + instruction.arity)
            instruction.operation.invoke(this, args)
            ip += instruction.arity
        }

        return program[0]
    }

    private fun reset() {
        program = input.toMutableList()
        stopped = false
    }

    protected fun stop() {
        stopped = true
    }
}
