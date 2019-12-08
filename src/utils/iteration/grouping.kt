package utils.iteration

fun <T> Iterable<T>.occurrences(value: T) = count { it == value }
fun <T> Sequence<T>.occurrences(value: T) = count { it == value }
fun CharSequence.occurrences(value: Char) = count { it == value }

fun <T> Iterable<T>.groupBySelf() = groupingBy { it }.eachCount()
fun <T> Sequence<T>.groupBySelf() = groupingBy { it }.eachCount()
