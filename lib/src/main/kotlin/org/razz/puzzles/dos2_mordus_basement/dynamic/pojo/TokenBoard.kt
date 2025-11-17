package org.razz.puzzles.dos2_mordus_basement.dynamic.pojo

import org.apache.commons.math3.util.ArithmeticUtils.pow
import org.razz.puzzles.dos2_mordus_basement.common.BinaryString.Companion.toBinaryString
import org.razz.puzzles.dos2_mordus_basement.common.CircularRange
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.Position.Companion.neighborhood

class TokenBoard(val tokenPlacements: List<List<Boolean>>) {
    fun <T : CircularRange<T>> toValueBoard(circularRange: T): ValueBoard<T> {
        val cellValues = List(tokenPlacements.size) { row ->
            List(tokenPlacements.size) { column ->
                neighborhood(row, column, tokenPlacements.size)
                    .count { (row, column, _) -> tokenPlacements[row][column] }
                    .let { circularRange.natural(it) }
            }
        }
        return ValueBoard(circularRange, cellValues)
    }

    fun <T : CircularRange<T>> isValidSolutionFor(valueBoard: ValueBoard<T>): Boolean = toValueBoard(valueBoard.circularRange) == valueBoard

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is TokenBoard) return false
        other.tokenPlacements.forEachIndexed { row, columns ->
            columns.forEachIndexed { column, otherToken ->
                if (otherToken != tokenPlacements[row][column]) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int = tokenPlacements.hashCode()

    override fun toString(): String = tokenPlacements.joinToString(separator = "\n") { row ->
        row.joinToString(separator = " ") { if (it) "1" else "0" }
    }

    companion object {
        fun permutations(size: Int, step: Int = 1): List<TokenBoard> = IntRange(0, pow(2, size * size) - 1)
            .chunked(step) { it.first() }
            .map { potentialSolution ->
            potentialSolution.toBinaryString().toSquareMatrix(size)
                .map { it.toList() }
                .toList()
                .let { TokenBoard(it) }
        }

        fun of(currentTokens: List<List<Boolean?>>): TokenBoard? = TokenBoard(
            tokenPlacements = currentTokens.map { rows -> rows.map { it ?: return null } }
        )
    }
}