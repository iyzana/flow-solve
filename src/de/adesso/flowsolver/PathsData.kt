package de.adesso.flowsolver

import java.util.*

/**
 * Project: FlowSolve
 * <p/>
 * Created on 03.05.2016 at 00:14
 *
 * @author Jannis
 */
class PathsData(colors: Collection<Int>, w: Int, h: Int) {
    val pathsMap = HashMap<Int, Array<Array<MutableList<Path>>>>()
    val colorPaths = HashMap<Int, Int>()

    init {
        for (color in colors) {
            pathsMap.put(color, Array(w) { Array(h) { ArrayList<Path>(100000) as MutableList<Path> } })
            colorPaths.put(color, 0)
        }
    }

    fun intersectsAll(path: Path, color: Int): Boolean {
        val intersecting = HashSet<Path>()
        path.forEach { node ->
            intersecting.addAll(get(color, x(node), y(node)))
            if (intersecting.size == colorPaths[color]) return true
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

    operator fun get(color: Int, x: Int, y: Int): MutableList<Path> = pathsMap[color]?.get(x)?.get(y) ?: throw IllegalArgumentException("No data for color $color")

    fun sizeFor(color: Int) = colorPaths[color] ?: throw IllegalArgumentException("No data for color $color")
}