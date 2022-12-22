package edu.austral.dissis.starships.model

class Starship(
    override val position: Vector, override val speed: Vector, override val isAlive: Boolean,
    val gun: Gun, val lives: Int, val points: Double, val size: Double
) : GameObject {
    private var id: Int = 0

    companion object {
        var ID = 0
    }

    init {
        id = ID++
    }

    private fun setId(newId: Int): Starship {
        id = newId
        return this
    }

    override fun idToString() :String {
        return "Starship-$id"
    }

    fun setGun(newGun: Gun): Starship {
        return Starship(position, speed, isAlive, newGun, lives, points,  size).setId(id)
    }

    fun addPoints(pointsAdd: Double): Starship {
        return Starship(position, speed, isAlive, gun, lives, points + pointsAdd, size).setId(id)
    }

    private fun takeDamage(): Starship {
        if (lives - 1 == 0) {
            return Starship(position, speed, false, gun, 0, points, size).setId(id)
        }
        return Starship(position, speed, isAlive, gun, lives-1, points, size).setId(id)
    }

    override fun handleCollisionWith(collider: GameObject): GameObject? {
        return collider.accept(this) as Starship?
    }

    override fun update(secondsSinceLastTime: Double): Starship {
        return Starship(position.add(speed.multiply(secondsSinceLastTime).rotate(Math.toRadians(90.0))), speed, isAlive, gun, lives, points, size).setId(id)
    }

    override fun rotate(angle: Double): Starship {
        return Starship(position, speed.rotate(angle), isAlive, gun, lives, points, size).setId(id)
    }

    override fun setPosition(position: Vector): Starship {
        return Starship(position, speed, isAlive, gun, lives, points, size).setId(id)
    }

    override fun setSpeed(speed: Vector): Starship {
        return Starship(position, speed, isAlive, gun, lives, points, size).setId(id)
    }

    override fun setAlive(alive: Boolean): Starship {
        return Starship(position, speed, alive, gun, lives, points, size).setId(id)
    }

    override fun accept(visitor: ColliderVisitor<GameObject>): GameObject? {
        return visitor.visit(this)
    }

    override fun visit(asteroid: Asteroid): Starship {
        return takeDamage()
    }

    override fun visit(bullet: Bullet): Starship? {
        return if (bullet.ownerId != idToString()) {
            takeDamage()
        } else {
            null
        }
    }

    override fun visit(starship: Starship): Starship? {
        return null
    }
}