package edu.austral.dissis.starships.model

interface GameObject : Collisionable<GameObject>, ColliderVisitor<GameObject> {
    val position: Vector
    val speed: Vector
    val isAlive: Boolean

    fun idToString(): String

    fun update(secondsSinceLastTime: Double) : GameObject

    fun rotate(angle: Double) : GameObject

    fun setPosition(position: Vector) : GameObject

    fun setSpeed(speed: Vector) : GameObject

    fun setAlive(alive: Boolean) : GameObject

    fun accept(visitor: ColliderVisitor<GameObject>): GameObject?
}