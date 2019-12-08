package utils.intcode

class Instruction(
    val arity: Int,
    val ignoreModeIndices: List<Int> = emptyList(),
    val operation: suspend IntcodeMachine.(List<Int>) -> Unit
)
