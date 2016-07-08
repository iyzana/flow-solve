package de.adesso.flowsolver.solver.model

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 23.05.2016
 */
enum class FlowColor(val hex: String, index: Int) {
    A("#fe0103", 1),
    B("#048100", 2),
    C("#0003f8", 3),
    D("#f0ee0b", 4),
    E("#f88403", 5),
    F("#01faf8", 6),
    G("#fb00fc", 7),
    H("#a62927", 8),
    I("#65017e", 9),
    J("#ffffff", 10),
    K("#a5a2a3", 11),
    L("#00ff02", 12),
    M("#b8b76a", 13),
    N("#040085", 14),
    O("#038082", 15),
    P("#ff138c", 16);

    companion object {
        @JvmStatic
        fun getHex(color: Int): String {
            return values()[color - 1].hex;
        }
    }
}