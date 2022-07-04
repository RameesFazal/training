package com.example.kotlintraining.designpattern.adapter

import kotlin.math.pow
import kotlin.math.sqrt

open class RoundPeg {
    open var radius = 0.0

    constructor()
    constructor(radius: Double) {
        this.radius = radius
    }
}

class SquarePeg(val width: Double) {

    fun getSquare(): Double = width.pow(2.0)
}

class RoundHole constructor(val radius: Double) {

    fun fits(peg: RoundPeg): Boolean = radius >= peg.radius
}

class SquarePegAdapter(private val peg: SquarePeg) : RoundPeg() {
    override var radius: Double
        get() {
            return sqrt((peg.width / 2).pow(2.0) * 2)
        }
        set(radius) {
            super.radius = radius
        }

}
fun main(args: Array<String>) {
    val roundHole = RoundHole(5.0)
    val roundPeg = RoundPeg(5.0)
    if (roundHole.fits(roundPeg)) {
        println("Round peg fits round hole")
    }
    val smallSquarePeg = SquarePeg(2.0)
    val largeSquarePeg = SquarePeg(20.0)

    val smallSqPegAdapter = SquarePegAdapter(smallSquarePeg)
    val largeSqPegAdapter = SquarePegAdapter(largeSquarePeg)

    if (roundHole.fits(smallSqPegAdapter)) {
        println("Square peg width 2 fits round hole round 5.")
    }
    if (!roundHole.fits(largeSqPegAdapter)) {
        println("Square peg width 20 does not fit into round hole round 5.")
    }
}