package edu.austral.dissis.starships.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class Vector(val x: Double, val y: Double) {

    val angle: Double
        get() = (atan2(y, x) - atan2(0.0, 1.0))
    val module: Double
        get() = (x.pow(2.0) + y.pow(2.0)).pow(0.5)

    fun add(other: Vector): Vector {
        return Vector(x + other.x, y + other.y)
    }

    fun subtract(other: Vector): Vector {
        return Vector(x - other.x, y - other.y)
    }

    fun multiply(scalar: Double): Vector {
        return Vector(x * scalar, y * scalar)
    }

    fun rotate(angle: Double): Vector {
        return Vector(x * cos(angle) - y * sin(angle), x * sin(angle) + y * cos(angle))
    }

    fun asUnitary(): Vector {
        val module = module
        return Vector(x / module, y / module)
    }

    fun angleToDegrees(): Double {
        return Math.toDegrees(angle)
    }

    fun setModule(l: Double): Vector {
        val currentModule: Double = module
        val currentAngle: Double = angle
        return if (currentModule == 0.0) {
            val x = l * cos(currentAngle)
            val y = l * sin(currentAngle)
            Vector(x, y)
        } else {
            this.asUnitary().multiply(l)
        }
    }

    companion object {

        fun vectorFromModule(module: Double, angleDegrees: Double): Vector {
            val angle = Math.toRadians(angleDegrees)
            return Vector(module * cos(angle), module * sin(angle))
        }
    }
}