package edu.austral.dissis.starships.model

class BasicGun : Gun {
    private val coolDown = 1.5
    private var timeElapsed = 0.0
    private val bulletWidth = 20.0
    private val bulletHeight = 30.0
    private val bulletDamage = 50.0

    override fun shoot(position: Vector, angle: Double, ownerId: String): List<Bullet> {
        timeElapsed = 0.0
        return listOf(Bullet(position, Vector(0.0, 1.0).rotate(angle).multiply(500.0), true, bulletDamage, bulletWidth, bulletHeight, ownerId))
    }

    override fun canShot(timeElapsed: Double): Boolean {
        this.timeElapsed += timeElapsed
        return this.timeElapsed >= coolDown
    }
}