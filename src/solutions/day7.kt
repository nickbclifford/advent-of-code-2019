package solutions

import com.marcinmoskala.math.permutations
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

suspend fun main() {
    val program = File("inputs/test.txt").readText().trim().split(',').map { it.toInt() }

    val machines = List(5) { TESTMachine(program, it) }

//    val chainPhaseSettings = (0..4).toList().permutations().filter { it.size == 5 }
//
//    fun runChain(initial: Int, phaseSettings: List<Int>): Int {
//        var step = initial
//        for ((setting, machine) in phaseSettings.zip(machines)) {
//            println("blocking chain for machine ${machine.machineNum}")
//            step = machine.run(listOf(setting, step))
//        }
//        return step
//    }
//
//    println("Max chain thrust: ${chainPhaseSettings.map { runChain(0, it) }.max()}")

    suspend fun runFeedback(phaseSettings: List<Int>): Int {
        val channels = List(5) { Channel<Int>(Channel.UNLIMITED) }

        for ((idx, channel) in channels.withIndex()) {
            if (idx == 0) {
                channel.send(0)
            }
            channel.send(phaseSettings[idx])

            println("$idx, ${Math.floorMod(idx + 1, 5)}")
            machines[idx].output = channel
            machines[Math.floorMod(idx + 1, 5)].input = channel
        }

        coroutineScope {
            for (machine in machines) {
                launch {
                    machine.logHeader("starting pipeline")
                    machine.runPipeline()
                    machine.logHeader("finished pipeline")
                }.invokeOnCompletion {
                    machine.logHeader("coroutine completed")
                }
            }
        }
        println("coroutine scope completed")
        return channels[4].toList().last()
    }

    val feedbackPhaseSettings = (5..9).toList().permutations().filter { it.size == 5 }

    println("Max feedback thrust: ${feedbackPhaseSettings.map { runFeedback(it) }.max()}")
}
