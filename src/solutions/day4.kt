package solutions

import utils.iteration.groupBySelf
import java.io.File

fun isValidPassword(digits: Sequence<Int>): Boolean {
    val pairs = digits.windowed(2)

    return pairs.all { it[0] <= it[1] } && pairs.any { it[0] == it[1] }
}

fun isValidLargerPassword(digits: Sequence<Int>): Boolean {
    val pairs = digits.windowed(2)

    val consecutivePairs = pairs.filter { it[0] == it[1] }.groupBySelf()

    return pairs.all { it[0] <= it[1] } && consecutivePairs.containsValue(1)
}

// maybe sequences will make it faster, idk
fun main() {
    val endpoints = File("inputs/day4.txt").readText().trim().split('-').map { it.toInt() }
    val range = endpoints[0]..endpoints[1]
    val digitsRange = range.map { num -> num.toString().toCharArray().asSequence().map { it.toInt() } }

    println("${digitsRange.count(::isValidPassword)} different passwords")
    println("${digitsRange.count(::isValidLargerPassword)} different larger passwords")
}
