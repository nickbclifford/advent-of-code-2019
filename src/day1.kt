import java.io.File

fun getFuel(mass: Int) = (mass / 3) - 2

fun main() {
    val masses = File("inputs/day1.txt").readLines().map { it.toInt() }

    println("Total fuel: ${masses.sumBy(::getFuel)}")

    val correctedFuel = masses.sumBy {
        var totalFuel = 0
        var neededFuel = getFuel(it)
        while (neededFuel > 0) {
            totalFuel += neededFuel
            neededFuel = getFuel(neededFuel)
        }
        totalFuel
    }

    println("Corrected fuel: $correctedFuel")
}
