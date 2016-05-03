package de.adesso.flowsolver

import java.util.*

/**
 * Project: FlowSolve
 * <p/>
 * Created on 03.05.2016 at 00:14
 *
 * @author Jannis
 */
class PathsData(colors: Collection<Int>, grid: Grid) {
    val pathsMap = HashMap<Int, Array<Array<MutableList<Path>>>>()
    val colorPaths = HashMap<Int, Int>()

    init {
        for (color in colors) {
            pathsMap.put(color, Array(grid.w) { x ->
                Array(grid.h) { y ->
                    val size = if (grid[x, y].color == 0) 1000 else 0
                    ArrayList<Path>(size) as MutableList<Path>
                }
            })

            colorPaths.put(color, 0)
        }
    }

    fun intersectsAll(node: Node, color: Int): Boolean {
        return get(color, node.x, node.y).size == colorPaths[color]
    }

    fun intersectsAll(path: Path, color: Int): Boolean {
        val targetSize = colorPaths[color]

        return calcValue(color, path, targetSize)
    }

    private fun calcValue(color: Int, path: Path, targetSize: Int?): Boolean {
        val intersecting = HashSet<Path>()
        path.forEach { node ->
            intersecting.addAll(get(color, x(node), y(node)))
            if (intersecting.size == targetSize) return true
        }

        return false
    }

    fun add(color: Int, paths: List<Path>) {
        val colors = pathsMap[color] ?: throw IllegalArgumentException("No data for color $color")
        colorPaths[color] = colorPaths[color]!! + paths.size
        paths.forEach { path ->
            path.forEach { node ->
                colors[x(node)][y(node)].add(path)
            }
        }
    }

    fun remove(color: Int, path: Path) {
        val colors = pathsMap[color] ?: throw IllegalArgumentException("No data for color $color")
        colorPaths[color] = colorPaths[color]!! - 1
        path.forEach { node ->
            colors[x(node)][y(node)].remove(path)
        }
    }

    operator fun get(color: Int, x: Int, y: Int): MutableList<Path> = pathsMap[color]?.get(x)?.get(y) ?: throw IllegalArgumentException("No data for color $color")

    fun sizeFor(color: Int) = colorPaths[color] ?: throw IllegalArgumentException("No data for color $color")
}