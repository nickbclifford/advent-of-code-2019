package solutions

import com.marcinmoskala.math.permutations
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

suspend fun main() {
    val program = File("inputs/test.txt").readText().trim().split(',').map { it.toInt() }

    val machines = Array(5) { TESTMachine(program, it) }

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
        coroutineScope {
            machines[0].input = produce {
                send(0); send(phaseSettings[0]); for (x in machines[4].output) send(x)
            }
            machines[1].input = produce {
                send(phaseSettings[1]); for (x in machines[0].output) send(x)
            }
            machines[2].input = produce {
                send(phaseSettings[2]); for (x in machines[1].output) send(x)
            }
            machines[3].input = produce {
                send(phaseSettings[3]); for (x in machines[2].output) send(x)
            }
            machines[4].input = produce {
                send(phaseSettings[4]); for (x in machines[3].output) send(x)
            }


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
        return machines[4].output.toList().last()
    }

    val feedbackPhaseSettings = (5..9).toList().permutations().filter { it.size == 5 }

    println("Max feedback thrust: ${feedbackPhaseSettings.map { runFeedback(it) }.max()}")
}
