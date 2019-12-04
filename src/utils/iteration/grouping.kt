package utils.iteration

fun <T> Iterable<T>.groupBySelf() = groupingBy { it }.eachCount()
fun <T> Sequence<T>.groupBySelf() = groupingBy { it }.eachCount()
