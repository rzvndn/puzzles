package org.razz.puzzles.dos2_mordus_basement.dynamic.pojo

import org.apache.commons.math3.util.Combinations

object TokenPlacements {
    object Combinations {
        private val combinations: MutableMap<Triple<NeighborhoodType, Int, Int>, List<List<Boolean>>> = mutableMapOf()

        operator fun get(neighborhoodType: NeighborhoodType, maximumCellValue: Int, desiredCenterValue: Int): List<List<Boolean>> = combinations
            .computeIfAbsent(Triple(neighborhoodType, maximumCellValue, desiredCenterValue)) { (type, maxValue, targetValue) ->
                generateSequence(targetValue) { previousValue ->
                    // This code computes the number of tokens that will produce the given target value
                    // when placed on cells in a neighborhood of the given type.
                    // Examples:
                    // A: for type=inside (aka 5 neighbors) and maxValue=4 we can achieve targetValue=1 in 2 ways:
                    // 1. place 1 token on one of the neighbors
                    // 2. place 5 tokens, one on each of the 5 neighbors
                    // B: for type=inside (aka 5 neighbors) and maxValue=2 we can achieve targetValue=0 in 3 ways:
                    // 1. place no tokens on any of the neighbors
                    // 2. place 2 tokens on 2 of the 5 neighbors
                    // 3. place 4 tokens on 4 of the 5 neighbors
                    (previousValue + maxValue).takeIf { it <= type.count }
                }.flatMap { tokensPlaced ->
                    Combinations(type.count, tokensPlaced).map { tokenIndexes ->
                        List(type.count) { index -> index in tokenIndexes }
                    }
                }.toList()
            }
    }

    @Suppress("EnumEntryName")
    enum class NeighborhoodType(val count: Int) {
        inside(5), edge(4), corner(3);

        companion object {
            operator fun invoke(size: Int, row: Int, column: Int): NeighborhoodType = size.dec().let { maxPosition ->
                val rowAndColumn = intArrayOf(row, column)
                val firstOrLast = intArrayOf(0, maxPosition)
                when {
                    rowAndColumn.all { it in firstOrLast } -> corner
                    rowAndColumn.none { it in firstOrLast } -> inside
                    else -> edge
                }
            }
        }
    }
}


fun main() {
    val corner41 = TokenPlacements.Combinations[TokenPlacements.NeighborhoodType.corner, 4, 1]
    val corner20 = TokenPlacements.Combinations[TokenPlacements.NeighborhoodType.corner, 2, 0]
    val edge43 = TokenPlacements.Combinations[TokenPlacements.NeighborhoodType.edge, 4, 3]
    val inside32 = TokenPlacements.Combinations[TokenPlacements.NeighborhoodType.inside, 3, 2]
    println("Token placement combinations:")
    println("corner / 4 / 1 - ${corner41.size}\n    - $corner41")
    println("corner / 2 / 0 - ${corner20.size}\n    - $corner20")
    println("edge / 4 / 3 - ${edge43.size}\n    - $edge43")
    println("inside / 3 / 2 - ${inside32.size}\n    - $inside32")
}
