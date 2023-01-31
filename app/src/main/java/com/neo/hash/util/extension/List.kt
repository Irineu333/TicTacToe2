package com.neo.hash.util.extension

fun <E> List<E>.updateAt(
    index: Int,
    update: (E) -> E
): List<E> {
    return List(size) {
        val element = get(it)

        if (it == index) {
            update(element)
        } else {
            element
        }
    }
}