package app.scenes

import app.Scenes
import app.FortressFuryGUI.stage
import logic.grid.GridPos
import logic.{EndlessGame, GunTower, Sharpshooter, Tower, Turret}
import scalafx.Includes.jfxNode2sfx
import scalafx.scene.SceneIncludes.jfxNode2sfx
import scalafx.application.JFXApp3
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.BaselineCenter
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color.{Blue, Red, Transparent}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scalafx.beans.property.StringProperty
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.delegate.SFXDelegate
import scalafx.Includes.*

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
  var selectedTower: Option[Tower] = None
  /** Game Variables */
  val level = 1
  def wave = game.yieldWave
  def gold = game.yieldGold
  def score = game.yieldScore
  def HP = game.headquarter.HP.toString + "/" + game.headquarter.maxHP.toString
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
  val grid = new GridPane():
    alignmentInParent = scalafx.geometry.Pos.Center
    prefHeight = rowNum * squareside
    prefWidth = colNum * squareside
    vgap = -1
    hgap = -1

    for
      row <- 0 until rowNum
      col <- 0 until colNum
    do
      val square = new StackPane:
        val squareBg = new Rectangle:
          width = squareside
          height = squareside
          fill = Transparent
        val squareImage = new ImageView:
          image =
            val kind = squaretype(col + 1, row)
            if kind == "buildable" then
              new Image("image/" + kind + min(Random.nextInt(30)+1, 7).toString +  ".png")
            else new Image("image/" + kind +  ".png")
          fitHeight = squareside
          fitWidth = squareside
        val gunImage = new ImageView:
          fitHeight = squareside*0.9
          fitWidth = squareside*0.9
          image = clearPlaceHolder

        onMouseClicked = (event) =>
          if selectedGunIndex != -1 then
            val success = game.place(gunNameCollection(selectedGunIndex), col + 1, row)
            if success then gunImage.image = Image("image/ci" + gunNameCollection(selectedGunIndex) + ".png")
            selectedGunIndex = -1
          else if !game.map.elementAt(GridPos(col+1, row)).isEmpty then
            selectedTower = game.map.elementAt(GridPos(col+1, row)).tower //TODO: EDIT METHOD


        children = Seq(squareBg, squareImage, gunImage)
      add(square, col, row)
  val center = grid

  /** LEVEL WAVE TEXT */
  val levelWaveValueProperty = StringProperty(wave.toString)
  val levelWaveLabel = new Text:
    font = Font.font("Arial", FontWeight.Bold, null, 20)
  levelWaveLabel.text <== levelWaveValueProperty

  /** GOLD TEXT */
  val goldValueProperty = StringProperty(gold.toString)
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
  var elapsedTime: Long = 0
  var timerIsRunning: Boolean = true
  val timerText = new Text:
    style = "-fx-font-size: 20pt"

  def updateTimerText(): Unit =
    val hours = elapsedTime / 3600
    val minutes = (elapsedTime % 3600) / 60
    val seconds = elapsedTime % 60
    timerText.text = f"$hours%02d:$minutes%02d:$seconds%02d"
    if elapsedTime%10 == 0 then
      game.giveGold()
      goldValueProperty.value = gold.toString


  def increaseTime(): Unit =
    elapsedTime += 1
    updateTimerText()

  val timerThread = new Thread(() => {
    while timerIsRunning do
      Thread.sleep(1000)
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


  onKeyTyped = (ke: KeyEvent) =>
    ke.character.toLowerCase match
      case r =>
        if selectedTower.nonEmpty then
          selectedTower.get.upgrade()
          print("ok")


