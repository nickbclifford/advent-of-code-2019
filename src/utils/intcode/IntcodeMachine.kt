package utils.intcode

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import utils.math.digitAt
import java.util.concurrent.CancellationException
import kotlin.properties.Delegates

const val columnSpacer = 30

abstract class IntcodeMachine(private val inputInstructions: List<Int>, val id: Int) {
    val prevId: Int
        get() = Math.floorMod(id - 1, 5)
    val nextId: Int
        get() = Math.floorMod(id + 1, 5)

    abstract val opcodes: Map<Int, Instruction>

    private var stopped = false
    protected var program = inputInstructions.toMutableList()

    protected var ip by Delegates.observable(0) { _, _, _ -> ipChanged = true }
    private var ipChanged = false

    lateinit var input: ReceiveChannel<Int>
    protected var internalOutput = Channel<Int>(Channel.UNLIMITED)

    val output: ReceiveChannel<Int>
        get() = internalOutput

    private suspend fun execute() {
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

        logHeader("before cancel/close")
        input.cancel(CancellationException("machine $id halted "))
        internalOutput.close(Exception("machine $id halted"))
        logHeader("after cancel/close")
    }

    fun run(noun: Int, verb: Int): Int {
        reset()
        program[1] = noun
        program[2] = verb

        runBlocking {
            execute()
        }

        return program[0]
    }

    fun run(inputs: List<Int>) = runBlocking {
        input = produce { inputs.forEach { send(it) } }
        runPipeline()
        output.toList().last()
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
        internalOutput = Channel(Channel.UNLIMITED)
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
