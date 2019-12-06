package solutions

import java.io.File

class TreeNode(val value: String, var parent: TreeNode? = null, var children: MutableSet<TreeNode> = mutableSetOf()) {
    fun depth(): Int {
        var counter = 0
        var current = this
        while (current.parent != null) {
            counter++
            current = current.parent!!
        }
        return counter
    }

    override operator fun equals(other: Any?): Boolean = other is TreeNode && value == other.value
    override fun hashCode() = value.hashCode()

    fun hasDescendant(other: TreeNode): Boolean = if (this == other) {
        true
    } else {
        children.any { it.hasDescendant(other) }
    }

    fun hasSibling(other: TreeNode) = parent?.children?.toSet()?.contains(other) ?: false
}

fun main() {
    val orbitStrings = File("inputs/day6.txt").readLines().map { val o = it.split(')'); o[0] to o[1] }

    val allNodes = orbitStrings.flatMap { o -> o.toList() }.associateWith { TreeNode(it) }

    for ((parent, child) in orbitStrings) {
        val parentNode = allNodes[parent] ?: throw NotImplementedError()
        val childNode = allNodes[child] ?: throw NotImplementedError()

        parentNode.children.add(childNode)
        childNode.parent = parentNode
    }

    println("Total orbits: ${allNodes.values.sumBy { it.depth() }}")

    val youObj = allNodes["YOU"] ?: throw NotImplementedError()
    val santaObj = allNodes["SAN"] ?: throw NotImplementedError()

    var counter = 0
    var currentNode = youObj

    while (!currentNode.hasSibling(santaObj)) {
        counter++
        currentNode = if (currentNode.hasDescendant(santaObj)) {
            currentNode.children.find { it.hasDescendant(santaObj) }!!
        } else {
            currentNode.parent!!
        }
    }

    counter -= 2 // account for start and end nodes ig?

    println("Transfers: $counter")
}
