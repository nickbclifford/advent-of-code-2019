package solutions

import utils.intcode.*
import java.io.File

class GravityAssistMachine(input: List<Int>) : IntcodeMachine(input) {
    override val opcodes = mapOf(
        1 to Instruction(3, listOf(2)) { args ->
            program[args[2]] = args[0] + args[1]
        },
        2 to Instruction(3, listOf(2)) { args ->
            program[args[2]] = args[0] * args[1]
        },
        99 to Instruction(0) { stop() }
    )
}

fun main() {
    val input = File("inputs/day2.txt").readText().trim().split(',').map { it.toInt() }

    val machine = GravityAssistMachine(input)

    // Part 1
    println("Position 0: ${machine.run(12, 2)}")

    // Part 2
    for (noun in 0..99) {
        for (verb in 0..99) {
            if (machine.run(noun, verb) == 19690720) {
                println("100 * noun + verb: ${100 * noun + verb}")
            }
        }
    }
}
