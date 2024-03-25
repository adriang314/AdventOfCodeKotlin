package common

object List2D {

    fun <T> flipVertically(list2D: List<List<T>>): List<List<T>> {
        return list2D.map { it.reversed() }
    }

    fun <T> flipHorizontally(list2D: List<List<T>>): List<List<T>> {
        return list2D.reversed()
    }

    fun <T> rotateRight(list2D: List<List<T>>, times: Int = 1): List<List<T>> {
        var result: List<List<T>> = list2D
        repeat(times) {
            result = result.flatMap { it.withIndex() }.groupBy({ (i, _) -> i }, { (_, v) -> v })
                .map { (_, v) -> v.reversed() }
        }

        return result
    }

    fun <T> removeBorder(list2D: List<List<T>>): List<List<T>> {
        return list2D.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }
    }
}