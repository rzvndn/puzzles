package org.razz.puzzles.dos2_mordus_basement.dynamic.pojo

data class Position(val row: Int, val column: Int, val size: Int) {
    val isValid = row >= 0 && row < size && column >= 0 && column < size

    val type = TokenPlacements.NeighborhoodType(size, row, column)

    companion object {
        fun neighbors(position: Position) = neighborhood(position.row, position.column, position.size)
            .drop(1).sortedBy { it.type.count }

        fun neighborhood(row: Int, column: Int, size: Int): List<Position> = listOf(
            Position(row, column, size),
            Position(row - 1, column, size),
            Position(row + 1, column, size),
            Position(row, column - 1, size),
            Position(row, column + 1, size),
        ).filter { it.isValid }

        fun prioritizedPositions(size: Int): List<Position> = (0..<size).flatMap { row ->
            (0..<size).map { column ->
                Position(row, column, size)
            }
        }.sortedBy { it.type.count }
    }
}