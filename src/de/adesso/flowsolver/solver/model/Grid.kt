package de.adesso.flowsolver.solver.model

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
data class Grid(val w: Int, val h: Int) {
    val grid: Array<Array<Node>>

    init {
        require(w <= 15 && h <= 15) { "maximum width and height is 15" }
        grid = Array(w.toInt()) { x ->
            Array(h.toInt()) { y ->
                Node(x, y)
            }
        }
    }

    operator fun get(x: Int, y: Int) = grid[x][y]
    operator fun get(x: Byte, y: Byte) = get(x.toInt(), y.toInt())
    
    operator fun unaryPlus(): Grid {
        for (y in 0..h - 1) {
            for (x in 0..w - 1) {
                if (this[x, y].color == 0) print(" .")
                else print(" " + Integer.toString(this[x, y].color, 36))
            }
            println()
        }
        println()

        return this
    }

    //    operator fun set(x: Int, y: Int, v: Node) {
    //        grid[x][y] = v
    //    }
    companion object {
        fun fromString(input: String): Grid {
            val lines = input.split(",").map { it.trim() }
            if (lines.size == 0) return Grid(0, 0)

            val grid = Grid(lines[0].length, lines.size)
            for ((y, line) in lines.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    grid[x, y].color = char - 'a'
                }
            }
            return grid
        }
    }
}