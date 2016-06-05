package de.adesso.flowsolver

import de.adesso.flowsolver.solver.model.Grid
import java.util.Scanner

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 12.05.2016
 */
enum class Pack(val file: String) {
    Regular5("levels/Regular/5x5.dat"),
    Regular6("levels/Regular/6x6.dat"),
    Regular7("levels/Regular/7x7.dat"),
    Regular8("levels/Regular/8x8.dat"),
    Regular9("levels/Regular/9x9.dat"),
    Jumbo10("levels/Jumbo/10x10.dat"),
    Jumbo11("levels/Jumbo/11x11.dat"),
    Jumbo12("levels/Jumbo/12x12.dat"),
    Jumbo13("levels/Jumbo/13x13.dat"),
    Jumbo14("levels/Jumbo/14x14.dat"),
}

object TestGrids {
    private val A = 1;
    private val B = 2;
    private val C = 3;
    private val D = 4;
    private val E = 5;
    private val F = 6;
    private val G = 7;
    private val H = 8;
    private val I = 9;
    private val J = 10;
    private val K = 11;
    private val L = 12;
    private val M = 13;
    private val N = 14;
    private val O = 15;
    
    fun loadGrid(pack: Pack, level: Int) = loadGrids(pack)[level]
    
    fun loadGrids(pack: Pack): List<Grid> {
        val file = TestGrids::class.java.classLoader.getResource(pack.file).readText()
        
        return Grid.fromFile(file)
    }
    
    fun readGrid() = Grid.fromString(Scanner(System.`in`.reader()).nextLine())
    
    fun createHard9Grid() = Grid.fromString("aaaaaaaaa,aaaaaaaea,aadaaaada,aahafaaaa,aaiahcaaa,aaaabgafa,aaaaiaaaa,aeaaaagca,aaabaaaaa")
    
    fun create5Grid(): Grid {
        return Grid(5, 5).apply {
            this[0, 0].color = A
            this[1, 3].color = B
            this[1, 4].color = A
            this[2, 0].color = B
            this[2, 1].color = C
            this[2, 4].color = C
            this[3, 3].color = D
            this[3, 4].color = E
            this[4, 0].color = D
            this[4, 1].color = E
        }
    }
    
    fun create7Grid(): Grid {
        return Grid(7, 7).apply {
            this[1, 2].color = E
            this[2, 4].color = B
            this[3, 3].color = B
            this[4, 3].color = F
            this[4, 4].color = D
            this[4, 5].color = A
            this[5, 1].color = E
            this[5, 5].color = D
            this[5, 6].color = C
            this[6, 0].color = C
            this[6, 1].color = A
            this[6, 6].color = F
        }
    }
    
    fun createHard8Grid() = Grid.fromString("aaaaaaaa,fcaaeaad,baaaaaaa,aaaabada,aaafaaaa,aaaaacaa,eaaaaaaa,aaaaaaaa")
    fun createHard8Grid2() = Grid.fromString("efaaaaaa,aaaaaaaa,aaadacaa,aaaaaaaa,aaadfaaa,aaabaeab,acaaaaaa,aaaaaaaa")
    fun createFill8Grid() = Grid.fromString("afhcaaaa,aeaaaada,aaaaaaaa,faaaaaac,aaaaaaaa,ebaaaaaa,baaagdga,haaaaaaa")
    
    fun create9Grid(): Grid {
        return Grid(9, 9).apply {
            this[0, 5].color = D
            this[1, 1].color = A
            this[1, 4].color = B
            this[1, 5].color = G
            this[1, 6].color = I
            this[1, 7].color = H
            this[2, 1].color = F
            this[2, 6].color = D
            this[3, 1].color = C
            this[3, 2].color = F
            this[3, 4].color = B
            this[4, 2].color = E
            this[4, 4].color = A
            this[6, 2].color = E
            this[7, 2].color = C
            this[7, 3].color = G
            this[7, 5].color = H
            this[8, 5].color = I
        }
    }
    
    fun create10Grid() = Grid.fromString("aaaaaaaaaa,afaaacdhja,aaaeaaaaaa,aaaaaiaaaa,gaaaabahaa,aaaaaaaaaa,aaaaabeaaa,acfadaaiaa,aaaaaajaaa,gaaaaaaaaa")
    
    fun create11Grid() = Grid.fromString("aaaaaaaaaaa,aaaaaaeakah,aaaaaalafaa,alegaaaaaaa,aamaaaaagaa,amaaaaaaaaa,afaaaaaaaba,adaahaakaaa,aaaaadajaaa,baaaaaacaaj,iaaaaaaaiac")
    
    fun create12Grid() = Grid.fromString("maaaaaaaidae,aacacfkaaaal,aaaaaaaaaaaa,aaaaaaagaaaa,gaaaaaaaaaaa,amafaadaaaaa,aaaaaaaaaaaa,aaaaehjajiaa,aaaaaaaaaaaa,aabaaaabhaka,aaaaaaaaaaaa,aaaaaaaaaaal")
    fun create12Grid2() = Grid.fromString("aaaaaaaaaaaa,aaaaaaaaaaaa,aaaaaaaaaaaa,aaaaaaaaakaa,aaaadaalahaa,aakaaaaaaiaa,ahbaaaalaajm,aaaabcdcaaaa,aagaaaaaaaaa,aaaaafaiaeaa,aaaaagaeaaaa,aajaaaafmaaa")
    fun create12Grid3() = Grid.fromString("aaaaaaaaaaaa,aaaaaaiakaka,aaaabaaaaaaa,aaacagaiagaa,aaaaaaaaaaaa,aafbaaaaaaaa,aaaaaaaaaaae,aadfaahaaaaa,aaaaaaaajaaa,aaaaaaaaaaaa,aaaaaeaacaaa,daaaaaaaaajh")
    
    fun create13Grid() = Grid.fromString("jaaaaaaaaaaaa,baagaaakoaaoa,ajabaaacaaaka,aeaaaacaalaaa,aaaaaaaaaaaaa,aaaaaaalfmaaa,aaaaaaiahaaaa,aaaaaaaaaafaa,aaaaanaaaadaa,gaaaaaaahaaaa,aaaaaiaamaaaa,adanaaaaaaaaa,aaaaaaaaaaaae")
    
    fun create14Grid(): Grid {
        return Grid(14, 14).apply {
            this[0, 7].color = D
            this[1, 1].color = N
            this[1, 4].color = I
            this[2, 6].color = F
            this[2, 10].color = G
            this[3, 5].color = A
            this[3, 10].color = E
            this[4, 5].color = O
            this[4, 6].color = F
            this[4, 8].color = J
            this[4, 9].color = B
            this[4, 11].color = G
            this[5, 1].color = N
            this[6, 5].color = L
            this[7, 6].color = A
            this[7, 7].color = L
            this[7, 8].color = E
            this[7, 9].color = B
            this[7, 13].color = K
            this[8, 3].color = M
            this[9, 8].color = C
            this[9, 9].color = K
            this[9, 13].color = M
            this[10, 4].color = D
            this[10, 11].color = C
            this[10, 13].color = H
            this[11, 2].color = O
            this[11, 7].color = J
            this[12, 12].color = I
            this[13, 10].color = H
        }
    }
}