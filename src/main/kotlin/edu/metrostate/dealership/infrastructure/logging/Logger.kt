package edu.metrostate.dealership.infrastructure.logging

/**
 * We could have a LoggerFactory, but this is way too much for such small need.
 */
class Logger private constructor() {
    private val className: String = javaClass.name

    fun info(msg: String) {
        println("[$className] : $msg")
    }

    fun error(msg: String) {
        System.err.println("[$className] : $msg")
    }

    fun warn(msg: String) {
        val yellow = "\u001B[33m"
        val reset = "\u001B[0m"
        println("$yellow[$className] : $msg$reset")
    }

    companion object {
        val logger: Logger = Logger()
    }
}