package edu.austral.dissis.starships.model

interface Gun {
    fun shoot(position: Vector, angle: Double, ownerId: String): List<Bullet>
    fun canShot(timeElapsed: Double): Boolean
}