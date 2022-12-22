package edu.austral.dissis.starships.model

class ShotGun : Gun {
    private val coolDown = 6
    private var timeElapsed = 0.0
    private val bulletWidth = 15.0
    private val bulletHeight = 30.0
    private val bulletDamage = 20.0
    private val bulletsQuantity = 5.0

    override fun shoot(position: Vector, angle: Double, ownerId: String): List<Bullet> {
        timeElapsed = 0.0
        val bullets = mutableListOf<Bullet>()
        for (i in 0..bulletsQuantity.toInt()) {
            val newAngle = -10 + Math.toDegrees(angle) + i * 5
            bullets.add(Bullet(position, Vector(0.0, 1.0).rotate(Math.toRadians(newAngle)).multiply(500.0), true, bulletDamage, bulletWidth, bulletHeight, ownerId))
        }
        return bullets
    }

    override fun canShot(timeElapsed: Double): Boolean {
        this.timeElapsed += timeElapsed
        return this.timeElapsed >= coolDown
    }
}