package edu.austral.dissis.starships.controller

import edu.austral.dissis.starships.model.*

class GameObjectManager(val gameObjects: List<GameObject>) {

    fun addGameObject(gameObject: GameObject): GameObjectManager {
        return GameObjectManager(gameObjects + gameObject)
    }

    fun addGameObjects(gameObjects: List<GameObject>): GameObjectManager {
        return GameObjectManager(this.gameObjects + gameObjects)
    }

    fun removeGameObject(id: String): GameObjectManager {
        return GameObjectManager(gameObjects.filter { it.idToString() != id })
    }

    fun updateGameObject(gameObject: GameObject): GameObjectManager {
        return GameObjectManager(gameObjects.map { if (it.idToString() == gameObject.idToString()) gameObject else it })
    }

    fun handleCollisions(gameObject1Id:String, gameObject2Id:String): GameObjectManager{
        val entities = gameObjects.filter { it.idToString() == gameObject1Id || it.idToString() == gameObject2Id }
        var newGameObjectManager = this
        if (entities.size == 2) {
            val gameObject1 = (entities[0] as Collisionable<GameObject>).handleCollisionWith(entities[1])
            val gameObject2 = (entities[1] as Collisionable<GameObject>).handleCollisionWith(entities[0])
            //si uno es una bala y el otro es un asteroide, se suman puntos a la nave
            if ((gameObject1 is Bullet && gameObject2 is Asteroid ||
                        gameObject1 is Asteroid && gameObject2 is Bullet)) {
                val bullet = (if (gameObject1 is Bullet) gameObject1 else gameObject2) as Bullet
                val asteroid = (if (gameObject1 is Asteroid) gameObject1 else gameObject2) as Asteroid
                if (!asteroid.isAlive) {
                    val starship = gameObjects.filter { it.idToString() == bullet.ownerId }[0] as Starship
                    newGameObjectManager = updateGameObject(starship.addPoints(asteroid.size))
                }
            }
            if (gameObject1 != null) newGameObjectManager = newGameObjectManager.updateGameObject(gameObject1)
            if (gameObject2 != null) newGameObjectManager = newGameObjectManager.updateGameObject(gameObject2)
        }

        return newGameObjectManager
    }

    fun getDestroyedGameObjects(): List<GameObject> {
        return gameObjects.filter { !it.isAlive }
    }

    fun removeDestroyedGameObjects(): GameObjectManager {
        return GameObjectManager(gameObjects.filter { it.isAlive })
    }

    fun update(secondsSinceLastTime: Double): GameObjectManager {
        return GameObjectManager(gameObjects.map { it.update(secondsSinceLastTime) })
    }

    fun reachBoundsStarship(id: String): GameObjectManager {
        if (id.contains("Starship")) {
            val starship = gameObjects.filter { it.idToString() == id }[0] as Starship
            return updateGameObject(starship.setSpeed(Vector.vectorFromModule(-30.0, Math.toDegrees(starship.speed.angle))))
        }
        return this
    }

    fun getStarships(): List<Starship> {
        return gameObjects.filterIsInstance<Starship>()
    }

    fun getAsteroids(): List<Asteroid> {
        return gameObjects.filterIsInstance<Asteroid>()
    }

}