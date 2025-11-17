package org.razz.puzzles.dos2_mordus_basement.bruteforce

import org.apache.commons.math3.util.ArithmeticUtils.pow
import org.razz.puzzles.dos2_mordus_basement.common.BinaryString.Companion.toBinaryString
import org.razz.puzzles.dos2_mordus_basement.common.Circular4
import org.razz.puzzles.dos2_mordus_basement.common.Circular4.inc
import org.razz.puzzles.dos2_mordus_basement.common.CircularRange
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.SolutionType
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.TokenBoard
import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.nanoseconds

object BruteForce {
    const val size: Int = 4
    val zero = Circular4.natural(0)

    fun Array<Array<CircularRange.Natural<Circular4>>>.addEffects(row: Int, column: Int) {
        this[row][column]++
        (row - 1).takeIf { it >= 0 }?.also { this[it][column]++ }
        (row + 1).takeIf { it < size }?.also { this[it][column]++ }
        (column - 1).takeIf { it >= 0 }?.also { this[row][it]++ }
        (column + 1).takeIf { it < size }?.also { this[row][it]++ }
    }

    fun Array<Array<CircularRange.Natural<Circular4>>>.matches(other: Array<Array<CircularRange.Natural<Circular4>>>): Boolean {
        onEachIndexed { row, columns ->
            columns.onEachIndexed { column, circular4 ->
                if (other[row][column].value != circular4.value) return false
            }
        }
        return true
    }

    fun solve(desiredEffects: Array<Array<CircularRange.Natural<Circular4>>>): List<List<Boolean>>? =
        IntRange(0, pow(2, desiredEffects.size * desiredEffects.size))
            .asSequence()
            .map { potentialSolution -> potentialSolution.toBinaryString().toSquareMatrix(desiredEffects.size) }
            .firstOrNull { potentialSolutionBoard ->
                val potentialEffects: Array<Array<CircularRange.Natural<Circular4>>> = Array(size) { Array(size) { zero } }
                potentialSolutionBoard.onEachIndexed { row, columns ->
                    columns.onEachIndexed { column, hasToken ->
                        if (hasToken) potentialEffects.addEffects(row, column)
                    }
                }
                potentialEffects.matches(desiredEffects)
            }
}

fun main() {
    measureNanoTime {
        exhaustiveBruteForce().also(::println)
    }.let {
        println("Execution time: ${it.nanoseconds} seconds")
    }
}

fun exhaustiveBruteForce(): String {
    val permutations = TokenBoard.permutations(4)
    return permutations.mapIndexed { index, tokenBoard ->
        println(permutations.size-index)
        val valueBoard = tokenBoard.toValueBoard(Circular4)
        val target = valueBoard.cellValues.map { it.toTypedArray() }.toTypedArray()
        when (val actualSolution = BruteForce.solve(target)?.let { TokenBoard.of(it) }) {
            null -> SolutionType.failed.also {
//                println("Null solution does not match expected solution:\n$tokenBoard")
            }

            tokenBoard -> SolutionType.exact.also {
//                println("Correct solution found for token placement:\n$actualSolution")
            }

            else -> SolutionType.alternate.also {
//                println("Alternative solution:\n$actualSolution\nfound for desired values:\n$valueBoard")
            }
        }
    }.groupBy { it }.entries
        .joinToString("\n", prefix = "Stats:\n") { (solutionType, list) -> "- $solutionType: ${list.size}" }
}
