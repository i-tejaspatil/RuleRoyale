package com.ruleroyale.engine.lifefromrules

class Grid(
    val rows: Int,
    val cols: Int,
    private val cells: Array<Array<CellState>>
) {

    fun getCell(position: Position): CellState =
        cells[position.row][position.col]

    fun isInside(position: Position): Boolean =
        position.row in 0 until rows && position.col in 0 until cols

    fun allPositions(): List<Position> =
        (0 until rows).flatMap { r ->
            (0 until cols).map { c ->
                Position(r, c)
            }
        }

    fun countAlive(): Int =
        cells.sumOf { row ->
            row.count { it == CellState.ALIVE }
        }

    fun copyWith(updatedCells: Array<Array<CellState>>): Grid =
        Grid(rows, cols, updatedCells)

    // 🔑 REQUIRED FOR STABILITY + OSCILLATION
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grid) return false
        if (rows != other.rows || cols != other.cols) return false

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (cells[r][c] != other.cells[r][c]) return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        for (row in cells) {
            for (cell in row) {
                result = 31 * result + cell.hashCode()
            }
        }
        return result
    }
}
