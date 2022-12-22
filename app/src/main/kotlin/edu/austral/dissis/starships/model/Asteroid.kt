package edu.austral.dissis.starships.model

class Asteroid(override val position: Vector, override val speed: Vector,
               override val isAlive: Boolean, val health: Double, val size: Double) : GameObject {
    private var id: Int = 0

    companion object {
        var ID = 0
    }

    init {
        id = ID++
    }

    private fun setId(newId: Int): Asteroid {
        id = newId
        return this
    }

    fun takeDamage(damage: Double): Asteroid {
        if (health - damage <= 0) {
            return Asteroid(position, speed, false, 0.0, size).setId(id)
        }
        return Asteroid(position, speed, isAlive, health - damage, size).setId(id)
    }

    override fun handleCollisionWith(collider: GameObject): GameObject? {
        return collider.accept(this) as Asteroid?
    }

    override fun idToString(): String {
        return "Asteroid-$id"
    }

    override fun update(secondsSinceLastTime: Double): Asteroid {
        return Asteroid(position.add(speed.multiply(secondsSinceLastTime)), speed, isAlive, health, size).setId(id)
    }

    override fun rotate(angle: Double): Asteroid {
        return Asteroid(position, speed.rotate(angle), isAlive, health, size).setId(id)
    }

    override fun setPosition(position: Vector): Asteroid {
        return Asteroid(position, speed, isAlive, health, size).setId(id)
    }

    override fun setSpeed(speed: Vector): Asteroid {
        return Asteroid(position, speed, isAlive, health, size).setId(id)
    }

    override fun setAlive(alive: Boolean): Asteroid {
        return Asteroid(position, speed, alive, health, size).setId(id)
    }

    override fun visit(starship: Starship): Asteroid {
        return Asteroid(position, speed, false, health, size).setId(id)
    }

    override fun visit(asteroid: Asteroid): Asteroid? {
        return null
    }

    override fun visit(bullet: Bullet): Asteroid? {
        if (bullet.isAlive) {
            return takeDamage(bullet.damage)
        }
        return null
    }

    override fun accept(visitor: ColliderVisitor<GameObject>): GameObject? {
        return visitor.visit(this)
    }
}