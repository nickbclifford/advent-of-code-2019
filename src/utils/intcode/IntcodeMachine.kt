package utils.intcode

import kotlinx.coroutines.channels.*
import kotlinx.coroutines.runBlocking
import utils.math.digitAt
import java.util.concurrent.CancellationException
import kotlin.properties.Delegates

const val columnSpacer = 30

abstract class IntcodeMachine(private val inputInstructions: List<Long>, val id: Int) {
    val prevId: Int
        get() = Math.floorMod(id - 1, 5)
    val nextId: Int
        get() = Math.floorMod(id + 1, 5)

    abstract val opcodes: Map<Int, Instruction>

    private var stopped = false
    protected var program = inputInstructions.toMutableList()

    protected var ip by Delegates.observable(0) { _, _, _ -> ipChanged = true }
    private var ipChanged = false

    var input: ReceiveChannel<Int>? = null
    var output: SendChannel<Int>? = null

    protected var relativeBase = 0

    private suspend fun execute() {
        while (!stopped) {
            val fullOpcode = program[ip].toInt()

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
                        0 -> program[arg.toInt()]
                        1 -> arg
                        2 -> program[(arg + relativeBase).toInt()]
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

        logHeader("before cancel/close")
        input?.cancel(CancellationException("machine $id halted "))
        output?.close()
        logHeader("after cancel/close")
    }

    fun run(noun: Int, verb: Int): Int {
        reset()
        program[1] = noun.toLong()
        program[2] = verb.toLong()

        runBlocking {
            execute()
        }

        return program[0].toInt()
    }

    fun run(inputs: List<Int>) = runBlocking {
        input = produce { inputs.forEach { send(it) } }
        val syncOutput = Channel<Int>(Channel.UNLIMITED)
        output = syncOutput
        runPipeline()
        syncOutput.toList()
    }

    suspend fun runPipeline() {
        reset()
        execute()
    }

    private fun reset() {
        stopped = false
        program = inputInstructions.toMutableList()
        ip = 0
        ipChanged = false
    }

    protected fun stop() {
        stopped = true
    }

    fun logHeader(message: String) {
        println(" ".repeat(columnSpacer * id) + "-- $message --")
    }

    fun logMessage(message: String) {
        println(" ".repeat(columnSpacer * id) + message)
    }

}
