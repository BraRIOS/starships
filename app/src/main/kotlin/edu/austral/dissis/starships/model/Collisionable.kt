package edu.austral.dissis.starships.model

interface Collisionable<T : Collisionable<T>> {
    fun handleCollisionWith(collider: T): T?
}