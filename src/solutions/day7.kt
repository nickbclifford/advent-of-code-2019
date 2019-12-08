package solutions

import com.marcinmoskala.math.permutations
import java.io.File

fun main() {
    val program = File("inputs/day7.txt").readText().trim().split(',').map { it.toInt() }

    val machines = List(5) { TESTMachine(program) }

    val chainPhaseSettings = (0..4).toList().permutations().filter { it.size == 5 }

    fun runChain(initial: Int, phaseSettings: List<Int>): Int {
        var step = initial
        for ((setting, machine) in phaseSettings.zip(machines)) {
            step = machine.run(listOf(setting, step))
        }
        return step
    }

    println("Max chain thrust: ${chainPhaseSettings.map { runChain(0, it) }.max()}")

    for (machine in machines) {
        machine.haltOnOutput = true
    }

    val inputs = List(5) { mutableListOf<Int>() }

    fun runFeedback(phaseSettings: List<Int>): Int {
        inputs[0].add(runChain(0, phaseSettings))
        do {
            repeat(5) {
                inputs[if (it == 4) 0 else it + 1].add(machines[it].run(inputs[it]))
            }
        } while (!machines[4].haltedByOpcode)
        return inputs[0].last()
    }

    val feedbackPhaseSettings = (5..9).toList().permutations().filter { it.size == 5 }

    println("Max feedback thrust: ${feedbackPhaseSettings.map(::runFeedback).max()}")
}
