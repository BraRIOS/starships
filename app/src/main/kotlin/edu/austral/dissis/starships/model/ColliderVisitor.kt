package edu.austral.dissis.starships.model

interface ColliderVisitor<T> {
    fun visit(starship: Starship) : T?

    fun visit(asteroid: Asteroid) : T?

    fun visit(bullet: Bullet) : T?
}