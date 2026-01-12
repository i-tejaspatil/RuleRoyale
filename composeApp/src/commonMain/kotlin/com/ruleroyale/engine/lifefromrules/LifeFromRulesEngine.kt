package com.ruleroyale.engine.lifefromrules

/**
 * Engine for cellular automaton simulations based on configurable rules.
 *
 * This engine implements a generalized version of Conway's Game of Life,
 * allowing users to experiment with different survival and birth conditions.
 * Each cell's next state is determined by counting its alive neighbors and
 * applying the configured rules.
 *
 * @property rules The rule configuration defining under-population, over-population,
 *                 and reproduction thresholds
 */
class LifeFromRulesEngine(
    private val rules: LifeRules
) {

    /**
     * Computes the next generation of the cellular automaton.
     *
     * Applies the configured rules to every cell in the grid simultaneously,
     * creating a new grid representing the next time step.
     *
     * @param current The current grid state
     * @return A new Grid representing the next generation
     */
    fun tick(current: Grid): Grid {
        val nextCells = Array(current.rows) { row ->
            Array(current.cols) { col ->
                val position = Position(row, col)
                val aliveNeighbors = countAliveNeighbors(current, position)
                val currentState = current.getCell(position)

                nextState(currentState, aliveNeighbors)
            }
        }

        return current.copyWith(nextCells)
    }

    /**
     * Determines the next state of a cell based on its current state and neighbor count.
     *
     * @param currentState Whether the cell is currently alive or dead
     * @param aliveNeighbors The number of alive neighbors (0-8)
     * @return The cell's next state after applying the rules
     */
    private fun nextState(
        currentState: CellState,
        aliveNeighbors: Int
    ): CellState {
        return when (currentState) {
            CellState.ALIVE -> {
                when {
                    aliveNeighbors < rules.underPopulation -> CellState.DEAD
                    aliveNeighbors > rules.overPopulation -> CellState.DEAD
                    else -> CellState.ALIVE
                }
            }

            CellState.DEAD -> {
                if (aliveNeighbors == rules.reproduction)
                    CellState.ALIVE
                else
                    CellState.DEAD
            }
        }
    }

    /**
     * Counts the number of alive cells in the 8-neighborhood of a given position.
     *
     * Checks all 8 adjacent cells (horizontal, vertical, and diagonal) and counts
     * how many are alive. Positions outside the grid boundaries are ignored.
     *
     * @param grid The current grid state
     * @param position The cell position to check neighbors for
     * @return The count of alive neighbors (0-8)
     */
    private fun countAliveNeighbors(
        grid: Grid,
        position: Position
    ): Int {
        val directions = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1,          0 to 1,
            1 to -1,  1 to 0,  1 to 1
        )

        return directions.count { (dr, dc) ->
            val neighbor = Position(
                position.row + dr,
                position.col + dc
            )
            grid.isInside(neighbor) &&
                    grid.getCell(neighbor) == CellState.ALIVE
        }
    }
}
