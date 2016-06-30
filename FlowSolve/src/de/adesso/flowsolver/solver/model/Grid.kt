package de.adesso.flowsolver.solver.model

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.ArrayList
import javax.imageio.ImageIO

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 28.04.2016
 */
data class Grid(val w: Int, val h: Int) {
    val grid: Array<Array<Node>>
    
    var nodes: List<Node>
        private set
    
    private constructor(w: Int, h: Int, nodes: List<Node>) : this(w, h) {
        require(nodes.size == w * h)
    }
    
    init {
        require(w <= 15 && h <= 15) { "maximum width and height is 15" }
        grid = Array(w) { x ->
            Array(h) { y ->
                Node(x, y)
            }
        }
        nodes = (0..w - 1).flatMap { x -> (0..h - 1).map { y -> grid[x][y] } }
    }
    
    fun valid(x: Int, y: Int) = x >= 0 && y >= 0 && x < this.w && y < this.h
    
    fun addPath(path: Path, color: Int) {
        path.forEach { node ->
            this[node.x, node.y].color = color
        }
    }
    
    operator fun get(x: Int, y: Int, wallColor: Int): Node {
        if (valid(x, y)) return grid[x][y]
        if (x >= -1 && y >= -1 && x <= this.w && y <= this.h) return Node(x, y, wallColor)
        throw IndexOutOfBoundsException("x: $x, y: $y")
    }
    
    operator fun get(x: Int, y: Int) = grid[x][y]
    operator fun get(node: Node) = get(node.x, node.y)
    
    private operator fun set(x: Int, y: Int, v: Node) {
        grid[x][y] = v
    }
    
    fun copy(): Grid {
        val copy = Grid(w, h)
        val nodesCache = ArrayList<Node>(w * h)
        for (y in 0..h - 1) {
            for (x in 0..w - 1) {
                copy[x, y] = this[x, y].copy()
                nodesCache.add(copy[x, y])
            }
        }
        
        copy.nodes = nodesCache
        
        return copy
    }
    
    fun print() {
        for (y in 0..h - 1) {
            for (x in 0..w - 1) {
                if (this[x, y].color == 0) print(" .")
                else print(" " + ('a' + this[x, y].color - 1))
            }
            println()
        }
        println()
    }
    
    fun toImage(fileName: String) {
        val image = BufferedImage(w * 10, h * 10, BufferedImage.TYPE_INT_RGB)
        val graphics = image.graphics
        
        for (x in 0..w - 1) {
            for (y in 0..h - 1) {
                val flowColor = this[x, y].color
                graphics.color = if (flowColor == 0) Color.BLACK else Color.decode("#" + FlowColor.values()[flowColor - 1].hex)
                graphics.fillRect(x * 10, y * 10, 10, 10)
            }
        }
        
        graphics.dispose()
        
        val file = File("results", "$fileName.bmp")
        file.parentFile.mkdirs()
        ImageIO.write(image, "BMP", file)
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
        
        fun fromFile(input: String): List<Grid> {
            val formatInput = input.replace("\r\n", "\n").replace("\r", "\n")
            val levels = formatInput.split("\n\n")
            
            return levels.mapIndexed { index, level ->
//                println("loading level $index")
                val lines = level.split('\n').map { it.trim() }
                
                if (lines.size == 0) Grid(0, 0)
                else {
                    val grid = Grid(lines[0].length, lines.size)
                    
                    for ((y, line) in lines.withIndex()) {
                        for ((x, char) in line.withIndex()) {
                            grid[x, y].color = if (char == '0') 0 else char - 'a' + 1
                        }
                    }
                    
                    grid
                }
            }
        }
    }
}