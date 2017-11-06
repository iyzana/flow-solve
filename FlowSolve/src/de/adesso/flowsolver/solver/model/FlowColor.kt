package de.adesso.flowsolver.solver.model

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 23.05.2016
 */
enum class FlowColor(val hex: String, index: Int) {
    A("#fe0103", 1), //red
    B("#048100", 2), // green
    C("#0003f8", 3), // blue
    D("#f0ee0b", 4), // yellow
    E("#f88403", 5), // orange
    F("#01faf8", 6), // cyan
    G("#fb00fc", 7), // pink
    H("#a62927", 8), // dark-red
    I("#65017e", 9), // purple
    J("#ffffff", 10), // white
    K("#a5a2a3", 11), // grey
    L("#00ff02", 12), // light-green
    M("#b8b76a", 13), // dirt
    N("#040085", 14), // dark-blue
    O("#038082", 15), // kaki
    P("#ff138c", 16); // red-pink

    companion object {
        @JvmStatic
        fun getHex(color: Int): String {
            return values()[color - 1].hex;
        }
    }
}