package de.adesso.flowsolver

/**
 * FlowSolve
 * adesso AG
 * @author kaiser
 * Created on 20.05.2016
 */
fun <K, V> Map<K, V>.forEach(action: (key: K, value: V) -> Unit) {
    forEach { entry -> action(entry.key, entry.value) }
}

fun <K, V, R> Map<K, V>.mapValues(action: (key: K, value: V) -> R): Map<K, R> {
    return mapValues { entry -> action(entry.key, entry.value) }
}