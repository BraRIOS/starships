package edu.austral.dissis.starships.controller

import edu.austral.dissis.starships.model.Starship
import edu.austral.dissis.starships.model.Vector
import javafx.scene.input.KeyCode

class StarshipController(val starshipId: String, controls: MutableList<KeyCode>) {
    private val acceleration: Double
    private val deceleration: Double
    private val rotation: Double
    private val controls: MutableList<KeyCode>

    init {
        this.controls = controls
        acceleration = 400.0
        deceleration = 800.0
        rotation = 5.0
    }

    fun handleKeyPressed(key: KeyCode, secondsSinceLastTime: Double, ship: Starship): Starship {
        val currentSpeed: Double = ship.speed.module
        if (key == controls[0]) {
            if (currentSpeed < 300) {
                return ship.setSpeed(ship.speed.setModule(currentSpeed + acceleration * secondsSinceLastTime))
            }
        } else if (key == controls[1]) {
            if (currentSpeed > 0) {
                val module = currentSpeed - deceleration * secondsSinceLastTime
                if (module < 0) {
                    return ship.setSpeed(ship.speed.setModule(0.01))
                }
                return ship.setSpeed(ship.speed.setModule(currentSpeed - deceleration * secondsSinceLastTime))
            }
        } else if (key == controls[2]) {
            return ship.rotate(Math.toRadians(-rotation))
        } else if (key == controls[3]) {
            return ship.rotate(Math.toRadians(rotation))
        }
        return ship
    }
}
