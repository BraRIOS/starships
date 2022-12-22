package edu.austral.dissis.starships.model

class Bullet(override val position: Vector, override val speed: Vector, override val isAlive: Boolean, val damage: Double,
             val bulletWidth: Double, val bulletHeight: Double, val ownerId : String) : GameObject{
    private var id: Int = 0

    companion object {
        var ID = 0
    }

    init {
        id = ID++
    }

    private fun setId(newId: Int): Bullet {
        id = newId
        return this
    }

    override fun idToString() :String {
        return "Bullet-$id"
    }

    override fun handleCollisionWith(collider: GameObject): GameObject? {
        return collider.accept(this) as Bullet?
    }

    override fun update(secondsSinceLastTime: Double): Bullet {
        return Bullet(position.add(speed.multiply(secondsSinceLastTime)), speed, isAlive, damage, bulletWidth, bulletHeight, ownerId).setId(id)
    }

    override fun rotate(angle: Double): Bullet {
        return Bullet(position, speed.rotate(angle), isAlive, damage, bulletWidth, bulletHeight, ownerId).setId(id)
    }

    override fun setPosition(position: Vector): Bullet {
        return Bullet(position, speed, isAlive, damage, bulletWidth, bulletHeight, ownerId).setId(id)
    }

    override fun setSpeed(speed: Vector): Bullet {
        return Bullet(position, speed, isAlive, damage, bulletWidth, bulletHeight, ownerId).setId(id)
    }

    override fun setAlive(alive: Boolean): Bullet {
        return Bullet(position, speed, alive, damage, bulletWidth, bulletHeight, ownerId).setId(id)
    }

    override fun accept(visitor: ColliderVisitor<GameObject>): GameObject? {
        return visitor.visit(this)
    }

    override fun visit(asteroid: Asteroid): Bullet {
        return Bullet(position, speed, false, damage, bulletWidth, bulletHeight, ownerId).setId(id)
    }

    override fun visit(starship: Starship): Bullet? {
        return if (starship.idToString() != ownerId) {
            Bullet(position, speed, false, damage, bulletWidth, bulletHeight, ownerId).setId(id)
        } else {
            null
        }
    }

    override fun visit(bullet: Bullet): Bullet? {
        return if (bullet.ownerId != ownerId) {
            Bullet(position, speed, false, damage, bulletWidth, bulletHeight, ownerId).setId(id)
        } else {
            null
        }
    }
}
