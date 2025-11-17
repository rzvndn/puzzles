package org.razz.puzzles.dos2_mordus_basement.dynamic.pojo

import org.razz.puzzles.dos2_mordus_basement.common.CircularRange

class ValueBoard<T : CircularRange<T>>(val circularRange: T, val cellValues: List<List<CircularRange.Natural<T>>>) {
    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is ValueBoard<T>) return false
        if (other.circularRange != circularRange) return false
        other.cellValues.onEachIndexed { row, columns ->
            columns.onEachIndexed { column, otherNatural ->
                if (otherNatural.value != cellValues[row][column].value) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int = cellValues.hashCode()

    override fun toString(): String = cellValues.joinToString(separator = "\n") { row ->
        row.joinToString(separator = " ") { "${it.value}" }
    }
}