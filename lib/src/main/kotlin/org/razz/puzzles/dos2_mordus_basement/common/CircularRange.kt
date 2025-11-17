package org.razz.puzzles.dos2_mordus_basement.common

interface CircularRange<Self : CircularRange<Self>> {
    val max: Int

    fun natural(value: Int): Natural<Self> = when {
        value < 0 -> max - (value % max)
        else -> value % max
    }.let { Natural(it) }

    @JvmInline
    value class Natural<T : CircularRange<T>>(val value: Int)

    operator fun Natural<Self>.inc() = natural(value + 1)
    operator fun Natural<Self>.plus(other: Natural<Self>) = natural(value + other.value)
    operator fun Natural<Self>.plus(other: Int) = natural(value + other)
    operator fun Natural<Self>.minus(other: Natural<Self>) = natural(value - other.value)
    operator fun Natural<Self>.minus(other: Int) = natural(value - other)
}

object Circular4 : CircularRange<Circular4> {
    override val max = 4
}

object Circular5 : CircularRange<Circular5> {
    override val max = 5
}

data class BinaryString(val value: String) {
    init {
        value.onEachIndexed { index, char ->
            require(char == '0' || char == '1') {
                "'$value' is not a binary string because char='$char' at position $index is not '0' or '1'."
            }
        }
    }

    fun toSquareMatrix(length: Int): List<List<Boolean>> = (length * length)
        .also {
            require(it >= value.length) { "'$value' is too big to fit inside a $length x $length square matrix." }
        }
        .let { size ->
            List(length) { MutableList(length) { false } }.apply {
                value.padStart(size).onEachIndexed { index, char ->
                    this[index / length][index % length] = char == '1'
                }
            }
        }

    companion object {
        fun Int.toBinaryString() = BinaryString(toString(2))
    }
}
