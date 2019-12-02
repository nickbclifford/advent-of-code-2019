import java.io.File

fun main() {
    val input = File("inputs/day2.txt").readText().trim().split(',').map { it.toInt() }

    fun runProgram(noun: Int, verb: Int): Int {
        val program = input.toMutableList()
        program[1] = noun
        program[2] = verb
        loop@ for (i in 0 until program.size step 4) {
            val opcode = program[i]
            val left = program[i + 1]
            val right = program[i + 2]
            val result = program[i + 3]
            program[result] = when (opcode) {
                1 -> program[left] + program[right]
                2 -> program[left] * program[right]
                99 -> break@loop
                else -> throw NotImplementedError()
            }
        }
        return program[0]
    }

    // Part 1
    println("Position 0: ${runProgram(12, 2)}")

    // Part 2
    for (noun in 0..99) {
        for (verb in 0..99) {
            if (runProgram(noun, verb) == 19690720) {
                println("100 * noun + verb: ${100 * noun + verb}")
            }
        }
    }
}
