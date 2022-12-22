package edu.austral.dissis.starships.controller

import edu.austral.dissis.starships.model.*
import javafx.scene.input.KeyCode

class GunController (val starshipId:String, private val shotCode:KeyCode, private val changeGun:KeyCode) {

    private var bullets: List<Bullet> = ArrayList()
    private var secondsSinceLastPressed = 0.0
    private var guns: MutableList<Gun> = ArrayList()
    private var currentIndex = 0

    init {
        guns.add(BasicGun())
        guns.add(ShotGun())
        guns.add(DualGun())
    }

    fun handleKeyPressed(key: KeyCode, secondsSinceLastTime: Double, ship: Starship): Pair<List<Bullet>, Starship> {
        val currentGun: Gun = ship.gun
        secondsSinceLastPressed += secondsSinceLastTime
        if (key == shotCode) {
            if (currentGun.canShot(secondsSinceLastPressed)) {
                bullets = currentGun.shoot(
                    ship.position.add(Vector(ship.size/4, ship.size/4)),
                    ship.speed.angle,
                    starshipId
                )
                secondsSinceLastPressed = 0.0
                return Pair(bullets, ship)
            }
        }
        if (key == changeGun) {
            currentIndex += 1
            if (currentIndex == guns.size) currentIndex = 0
            return Pair(emptyList(), ship.setGun(guns[currentIndex]))
        }
        return Pair(emptyList(), ship)
    }
}