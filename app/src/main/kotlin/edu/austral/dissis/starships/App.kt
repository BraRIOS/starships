package edu.austral.dissis.starships

import edu.austral.dissis.starships.generator.AsteroidGenerator
import edu.austral.dissis.starships.controller.GameObjectManager
import edu.austral.dissis.starships.controller.GunController
import edu.austral.dissis.starships.controller.StarshipController
import edu.austral.dissis.starships.model.*
import edu.austral.dissis.starships.utils.IniFile
import edu.austral.dissis.starships.utils.ModelUITranslator
import edu.austral.ingsis.starships.ui.*
import javafx.application.Application
import javafx.application.Application.launch
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeType
import javafx.scene.text.*
import javafx.stage.Stage
import java.awt.Desktop
import java.io.File
import java.io.PrintWriter

val imageResolver = CachedImageResolver(DefaultImageResolver())
var objectManager = GameObjectManager(listOf())
var  facade = ElementsViewFacade(imageResolver)
val translator = ModelUITranslator()
val asteroidGenerator = AsteroidGenerator()

fun main() {
    launch(Starships::class.java)
}

class Starships : Application() {
    private val keyTracker = KeyTracker()
    private var scene: Scene? = null
    private var models = mutableListOf<String>()
    private val resourcesPath = "app/src/main/resources/"
    private var gameLoaded = false

    companion object {
        const val WINDOW_WIDTH = 800.0
        const val WINDOW_HEIGHT = 800.0
        var MAX_ASTEROIDS = 10
    }

    override fun start(primaryStage: Stage) {
        scene = Scene(showMenu(), WINDOW_WIDTH, WINDOW_HEIGHT)

        primaryStage.title = "Starships"
        primaryStage.scene = scene
        primaryStage.show()
    }
    fun showMenu(): Parent {
        val root = BorderPane()
        root.background = Background(BackgroundImage(imageResolver.resolve("main_menu_BG", 1920.0, 1080.0),
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            BackgroundSize(100.0, 100.0, true, true, true, true)))

        val title = Text("Starships")
        title.font = Font.font("UNISPACE", FontWeight.BOLD, FontPosture.REGULAR, 60.0)
        title.fill = Color.WHITE
        title.stroke = Color.BLACK
        title.strokeWidth = 2.0
        title.strokeType = StrokeType.OUTSIDE

        val titleBox = HBox(title)
        titleBox.alignment = Pos.CENTER
        titleBox.padding = Insets(200.0, 0.0, -200.0, 0.0)
        root.top = titleBox

        val menu = VBox()
        menu.alignment = Pos.CENTER
        menu.spacing = 20.0

        val startButton = Button("Start")
        startButton.font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20.0)

        val loadButton = Button("Load Game")
        loadButton.font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20.0)

        val settingsButton = Button("Settings")
        settingsButton.font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20.0)

        startButton.setOnMouseClicked {
           startFacade()
        }

        loadButton.setOnMouseClicked {
            gameLoaded = true
            startFacade()
        }

        val file = File("${resourcesPath}config.ini")
        settingsButton.setOnMouseClicked {
            Desktop.getDesktop().open(file)
        }

        menu.children.addAll(startButton, loadButton, settingsButton)
        root.center = menu
        return root
    }

    private fun startFacade() {
        facade = ElementsViewFacade(imageResolver)
        (facade.view as Pane).background = Background(
            BackgroundImage(
                imageResolver.resolve("space", WINDOW_WIDTH*3, WINDOW_HEIGHT*3),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT
            )
        )

        val starshipControllers = mutableListOf<StarshipController>()
        val gunControllers = mutableListOf<GunController>()

        if (gameLoaded) {
            //Load Game Saved
            val iniRead = IniFile("${resourcesPath}save.txt")

            val models = iniRead.getString("SavedGame","modelsSelected", "starship.png")
                .split(",").toMutableList()
            readConfigs(starshipControllers, gunControllers, models)

            val shipsValues = iniRead.getString("SavedGame", "shipsValues", "")
                .split(",")
            for (i in 1..shipsValues.size) {
                val currentShip = objectManager.getStarships()[i-1]
                val vars = shipsValues[i-1].split(";")
                val position = Vector(vars[0].toDouble(),vars[1].toDouble())
                val vSpeed = Vector.vectorFromModule(vars[2].toDouble(),Math.toDegrees(vars[3].toDouble()))
                val lives = vars[4].toInt()
                val points = vars[5].toDouble()
                var isAlive = true
                if (lives == 0) {
                    isAlive = false
                }
                val newShip = Starship(position, vSpeed, isAlive, BasicGun(), lives, points, currentShip.size).setId(currentShip.getId())
                objectManager = objectManager.updateGameObject(newShip)
            }

            val asteroidsValues = iniRead.getString("SavedGame","asteroidsValues","")
                .split(",")
            for (i in 1..asteroidsValues.size) {
                val varsAst = asteroidsValues[i-1].split(";")
                val vPos = Vector(varsAst[0].toDouble(), varsAst[1].toDouble())
                val vSpeed = Vector.vectorFromModule(varsAst[2].toDouble(), Math.toDegrees(varsAst[3].toDouble()))
                val health = varsAst[4].toDouble()
                val size = varsAst[5].toDouble()
                val asteroid = Asteroid(vPos,vSpeed,true,health,size)
                objectManager = objectManager.addGameObject(asteroid)
                facade.elements[asteroid.idToString()] = translator.modelToUi(asteroid)
            }

        }else readConfigs(starshipControllers, gunControllers, models)

        val timeListener = TimeListener(this)
        facade.timeListenable.addEventListener(timeListener)
        facade.collisionsListenable.addEventListener(CollisionListener(starshipControllers, gunControllers))
        facade.reachBoundsListenable.addEventListener(ReachBoundsListener())
        facade.outOfBoundsListenable.addEventListener(OutOfBoundsListener())
        keyTracker.keyPressedListenable.addEventListener(
            KeyPressedListener(
                this,
                timeListener,
                starshipControllers,
                gunControllers
            )
        )
        facade.showGrid.set(false)
        facade.showCollider.set(false)
        scene?.root = facade.view
        keyTracker.scene = scene

        facade.start()
        keyTracker.start()
    }

    fun gameOver() {
        objectManager = GameObjectManager(listOf())
        facade.elements.clear()
        stop()
        val textGameOver = newText("GAME OVER",48.0)
        textGameOver.font = Font.font("UNISPACE", FontWeight.BOLD, FontPosture.REGULAR, 50.0)
        val go = VBox()
        val button = Button("Go to menu")
        button.font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20.0)
        go.alignment = Pos.CENTER
        go.spacing = 30.0
        go.style = "-fx-background-color: black;"
        go.minWidth = WINDOW_WIDTH
        go.minHeight = WINDOW_HEIGHT
        button.setOnMouseClicked {
            scene?.root = showMenu()
        }
        go.children.addAll(textGameOver,button)
        scene?.root = go
    }

    fun restart() {
        objectManager = GameObjectManager(listOf())
        facade.elements.clear()
        stop()
        startFacade()
    }

    fun saveGame() {
        val pathBackup = "${resourcesPath}save.txt"
        val fw = PrintWriter(pathBackup)
        fw.println("[SavedGame]")
        fw.println("modelsSelected=" + models.joinToString(","))
        val text = mutableListOf<String>()
        objectManager.getStarships().forEach { ship ->
            if (ship.isAlive) {
                text.add("${ship.position.x};${ship.position.y};${ship.speed.module};" +
                        "${ship.speed.angle};${ship.lives};${ship.points}")
            }
        }
        fw.println("shipsValues=" + text.joinToString(","))
        val textAsteroid = mutableListOf<String>()
        objectManager.getAsteroids().forEach { a ->
            if (a.isAlive)
                textAsteroid.add(a.position.x.toString() + ";" + a.position.y + ";" +
                        a.speed.module + ";" + a.speed.angle + ";" + a.health + ";" + a.size)
        }
        fw.println("asteroidsValues=" + textAsteroid.joinToString(","))
        fw.close()
        (scene?.root as Pane).children.filterIsInstance<VBox>().first().children.add(newText("Game Saved", 20.0))
    }

    override fun stop() {
        facade.stop()
        keyTracker.stop()
    }

    private fun readConfigs(listControllers: MutableList<StarshipController>,
                            listGunController: MutableList<GunController>,
                            models: MutableList<String>) {

        val ini = IniFile("${resourcesPath}config.ini")

        val players = ini.getInt("Settings","players",1)
        val lives = ini.getInt("Settings","lives",3)
        if (models.isEmpty()) models.addAll(ini.getString("Settings","models","starship.png").split(","))
        MAX_ASTEROIDS = ini.getInt("Settings","asteroids",30)

        for (i in 1..players) {
            val imageRef = ImageRef(models[i-1], 100.0, 100.0)
            val ship = Starship(
                Vector(WINDOW_WIDTH/4 * i, WINDOW_HEIGHT/2),
                Vector(-1.0, 0.0),
                true,
                BasicGun(),
                lives,
                0.0,
                50.0
            )

            objectManager = objectManager.addGameObject(ship)

            val controls = loadMoveKeysFromIniFile(ini, "ControlsPlayer$i")
            val shotKey = ini.getString("ControlsPlayer$i","Shot","Space")
            val changeKey = ini.getString("ControlsPlayer$i","ChangeGun","Enter")

            listControllers.add(StarshipController(ship.idToString(), controls))
            listGunController.add(GunController(ship.idToString(), KeyCode.getKeyCode(shotKey), KeyCode.getKeyCode(changeKey)))

            facade.elements[ship.idToString()] = translator.modelToUiWithImageRef(ship, imageRef)
        }
    }

    private fun loadMoveKeysFromIniFile(iniFile: IniFile, section: String): ArrayList<KeyCode> {
        fun load(k: String): KeyCode {
            return KeyCode.getKeyCode(iniFile.getString(section,k,k))
        }
        return arrayListOf(load("Up"),load("Down"),load("Left"),load("Right"))
    }

    fun renderScoreBoard() {
        val paneFacade = facade.view as Pane
        paneFacade.children.removeAll(paneFacade.children.filter { it is GridPane || it is VBox })
        val grid = GridPane()
        grid.children.clear()
        grid.vgap = 10.0
        grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
        grid.relocate(20.0, 20.0)

        for (i in 1..objectManager.getStarships().size) {
            grid.add(newText("Player $i:",20.0),
                0,i*3-3)
            grid.add(newText("  Points: ${objectManager.getStarships()[i-1].points.toInt()}",15.0),
                0, i*3-2)
            grid.add(newText("  Lives: ${objectManager.getStarships()[i-1].lives}",15.0),
                0, i*3-1)
        }
        grid.background = Background(BackgroundFill(Color(0.0,0.0,1.0,0.4), CornerRadii.EMPTY, Insets.EMPTY))

        (facade.view as Pane).children.addAll(grid)
    }

    private fun newText(str: String, size: Double) : Text {
        val t = Text(str)
        //General Font Settings
        t.font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, size)
        t.fill = Color.WHITE
        return t
    }

    fun renderPauseMenu(){
        val paneFacade = facade.view as Pane
        val vBox = VBox()
        paneFacade.children.removeAll(paneFacade.children.filterIsInstance<GridPane>())
        vBox.spacing = 10.0
        vBox.prefWidth = WINDOW_WIDTH
        vBox.prefHeight = WINDOW_HEIGHT
        vBox.alignment = Pos.CENTER
        vBox.background = Background(BackgroundFill(Color(0.6627451, 0.6627451, 0.6627451,0.01), CornerRadii.EMPTY, Insets.EMPTY))

        val textPause = newText("PAUSE",48.0)
        textPause.stroke = Color.BLACK
        textPause.strokeWidth = 3.0
        val textResume = newText("Press P to continue",15.0)
        textResume.stroke = Color.BLACK
        textResume.strokeWidth = 0.2
        val textRestart = newText("Press R to restart",15.0)
        textRestart.stroke = Color.BLACK
        textRestart.strokeWidth = 0.2
        val textSave = newText("Press S to save",15.0)
        textSave.stroke = Color.BLACK
        textSave.strokeWidth = 0.2

        vBox.children.addAll(textPause,textResume,textRestart,textSave)

        (facade.view as Pane).children.addAll(vBox)
    }
}

class TimeListener(private val game: Starships) : EventListener<TimePassed> {
    var currentFrame = 0
    var secondsSinceLastTime = 0.0
    var paused = false
    override fun handle(event: TimePassed) {
        if (!paused) {
            secondsSinceLastTime = event.secondsSinceLastTime
            generateAsteroid(currentFrame)
            objectManager = objectManager.update(event.secondsSinceLastTime)
            moveUI()
            game.renderScoreBoard()
            if (gameOverCondition()) {
                game.gameOver()
            }
            currentFrame++
        }
        else {
            game.renderPauseMenu()
        }
    }

    private fun gameOverCondition(): Boolean {
        return objectManager.getStarships().isEmpty()
    }

    private fun generateAsteroid(currentFrame : Int) {
        if (objectManager.gameObjects.filterIsInstance<Asteroid>().size < Starships.MAX_ASTEROIDS) {
            val asteroid= asteroidGenerator.generate(currentFrame, facade.view.scene.width, facade.view.scene.height)
            if (asteroid!=null) {
                objectManager = objectManager.addGameObject(asteroid)
                facade.elements[asteroid.idToString()] = translator.modelToUi(asteroid)
            }
        }
    }

    private fun moveUI(){
        objectManager.gameObjects.forEach {
            val uiElement = facade.elements[it.idToString()]
            uiElement?.let { uie ->
                uie.x.set(it.position.x)
                uie.y.set(it.position.y)
                uie.rotationInDegrees.set(it.speed.angleToDegrees())
            }
        }
    }
}

class CollisionListener(private val starshipControllers: MutableList<StarshipController>,
                        private val gunControllers: MutableList<GunController>) : EventListener<Collision> {
    override fun handle(event: Collision) {
        objectManager = objectManager.handleCollisions(event.element1Id, event.element2Id)
        destroyElements()
    }

    private fun destroyElements() {
        objectManager.getDestroyedGameObjects().forEach {
            facade.elements.remove(it.idToString())
        }
        objectManager = objectManager.removeDestroyedGameObjects()
        starshipControllers.removeIf {
            objectManager.getStarships().none { s -> s.idToString() == it.starshipId }
        }
        gunControllers.removeIf {
            objectManager.getStarships().none { s -> s.idToString() == it.starshipId }
        }
    }

}

class KeyPressedListener(private val game: Starships, private val timeListener: TimeListener,
                         private val starshipControllers: MutableList<StarshipController>,
                         private val gunControllers: MutableList<GunController>) : EventListener<KeyPressed> {
    override fun handle(event: KeyPressed) {
        var starship: Starship
        starshipControllers.forEach {
            starship = it.handleKeyPressed(event.key, timeListener.secondsSinceLastTime,
                objectManager.gameObjects.first { o -> o.idToString() == it.starshipId } as Starship)
            objectManager = objectManager.updateGameObject(starship)
        }
        gunControllers.forEach { controller ->
            val (bullets, starship2) = controller.handleKeyPressed(event.key, timeListener.secondsSinceLastTime,
                objectManager.gameObjects.first { o -> o.idToString() == controller.starshipId } as Starship)
            starship = starship2
            objectManager = objectManager.updateGameObject(starship)
            if (bullets.isNotEmpty()) {
                objectManager = objectManager.addGameObjects(bullets as List<GameObject>)
                bullets.forEach {
                    val bulletView = translator.modelToUi(it, starship.gun)
                    facade.elements[bulletView.id] = bulletView
                }
            }
        }
        if (event.key == KeyCode.P) {
            timeListener.paused = !timeListener.paused
        }
        if (timeListener.paused) {
            if (event.key == KeyCode.R) {
                gunControllers.clear()
                starshipControllers.clear()
                game.restart()
            }
            if (event.key == KeyCode.S) {
                game.saveGame()
            }
        }
    }
}

class OutOfBoundsListener() : EventListener<OutOfBounds> {
    override fun handle(event: OutOfBounds) {
        objectManager = objectManager.removeGameObject(event.id)
        facade.elements.remove(event.id)
    }
}

class ReachBoundsListener() : EventListener<ReachBounds> {
    override fun handle(event: ReachBounds) {
        objectManager = objectManager.reachBoundsStarship(event.id)
    }
}
