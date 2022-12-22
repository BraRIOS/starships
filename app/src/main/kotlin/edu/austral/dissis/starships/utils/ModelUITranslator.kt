package edu.austral.dissis.starships.utils

import edu.austral.dissis.starships.model.*
import edu.austral.ingsis.starships.ui.ElementColliderType.*
import edu.austral.ingsis.starships.ui.ElementModel
import edu.austral.ingsis.starships.ui.ImageRef

class ModelUITranslator {
    val GREEN_STARSHIP_IMAGE_REF = ImageRef("starship_green", 70.0, 70.0)
    val RED_STARSHIP_IMAGE_REF = ImageRef("starship_red", 70.0, 70.0)

    fun modelToUi(model: GameObject): ElementModel{
        return ElementModel(
            model.idToString(),
            model.position.x,
            model.position.y,
            Math.toDegrees(model.speed.angle),
            40.0,
            40.0,
            Elliptical,
            null
        )
    }
    fun modelToUi(starship: Starship):ElementModel{
        return ElementModel(
            starship.idToString(),
            starship.position.x,
            starship.position.y,
            starship.size,
            starship.size,
            Math.toDegrees(starship.speed.angle),
            Triangular,
            ImageRef("starship", 800.0, 800.0)
        )
    }

    fun modelToUi(asteroid: Asteroid):ElementModel{
        return ElementModel(
            asteroid.idToString(),
            asteroid.position.x,
            asteroid.position.y,
            asteroid.size,
            asteroid.size,
            Math.toDegrees(asteroid.speed.angle),
            Elliptical,
            ImageRef("asteroid", 800.0, 800.0)
        )
    }

    fun modelToUi(bullet: Bullet, gun: Gun):ElementModel{
        val id = bullet.idToString()
        val x = bullet.position.x
        val y = bullet.position.y
        val height = bullet.bulletHeight
        val width = bullet.bulletWidth
        val angle = Math.toDegrees(bullet.speed.angle)
        val colliderType = Rectangular
        var imageRef: ImageRef? = null
        when (gun){
            is BasicGun -> imageRef = ImageRef("bullet_dot_yellow", 800.0, 800.0)
            is ShotGun -> imageRef = ImageRef("bullet_dot_blue", 800.0, 800.0)
            is DualGun -> imageRef = ImageRef("bullet_line_orange", 800.0, 800.0)
        }
        return ElementModel(id, x, y, width, height, angle, colliderType, imageRef)
    }

    fun modelToUiWithImageRef(starship: Starship, imageRef: ImageRef):ElementModel{
        return ElementModel(
            starship.idToString(),
            starship.position.x,
            starship.position.y,
            starship.size,
            starship.size,
            Math.toDegrees(starship.speed.angle),
            Triangular,
            imageRef
        )
    }
}