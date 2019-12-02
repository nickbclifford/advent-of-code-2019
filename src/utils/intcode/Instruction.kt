package utils.intcode

class Instruction(val arity: Int, val operation: IntcodeMachine.(List<Int>) -> Unit)
