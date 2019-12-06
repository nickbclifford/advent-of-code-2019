package solutions

import java.io.File

class TreeNode(val value: String) {
    companion object {
        lateinit var searchNode: TreeNode
    }

    var parent: TreeNode? = null
    var children: MutableSet<TreeNode> = mutableSetOf()

    fun depth(): Int = if (parent == null) {
        0
    } else {
        parent!!.depth() + 1
    }

    override operator fun equals(other: Any?): Boolean = other is TreeNode && value == other.value
    override fun hashCode() = value.hashCode()

    val hasDescendant: Boolean by lazy {
        if (this == searchNode) {
            true
        } else {
            children.any { it.hasDescendant }
        }
    }


    fun hasSearchSibling() = parent?.children?.toSet()?.contains(searchNode) ?: false
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

    TreeNode.searchNode = santaObj

    var counter = 0
    var currentNode = youObj

    while (!currentNode.hasSearchSibling()) {
        counter++
        currentNode = if (currentNode.hasDescendant) {
            currentNode.children.find { it.hasDescendant }!!
        } else {
            currentNode.parent!!
        }
    }

    counter -= 2 // account for start and end nodes ig?

    println("Transfers: $counter")
}
