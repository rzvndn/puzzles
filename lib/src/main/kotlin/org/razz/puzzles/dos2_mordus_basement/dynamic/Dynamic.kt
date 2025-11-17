package org.razz.puzzles.dos2_mordus_basement.dynamic

import org.razz.puzzles.dos2_mordus_basement.common.Circular4
import org.razz.puzzles.dos2_mordus_basement.common.CircularRange
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.Position
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.Position.Companion.neighbors
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.Position.Companion.prioritizedPositions
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.SolutionType
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.TokenBoard
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.TokenPlacements
import org.razz.puzzles.dos2_mordus_basement.dynamic.pojo.ValueBoard
import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Mordus' Basement Pressure Plates Puzzle.
 *
 * We are given 16 pressure plates arranged in a 4 by 4 square:
 * ⬜️⬜️⬜️⬜️
 * ⬜️⬜️⬜️⬜️
 * ⬜️⬜️⬜️⬜️
 * ⬜️⬜️⬜️⬜️
 * Symbols may appear above a pressure plate depending on how many pressure
 * plates in its (von Neumann) neighborhood are activated in total:
 * - 0 - A
 * - 1 - B
 * - 2 - C
 * - 3 - D
 * - 4 - A
 * - 5 - B
 * The von Neumann neighborhood of a pressure plate is composed of the pressure
 * plate itself along with its neighbors to the east, west, north, and south.
 *
 * You may activate any number of pressure plates. Order does not matter.
 *
 * Find out which pressure plates should be pressed to produce a given
 * 4 by 4 square of desired symbols.
 *
 * E.g. given the following symbols:
 * A B B C
 * A B D B
 * B C C C
 * A B B A
 * then the following pressure plates should be pressed:
 * ⬜️⬜️❎️⬜️
 * ⬜️⬜️⬜️❎️
 * ⬜️❎️❎️⬜️
 * ⬜️⬜️⬜️⬜️
 */
fun main() {
    measureNanoTime {
        Dynamic.solveAll(4, Circular4).also(::println)
    }.let {
        println("Execution time: ${it.nanoseconds} seconds")
    }
}

object Dynamic {
    fun <T : CircularRange<T>> solveAll(size: Int, circularRange: T, step: Int = 1, verboseLogging: Boolean = false): String =
        TokenBoard.permutations(size, step).let { permutations ->
            permutations.mapIndexed { index, tokenBoard ->
                println(permutations.size - index)
                solve(tokenBoard = tokenBoard, circularRange, verboseLogging)
            }.groupBy { it }.entries
                .joinToString("\n", prefix = "Stats:\n") { (solutionType, list) -> "- $solutionType: ${list.size}" }
        }


    fun <T : CircularRange<T>> solve(tokenBoard: TokenBoard, circularRange: T, verboseLogging: Boolean = false): SolutionType =
        tokenBoard.toValueBoard(circularRange).let { valueBoard ->
            when (val actualSolution = solve(valueBoard)) {
                null -> SolutionType.failed.also {
                    if (verboseLogging) println("Null solution does not match expected solution:\n$tokenBoard")
                }

                tokenBoard -> SolutionType.exact.also {
                    if (verboseLogging) println("Correct solution found for token placement:\n$actualSolution")
                }

                else -> SolutionType.alternate.also {
                    if (verboseLogging) println("Alternative solution:\n$actualSolution\nfound for desired values:\n$valueBoard")
                }
            }
        }

    fun <T : CircularRange<T>> solve(
        valueBoard: ValueBoard<T>,
        currentTokens: List<List<Boolean?>> = List(valueBoard.cellValues.size) { List(valueBoard.cellValues.size) { null } },
        availablePositions: List<Position> = prioritizedPositions(valueBoard.cellValues.size),
    ): TokenBoard? {
        val pickedPosition = availablePositions.firstOrNull()
            ?: return TokenBoard.of(currentTokens)
        val pickedPositionAndNeighbors = listOf(pickedPosition).plus(neighbors(pickedPosition))
        val nextAvailablePositions = availablePositions.minus(pickedPosition)
        TokenPlacements.Combinations[pickedPosition.type, valueBoard.circularRange.max, valueBoard.cellValues[pickedPosition.row][pickedPosition.column].value]
            .filter { placements ->
                pickedPositionAndNeighbors.filterIndexed { index, (row, column, _) ->
                    currentTokens[row][column]
                        ?.equals(placements[index])
                        ?: true
                }.size == pickedPositionAndNeighbors.size
            }
            .forEach { placements ->
                val potentialSolution = currentTokens.mapIndexed { row, columns ->
                    columns.mapIndexed { column, token ->
                        token ?: pickedPositionAndNeighbors.indexOf(Position(row, column, pickedPosition.size))
                            .takeIf { it != -1 }
                            ?.let { placements[it] }
                    }
                }.let { nextCurrentTokens ->
                    solve(valueBoard, nextCurrentTokens, nextAvailablePositions)
                }
                if (potentialSolution != null && potentialSolution.isValidSolutionFor(valueBoard)) {
                    return potentialSolution
                }
            }

        return null
    }
}
