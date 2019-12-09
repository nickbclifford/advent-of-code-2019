package solutions

import utils.intcode.Instruction
import utils.intcode.IntcodeMachine
import java.io.File

class TESTMachine(instructions: List<Int>, _id: Int): IntcodeMachine(instructions, _id) {
    override val opcodes = mapOf(
        1 to Instruction(3, listOf(2)) { args ->
            program[args[2]] = args[0] + args[1]
        },
        2 to Instruction(3, listOf(2)) { args ->
            program[args[2]] = args[0] * args[1]
        },
        3 to Instruction(1, listOf(0)) { args ->
            logMessage("requesting input...")
            val i = input?.receive()
            if (i != null) {
                program[args[0]] = i
            }
            logMessage("channel $prevId -> $i")
        },
        4 to Instruction(1) { args ->
            val i = args[0]
            logMessage("sending output...")
            output?.send(i)
            logMessage("$i -> channel $nextId")
        },
        5 to Instruction(2) { args ->
            if (args[0] != 0) {
                ip = args[1]
            }
        },
        6 to Instruction(2) { args ->
            if (args[0] == 0) {
                ip = args[1]
            }
        },
        7 to Instruction(3, listOf(2)) { args ->
            program[args[2]] = if (args[0] < args[1]) 1 else 0
        },
        8 to Instruction(3, listOf(2)) { args ->
            program[args[2]] = if (args[0] == args[1]) 1 else 0
        },
        99 to Instruction(0) {
            logHeader("$id halting")
            stop()
        }
    )
}

fun main() {
    val instructions = File("inputs/day5.txt").readText().trim().split(',').map { it.toInt() }

    val machine = TESTMachine(instructions, 0)

    println("Diagnostic: ${machine.run(listOf(1))}")
    println("Thermal radiators: ${machine.run(listOf(5))}")
}
