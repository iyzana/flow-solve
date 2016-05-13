package de.adesso.flowsolver.solver.model

import java.awt.image.BufferedImage
import java.io.File
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.imageio.ImageIO

/**
 * Project: FlowSolve
 * <p/>
 * Created on 03.05.2016 at 00:14
 *
 * @author Jannis
 */
class PathsData(colors: Collection<Int>, grid: Grid) {
    val w = grid.w
    val h = grid.h
    val pathsMap = HashMap<Int, Array<Array<MutableList<Path>>>>()
    val colorPaths = HashMap<Int, Int>()
    
    init {
        for (color in colors) {
            pathsMap.put(color, Array(w) { x ->
                Array(h) { y ->
                    val size = if (grid[x, y].color == 0) 1000 else 0
                    ArrayList<Path>(size) as MutableList<Path>
                }
            })
            
            colorPaths.put(color, 0)
        }
    }
    
    fun createStatisticalData() {
        for (color in colorPaths.keys) {
            val imageView = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
            val imageData = BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
            
            val pathCount = colorPaths[color]!!
            val data = pathsMap[color]!!
            for (x in 0..w - 1) {
                for (y in 0..h - 1) {
                    imageView.setRGB(x, y, 255 * data[x][y].size / pathCount)
                    imageData.setRGB(x, y, 16777215 * data[x][y].size / pathCount)
                }
            }
            
            ImageIO.write(imageView, "BMP", File("${color}_view.bmp"))
            ImageIO.write(imageData, "BMP", File("${color}_data.bmp"))
        }
    }
    
    fun intersectsAll(node: Node, color: Int): Boolean {
        return get(color, node.x, node.y).size == colorPaths[color]
    }
    
    fun intersectsAll(path: Path, color: Int): Boolean {
        val targetSize = colorPaths[color]!!
        
        val intersecting = HashSet<Path>()
        path.forEach { node ->
            intersecting.addAll(get(color, Node.x(node), Node.y(node)))
            if (intersecting.size == targetSize) return true
        }
        
        return false
    }
    
    fun add(color: Int, paths: List<Path>) {
        val colors = pathsMap[color] ?: throw IllegalArgumentException("No data for color $color")
        colorPaths[color] = colorPaths[color]!! + paths.size
        paths.forEach { path ->
            path.forEach { node ->
                colors[Node.x(node)][Node.y(node)].add(path)
            }
        }
    }
    
    fun remove(color: Int, path: Path) {
        val colors = pathsMap[color] ?: throw IllegalArgumentException("No data for color $color")
        colorPaths[color] = colorPaths[color]!! - 1
        path.forEach { node ->
            colors[Node.x(node)][Node.y(node)].remove(path)
        }
    }
    
    operator fun get(color: Int, x: Int, y: Int): MutableList<Path> = pathsMap[color]?.get(x)?.get(y) ?: throw IllegalArgumentException("No data for color $color")
    
    fun sizeFor(color: Int) = colorPaths[color] ?: throw IllegalArgumentException("No data for color $color")
}