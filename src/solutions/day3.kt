package solutions

import utils.iteration.groupBySelf
import java.io.File
import kotlin.math.abs

val points = mutableListOf<Pair<Int, Int>>()

fun processWire(wire: List<Pair<Char, Int>>): MutableList<Pair<Int, Int>> {
    var x = 0
    var y = 0

    val holdPoints = mutableListOf<Pair<Int, Int>>()

    for ((direction, distance) in wire) {
        when (direction) {
            'U' -> {
                repeat(distance) {
                    holdPoints.add(x to ++y)
                }
            }
            'D' -> {
                repeat(distance) {
                    holdPoints.add(x to --y)
                }
            }
            'L' -> {
                repeat(distance) {
                    holdPoints.add(--x to y)
                }
            }
            'R' -> {
                repeat(distance) {
                    holdPoints.add(++x to y)
                }
            }
        }
    }

    points.addAll(holdPoints.distinct())
    return holdPoints
}

fun main() {
    val wires = File("inputs/day3.txt").readLines().map { wire ->
        val paths = wire.split(',')
        paths.map { it[0] to it.substring(1).toInt() }
    }

    val wirePoints = wires.map(::processWire)

    val intersections = points.groupBySelf().filterValues { it == 2 }.keys
    val distances = intersections.map { (x, y) -> abs(x) + abs(y) }

    println("Manhattan distance: ${distances.min()}")

    val steps = intersections.map { i -> wirePoints.sumBy { it.indexOf(i) + 1 } }
    println("Fewest steps: ${steps.min()}")
}
