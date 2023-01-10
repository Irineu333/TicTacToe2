package com.neo.hash.util.function

fun interpolate(
    start: Float,
    stop: Float,
    fraction: Float
): Float {
    val difference = stop - start
    return fraction * difference + start
}