package edu.austral.dissis.starships.generator

import edu.austral.dissis.starships.model.Asteroid
import edu.austral.dissis.starships.model.Vector

class AsteroidGenerator() {
    private var framesBetweenAsteroids = 0
    private var frameFromLastAsteroid = 0

    init{
        framesBetweenAsteroids = 60
    }

    fun generate(currentFrame : Int, windowWidth: Double, windowHeight: Double):  Asteroid? {
        if(currentFrame - frameFromLastAsteroid >= framesBetweenAsteroids){
            val a: Asteroid = createRandomAsteroid(windowWidth, windowHeight)
            frameFromLastAsteroid = currentFrame
            return a
        }
        return null
    }

    private fun createRandomAsteroid(windowWidth: Double, windowHeight: Double): Asteroid {
        val x = 40.0 + Math.random() * (windowWidth - 40.0)
        val angle = Math.toRadians(160 + Math.random() * 40)
        val size = 20.0 + Math.random() * 70.0
        val speed = 20.0 + Math.random() * 100
        val pos = Vector(x, 0.0)
        val spd = Vector(0.0, -1.0).rotate(angle).multiply(speed)
        return Asteroid(pos, spd, true, size, size)
    }
}