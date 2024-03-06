package app.scenes

import app.Scenes
import app.FortressFuryGUI.stage
import logic.grid.GridPos
import logic.{ArmoredCar, Cavalry, EndlessGame, EnemySoldier, GunTower, Infantry, Sharpshooter, Tank, Tower, Turret}
import scalafx.scene.SceneIncludes.jfxNode2sfx
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{DoubleProperty, ObjectProperty, StringProperty}
import scalafx.geometry.Insets
import scalafx.geometry.Pos.BaselineCenter
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.paint.Color.{Blue, Red, Transparent}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.delegate.SFXDelegate
import scalafx.Includes.*

import java.util.{Timer, TimerTask}
import java.util.concurrent.{Executors, TimeUnit}
import scala.math.min
import scala.util.Random

class GameScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  val game = EndlessGame(logic.Map1)

  /** UI Variables */
  val squareside = 50
  val windowWidth = 1495
  val windowHeight = 750
  val colNum = 31
  val rowNum = 11
  var selectedGunIndex = -1
  var gunNameCollection = Vector("Sharpshooter", "Cannon", "Turret", "GrenadeLauncher", "Sniper", "RocketLauncher")
  var selectedGridPos = GridPos(-1, -1)
  /** Game Variables */
  val level = 1
  def wave = game.getWave
  def gold = game.getGold
  def score = game.getScore
  def HP = game.headquarter.getHP.toString + "/" + game.headquarter.maxHP.toString
  val gunPrice = Vector(120, 150, 200, 250, 300, 500)

  /** Picture Variables */
  val sqCannon = Image("image/sqCannon.png")
  val sqSharpshooter = Image("image/sqSharpshooter.png")
  val sqSniper = Image("image/sqSniper.png")
  val sqTurret = Image("image/sqTurret.png")
  val sqGrenadeLauncher = Image("image/sqGrenadeLauncher.png")
  val sqRocketLauncher = Image("image/sqRocketLauncher.png")
  val sqGunCollection = Vector(sqSharpshooter, sqCannon, sqTurret, sqGrenadeLauncher, sqSniper, sqRocketLauncher)
  val clearPlaceHolder = Image("image/clearPlaceHolder.png")


  /** GRID */
  val gridMap = new GridPane():
    alignmentInParent = scalafx.geometry.Pos.Center
    maxHeight = rowNum * squareside
    maxWidth = colNum * squareside
    vgap = -1
    hgap = -1

    for
      row <- 0 until rowNum
      col <- 0 until colNum
    do
      val kind = squaretype(col, row)
      val square = new StackPane:
        val squareImage = new ImageView:
          image =
            if kind == "buildable" then
              new Image("image/" + kind + min(Random.nextInt(30)+1, 7).toString + ".png")
            else new Image("image/" + kind +  ".png")
          fitHeight = squareside
          fitWidth = squareside

        val gunImage = new ImageView:
          fitHeight = squareside*0.9
          fitWidth = squareside*0.9
          image = clearPlaceHolder




        children = Seq(squareImage, gunImage)
        onMouseClicked = (event) =>
          if selectedGunIndex != -1 then
            val success = game.place(gunNameCollection(selectedGunIndex), col, row)
            if success then
              gunImage.image = Image("image/ci" + gunNameCollection(selectedGunIndex) + ".png")
              gunImage.rotate <== DoubleProperty(game.map.elementAt(GridPos(col, row)).tower.get.asInstanceOf[GunTower].rotationAngle)
              updateGold()
            selectedGunIndex = -1
          else if !game.map.elementAt(GridPos(col, row)).isEmpty && game.map.elementAt(GridPos(col, row)).isPlacable then
          selectedGridPos = GridPos(col, row)

      add(square, col, row)
  val paneForEnemy = new Pane:
    maxWidth <== gridMap.maxWidth
    maxHeight <== gridMap.maxHeight

  paneForEnemy.setMouseTransparent(true)

//TODO: Mapping
  val center = new StackPane:
    children = Seq(gridMap, paneForEnemy)



  /** LEVEL WAVE TEXT */
  val levelWaveValueProperty = StringProperty("Wave " + wave.toString)
  val levelWaveLabel = new Text:
    font = Font.font("Arial", FontWeight.Bold, null, 20)
  levelWaveLabel.text <== levelWaveValueProperty

  /** GOLD TEXT */
  val goldValueProperty = StringProperty(gold.toString)
  def updateGold() = goldValueProperty.value = gold.toString
  val goldText = new TextFlow:
    val goldLabel = new Text:
      text = "Gold: "
      font = Font.font("Arial", FontWeight.Bold, null, 15)
    val goldValue = new Text:
      font = Font.font("Arial", null, null, 15)
    goldValue.text <== goldValueProperty
    children = Seq(goldLabel, goldValue)

  /** HP TEXT */
  val HPProperty = StringProperty(HP)
  val HPText = new TextFlow:
    val HPLabel = new Text:
      text = "HP: "
      font = Font.font("Arial", FontWeight.Bold, null, 15)
    val HPValue = new Text:
      font = Font.font("Arial", null, null, 15)
    HPValue.text <== HPProperty
    children = Seq(HPLabel, HPValue)

  /** SCORE TEXT */
  val scoreProperty = StringProperty(score.toString)
  val scoreText = new Text:
    font = Font.font("Arial", FontWeight.Bold, null, 36)
  scoreText.text <== scoreProperty

  /** Variables for speedup and setting buttons */
  val bgSize = 70
  val iconSize = 50

  /** SETTING BUTTON */
  val setting = new StackPane:
    val settingBg = new Rectangle:
      arcHeight = 30
      arcWidth = 30
      height = bgSize
      width = bgSize
      fill = Blue
    val settingIcon = new Rectangle:
      height = iconSize
      width = iconSize
      fill = Red
    children = Seq(settingBg, settingIcon)
    alignment = scalafx.geometry.Pos.Center

  /** SPEEDUP BUTTON */
  val speedup = new StackPane:
    val speedupBg = new Rectangle:
      arcHeight = 30
      arcWidth = 30
      height = bgSize
      width = bgSize
      fill = Blue
    val speedupIcon = new Rectangle:
      height = iconSize
      width = iconSize
      fill = Red
    children = Seq(speedupBg, speedupIcon)
    alignment = scalafx.geometry.Pos.Center

  /** GUN BUTTONS */
  val btWidth = 100
  val btHeight = 130
  val gunButtons = Array.tabulate(6) { i =>
    val bg = new Rectangle:
      width = btWidth
      height = btHeight
      arcWidth = 20
      arcHeight = 20
      fill = Red
    val gunPicture = new StackPane:

      val pictureBg = new Rectangle:
        width = btWidth*4/5
        height = btWidth*4/5
        arcWidth = 20
        arcHeight = 20
        fill = Transparent
        padding = Insets(btWidth/15, 0, 0, 0)
      val pictureContent = new ImageView:
        fitWidth = btWidth*4/5
        fitHeight = btWidth*4/5
        image = sqGunCollection(i)
      children = Seq(pictureBg, pictureContent)

    val priceLabel = Label(s"${gunPrice(i)}")
    priceLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)

    val contentOfButton = new VBox:
      alignment = BaselineCenter
      children = Array(gunPicture, priceLabel)
      padding = Insets(btWidth/15, 0, 0, 0)

    new StackPane:
      maxHeight = btHeight
      children += bg
      children += contentOfButton
      alignment = scalafx.geometry.Pos.Center
      onMouseClicked = (event) =>
        selectedGunIndex = i
}
  /** TIME CLOCK */
  var timeInOneFifthSec: Long = 0
  def elapsedTime: Long = timeInOneFifthSec/5
  var timerIsRunning: Boolean = true
  val timerText = new Text:
    style = "-fx-font-size: 20pt"

  def updateTimerText(): Unit =
    val hours = elapsedTime / 3600
    val minutes = (elapsedTime % 3600) / 60
    val seconds = elapsedTime % 60
    timerText.text = f"$hours%02d:$minutes%02d:$seconds%02d"
    game.enemies.foreach(_.advance())
    game.gunTowers.foreach(_.shoot())
    game.enemies.foreach(enemy =>
      if enemy.isDead then
        Platform.runLater(() -> {paneForEnemy.children -= enemy.imageView}))
    game.filterDeadEnemy()

    if timeInOneFifthSec%50 == 0 then
      game.giveGold()
      updateGold()

  def increaseTime(): Unit =
    timeInOneFifthSec += 1
    updateTimerText()

  val timerThread = new Thread(() => {
    while timerIsRunning do
      Thread.sleep(200)
      increaseTime()})

  timerThread.start()

  /** Top */
  val topleft = new VBox:
    spacing = 10
    padding = Insets(0, 20, 0, 20)
    children = Seq(levelWaveLabel, goldText, HPText)
  val topcenter = new VBox:
    alignment = scalafx.geometry.Pos.Center
    spacing = 10
    children = Seq(scoreText, timerText)
  val topright = new HBox:
    children = Seq(speedup, setting)
    padding = Insets(0, 45, 0, 0)
    spacing = 10
  val top = new BorderPane(topcenter, null, topright, null, topleft):
    padding = Insets(0, 0, 15, 0)

  /** Bottom */
  val bottom = new HBox:
    children = gunButtons
    spacing = 10
    padding = Insets(0, 0, 10, 0)
    alignment = scalafx.geometry.Pos.Center

  /** Root */
  val maincontainer = BorderPane(center, top, null, bottom, null)
  root = maincontainer

  /** Helper methods */
  def squaretype(x: Int, y: Int) =
    val h1   = ((x >= 0 && x <= 3) || (x >= 8 && x <= 13) || (x >= 22 && x <= 26))  && (y == 4)
    val h2   = (x >= 5 && x <= 13)                                                  && (y == 8)
    val h3   = (x >= 8 && x <= 16)                                                  && (y == 2)
    val h4   = (x >= 18 && x <= 20)                                                 && (y == 6)
    val v1   = (x == 4 || x == 14 || x == 27)                                       && (y >= 5 && y <= 7)
    val v2   = (x == 7)                                                             && (y == 3)
    val v3   = (x == 17)                                                            && (y >= 3 && y <= 5)
    val v4   = (x == 21)                                                            && (y == 5)
    val c1   = Vector((4, 4), (14, 4), (17, 2), (27, 4)).contains((x,y))
    val c2   = Vector((4, 8), (7, 4), (17, 6)).contains((x,y))
    val c3   = Vector((14, 8), (21, 6)).contains((x,y))
    val c4   = Vector((7, 2), (21, 4)).contains((x,y))
    if h1 || h2 || h3 || h4 then "horizontal"
    else if v1 || v2 || v3 || v4 then "vertical"
    else if c1 then "corner1"
    else if c2 then "corner2"
    else if c3 then "corner3"
    else if c4 then "corner4"
    else "buildable"
  def squareInGrid(pos: GridPos, gridd: GridPane) =
    gridd.children.find(node => getRowIndex(node) == pos.y && getColumnIndex(node) == pos.x).get.asInstanceOf[javafx.scene.layout.StackPane]
  def miniSquarePosToGridPos(col: Int, row: Int, miniCol: Int, miniRow: Int) =
    val x = col + 1.0 + 0.2 * miniCol
    val y = row + 1.0 + 0.2 * miniRow
    (x, y)
  def gridPosToMiniSquarePos(x: Double, y: Double) =
    val col = x.toInt - 1.0
    val row = y.toInt - 1.0
    val miniCol = (x - col*1.0)/0.2
    val miniRow = (y - row*1.0)/0.2
    (col, row, miniCol.toInt, miniRow.toInt)
  def unit(amount: Int, unitType: Int): Vector[EnemySoldier] =
    Vector.tabulate(amount){i =>
      unitType match
        case 1 => Infantry(game)
        case 2 => Cavalry(game)
        case 3 => ArmoredCar(game)
        case 4 => Tank(game)}
  def deployWave(wave: Int) =
    def a = wave / 5
    def b = wave / 4
    def c = wave / 3 + 5
    def d = wave / 2 + 10
    val troops = unit(a, 4) ++ unit(b, 3) ++ unit(c, 2) ++ unit(d, 1)
    val timer = new Timer()
    var index = 0
    val task = new TimerTask:
      def run() =
        if index < troops.length then
          val enemy = troops(index)
          game.deploy(troops(index))

          Platform.runLater(() -> {
            enemy.enemyImage.image = Image(enemy.picturePath)
            //enemy.imageView.center.asInstanceOf[ObjectProperty[javafx.scene.image.ImageView]].value.image = Image(enemy.picturePath)
            paneForEnemy.children += enemy.imageView})
          index += 1

        else
          timer.cancel()
    timer.schedule(task, 0, 1000)
//TODO: ask about java thing
  onKeyTyped = (ke: KeyEvent) =>
    ke.character.toLowerCase match
      /** REMOVE GUNS */
      case "r" =>
        if selectedGridPos != GridPos(-1, -1) then
          squareInGrid(selectedGridPos, gridMap).children(1).asInstanceOf[javafx.scene.image.ImageView].image = clearPlaceHolder
          game.remove(selectedGridPos)
          selectedGridPos = GridPos(-1, -1)
          updateGold()
        else ()
      /** CLEAR GUN SELECTION */
      case "y" =>
        selectedGridPos = GridPos(-1, -1)
        selectedGunIndex = -1
      /** UPGRADE GUN */
      case "u" =>
        deployWave(1)



