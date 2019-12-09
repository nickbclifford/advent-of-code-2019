package solutions

import utils.intcode.Instruction
import utils.intcode.IntcodeMachine
import java.io.File

class BOOSTMachine(instructions: List<Long>): IntcodeMachine(instructions, 0) {
    override val opcodes = mapOf(
        1 to Instruction(3, listOf(2)) { args ->
            program[args[2].toInt()] = args[0] + args[1]
        },
        2 to Instruction(3, listOf(2)) { args ->
            program[args[2].toInt()] = args[0] * args[1]
        },
        3 to Instruction(1, listOf(0)) { args ->
            val i = input?.receive()
            if (i != null) {
                program[args[0].toInt()] = i.toLong()
            }
        },
        4 to Instruction(1) { args ->
            val i = args[0]
            output?.send(i.toInt())
        },
        5 to Instruction(2) { args ->
            if (args[0] != 0L) {
                ip = args[1].toInt()
            }
        },
        6 to Instruction(2) { args ->
            if (args[0] == 0L) {
                ip = args[1].toInt()
            }
        },
        7 to Instruction(3, listOf(2)) { args ->
            program[args[2].toInt()] = if (args[0] < args[1]) 1 else 0
        },
        8 to Instruction(3, listOf(2)) { args ->
            program[args[2].toInt()] = if (args[0] == args[1]) 1 else 0
        },
        9 to Instruction(1) { args ->
            relativeBase += args[0].toInt()
        },
        99 to Instruction(0) {
            stop()
        }
    )
}

fun main() {
    val program = File("inputs/day9.txt").readText().trim().split(',').map { it.toLong() } + List(1000) { 0L }

    val machine = BOOSTMachine(program)

    println("Keycode: ${machine.run(listOf(1))}")
}
