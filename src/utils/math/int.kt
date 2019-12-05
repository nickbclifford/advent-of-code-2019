package utils.math

import kotlin.math.pow

fun Int.pow(exp: Int) = toDouble().pow(exp).toInt()
fun Int.digitAt(powerOfTen: Int) = Math.floorDiv(this, 10.pow(powerOfTen)) % 10
