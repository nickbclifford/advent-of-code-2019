package utils.iteration

// taken from KotlinDiscreteMathToolkit
// https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit/blob/master/src/main/java/com/marcinmoskala/math/PermutationsExt.kt

/* This function returns all permutations of elements from set. These are different ways to arrange elements from this list.  */
fun <T> Set<T>.permutations(): Set<List<T>> = toList().permutations()

/* This function returns all permutations of elements from list. These are different ways to arrange elements from this list.  */
fun <T> List<T>.permutations(): Set<List<T>> = when {
    isEmpty() -> setOf()
    size == 1 -> setOf(listOf(get(0)))
    else -> {
        val element = get(0)
        drop(1).permutations()
            .flatMap { sublist -> (0..sublist.size).map { i -> sublist.plusAt(i, element) } }
            .toSet()
    }
}


internal fun <T> List<T>.plusAt(index: Int, element: T): List<T> = when {
    index !in 0..size -> throw Error("Cannot put at index $index because size is $size")
    index == 0 -> listOf(element) + this
    index == size -> this + element
    else -> dropLast(size - index) + element + drop(index)
}
