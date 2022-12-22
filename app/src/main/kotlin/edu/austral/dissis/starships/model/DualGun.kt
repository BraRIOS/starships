package edu.austral.dissis.starships.model

class DualGun : Gun {
    private val coolDown = 1
    private var timeElapsed = 0.0
    private val bulletWidth = 10.0
    private val bulletHeight = 20.0
    private val bulletDamage = 40.0

    //parallel separated bullets
    override fun shoot(position: Vector, angle: Double, ownerId: String): List<Bullet> {
        timeElapsed = 0.0
        val bullets = mutableListOf<Bullet>()
        bullets.add(Bullet(
            position.add(Vector.vectorFromModule(10.0, Math.toDegrees(angle)-180)),
            Vector.vectorFromModule(500.0, Math.toDegrees(angle)+90),
            true, bulletDamage, bulletWidth, bulletHeight, ownerId))
        bullets.add(Bullet(
            position.add(Vector.vectorFromModule(10.0, Math.toDegrees(angle))),
            Vector.vectorFromModule(500.0, Math.toDegrees(angle)+90),
            true, bulletDamage, bulletWidth, bulletHeight, ownerId))
        return bullets

    }

    override fun canShot(timeElapsed: Double): Boolean {
        this.timeElapsed += timeElapsed
        if (this.timeElapsed > coolDown) {
            this.timeElapsed = 0.0
            return true
        }
        return false
    }
}