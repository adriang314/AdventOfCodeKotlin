package common

fun <T> Set<T>.allPermutations(): Set<List<T>> {
    if (this.isEmpty())
        return emptySet()

    fun <T> allPermutationsInternal(list: List<T>): Set<List<T>> {
        if (list.isEmpty())
            return setOf(emptyList())
        val result: MutableSet<List<T>> = mutableSetOf()
        for (i in list.indices) {
            allPermutationsInternal(list - list[i]).forEach { item -> result.add(item + list[i]) }
        }
        return result
    }
    return allPermutationsInternal(this.toList())
}