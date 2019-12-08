package solutions

import utils.iteration.occurrences
import java.io.File

const val height = 6
const val width = 25

fun main() {
    val image = File("inputs/day8.txt").readText().trim()

    val layers = image.chunked(height * width)
    val zeroLayer = layers.minBy { it.occurrences('0') }!!

    println("Corruption check: ${zeroLayer.occurrences('1') * zeroLayer.occurrences('2')}")

    val finalLayer = StringBuilder()

    repeat(height * width) {
        for (layer in layers) {
            if (layer[it] == '2') {
                continue
            } else {
                finalLayer.append(layer[it])
                break
            }
        }
    }

    val decoded = finalLayer.toString()

    println("Decoded image:")
    for (line in decoded.chunked(width)) {
        println(line)
    }
}
