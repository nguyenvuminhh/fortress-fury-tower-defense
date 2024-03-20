package app.scenes

import app.Scenes
import app.FortressFuryGUI.stage
import logic.grid.GridPos
import logic.{ArmoredCar, Cannon, Cavalry, EnemySoldier, Game, GunTower, Infantry, Sharpshooter, Tank, Tower, Turret}
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
import scalafx.scene.paint.Color.{Black, Blue, Green, Red, Transparent, White}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.delegate.SFXDelegate
import scalafx.Includes.*
import scalafx.scene.paint.Color

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.Buffer
import java.util.{Timer, TimerTask}
import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.Future
import scala.io.Source
import scala.math.min
import scala.util.Random

class GameScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
    game: Game
) extends Scene:



  /** COLOR VARIABLES */
  val buttonColor = Color.web("#BF9000") //DARK BROWN
  val topTextColor = "#000000"
  val fontName = "Gotham"
  val strokeColor = Black
  val bgColor = "#ffffff"

  /** UI VARIABLES */
  val squareside = 50
  val colNum = 32
  val rowNum = 11
  var selectedGunIndex = -1
  var gunNameCollection = Vector("Sharpshooter", "Cannon", "Turret", "GrenadeLauncher", "Sniper", "RocketLauncher")
  var selectedGridPos = GridPos(-1, -1)
  val widthPropertyOfHQHP = DoubleProperty(50*game.headquarter.HPpercentage)

  /** GAME VARIABLES */
  def wave = (game.getWave)
  def gold = game.getGold
  def score = game.getScore
  def HP = game.headquarter.getHP.toString + "/" + game.headquarter.getMaxHP.toString
  val gunPrice = Vector(120, 150, 200, 250, 300, 500)


  /** IMAGE VARIABLES */
  val sqCannon = Image("image/sqCannon.png")
  val sqSharpshooter = Image("image/sqSharpshooter.png")
  val sqSniper = Image("image/sqSniper.png")
  val sqTurret = Image("image/sqTurret.png")
  val sqGrenadeLauncher = Image("image/sqGrenadeLauncher.png")
  val sqRocketLauncher = Image("image/sqRocketLauncher.png")
  val sqGunCollection = Vector(sqSharpshooter, sqCannon, sqTurret, sqGrenadeLauncher, sqSniper, sqRocketLauncher)
  val poisonPic = Image("image/poison.png")
  val freezePic = Image("image/freeze.png")
  val ragePic = Image("image/rage.png")
  val abilityCollection = Vector(poisonPic, freezePic, ragePic)
  val clearPlaceHolder = Image("image/clearPlaceHolder.png")

  /** GRID */
  val gridMap = new GridPane():
    alignmentInParent = scalafx.geometry.Pos.Center
    maxHeight = rowNum * squareside
    maxWidth = colNum * squareside
    vgap = -1
    hgap = -1
    alignmentInParent = scalafx.geometry.Pos.TopLeft
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

        val HPimage = new StackPane:
          val maxHPbar = new Rectangle:
            width = 50
            height = 5
            fill = Color.Black
            alignmentInParent = scalafx.geometry.Pos.TopLeft
          val HPbar = new Rectangle:
            width <== widthPropertyOfHQHP
            height = 5
            fill = Color.Green
            alignmentInParent = scalafx.geometry.Pos.TopLeft
          children = Seq(maxHPbar, HPbar)
          alignmentInParent = scalafx.geometry.Pos.TopCenter

        if GridPos(col, row) == game.map.HQSquare then
          gunImage.image = Image("image/hqImage.png")
          children = Seq(squareImage, gunImage, HPimage)
        else children = Seq(squareImage, gunImage)

        onMouseClicked = (event) =>
          if selectedGunIndex != -1 then
            val success = game.place(gunNameCollection(selectedGunIndex), col, row)
            if success then
              gunImage.image = Image("image/ci" + gunNameCollection(selectedGunIndex) + ".png")
              gunImage.rotate <== game.map.elementAt(GridPos(col, row)).tower.get.asInstanceOf[GunTower].prevAngle
              updateGold()
            selectedGunIndex = -1
          else if !game.map.elementAt(GridPos(col, row)).isEmpty && game.map.elementAt(GridPos(col, row)).isPlacable then
            selectedGridPos = GridPos(col, row)
            bottomCenter.children = game.map.elementAt(selectedGridPos).tower.get.description
      add(square, col, row)

  val paneForEnemy = new Pane:
    maxWidth <== gridMap.maxWidth
    maxHeight <== gridMap.maxHeight
  paneForEnemy.setMouseTransparent(true)

  /** LEVEL WAVE TEXT */
  val levelWaveValueProperty = StringProperty("Wave" + (wave -1))
  val levelWaveLabel = new Text:
    style = s"-fx-font-family: $fontName; -fx-font-weight: bold; -fx-font-size: 20px; -fx-fill: $topTextColor;"
  levelWaveLabel.text <== levelWaveValueProperty
  /** GOLD TEXT */
  val goldValueProperty = StringProperty(gold.toString)
  def updateGold() = goldValueProperty.value = gold.toString
  val goldText = new TextFlow:
    val goldLabel = new Text:
      text = "Gold: "
      style = s"-fx-font-family: $fontName; -fx-font-weight: bold; -fx-font-size: 15; -fx-fill: $topTextColor;"

    val goldValue = new Text:
      style = s"-fx-font-family: $fontName; -fx-font-size: 15px; -fx-fill: $topTextColor;"
    goldValue.text <== goldValueProperty
    children = Seq(goldLabel, goldValue)

  /** HP TEXT */
  val HPProperty = StringProperty(HP)
  val HPText = new TextFlow:
    val HPLabel = new Text:
      text = "HP: "
      style = s"-fx-font-family: $fontName; -fx-font-weight: bold; -fx-font-size: 15px; -fx-fill: $topTextColor;"
    val HPValue = new Text:
      style = s"-fx-font-family: $fontName; -fx-font-size: 15px; -fx-fill: $topTextColor;"
    HPValue.text <== HPProperty
    children = Seq(HPLabel, HPValue)

  /** SCORE TEXT */
  val scoreProperty = StringProperty(score.toString)
  val scoreText = new Text:
    style = s"-fx-font-family: $fontName; -fx-font-weight: bold; -fx-font-size: 36px; -fx-fill: $topTextColor;"
  scoreText.text <== scoreProperty

  /** Variables for speedup and setting buttons */
  val bgSize = 70
  val iconSize = 50

  /** SETTING BUTTON */
  val setting = new StackPane:
    val settingBg = new Rectangle:
      arcHeight = 30
      arcWidth = 30
      stroke = White
      height = bgSize
      width = bgSize
      fill = buttonColor
    val settingIcon = new ImageView:
      fitHeight = iconSize
      fitWidth = iconSize
      image = Image("image/pauseButton.png")
      preserveRatio = true

    children = Seq(settingBg, settingIcon)
    alignment = scalafx.geometry.Pos.Center
    onMouseClicked = (event) =>
      if !game.getIsPaused then game.pause() else game.resume()

  /** SPEEDUP BUTTON */
  val speedup = new StackPane:
    val speedupBg = new Rectangle:
      arcHeight = 30
      arcWidth = 30
      height = bgSize
      width = bgSize
      fill = buttonColor
    val speedupIcon = new ImageView:
      fitHeight = iconSize
      fitWidth = iconSize
      image = Image("image/speedupButton.png")
      preserveRatio = true
    children = Seq(speedupBg, speedupIcon)
    alignment = scalafx.geometry.Pos.Center
    onMouseClicked = (event) =>
      game.speedup()





  /** TIME CLOCK */
  def elapsedTime: Long = game.getSurvivingTimeInOneFifthSec/5
  val timerText = new Text:
    style = s"-fx-font-size: 20pt; -fx-fill: $topTextColor;"

  def updateTimerText(): Unit =
    //CONVERT TO HH:MM:SS
    val hours = elapsedTime / 3600
    val minutes = (elapsedTime % 3600) / 60
    val seconds = elapsedTime % 60
    //UPDATE TIMETEXT
    timerText.text = f"$hours%02d:$minutes%02d:$seconds%02d"
    //UPDATE SCORE AND GOLD AND HP
    scoreProperty.value = game.getScore.toString
    if game.getSurvivingTimeInOneFifthSec%50 == 0 then
      game.giveGold()
      updateGold()
    HPProperty.value = HP
    //ADVANCE THE ENEMY
    if game.getFreezeEndTime == 0 then
      game.enemies.foreach(enemy =>
        if enemy.getX == game.map.crashSquare.x && enemy.getY == game.map.crashSquare.y then
          enemy.crash()
          widthPropertyOfHQHP.value = 50*game.headquarter.HPpercentage
        else enemy.advance())
    //FILTER DEAD ENEMY
    game.enemies.foreach(enemy =>
      if enemy.isDead then
        game.addScore(enemy.point)
        game.increaseEnemyKilled()
        game.addGold(enemy.gold)
        Platform.runLater(() -> {paneForEnemy.children -= enemy.imageView}))
    game.filterDeadEnemy()
    //DEPLOY ENEMY
    if (game.getSurvivingTimeInOneFifthSec == game.nextDeployTime || game.enemies.isEmpty) && game.getSurvivingTimeInOneFifthSec >= game.cannotDeployUntil then
      deployWave(wave)
      game.nextDeployTime = game.getSurvivingTimeInOneFifthSec + 120*5
      game.nextWave
      levelWaveValueProperty.value = "Wave " + (wave - 1)
      game.cannotDeployUntil = game.getSurvivingTimeInOneFifthSec + 10*5
    if game.toBeDeployed.nonEmpty && game.getSurvivingTimeInOneFifthSec%5 == 0 then
      val enemy = game.toBeDeployed.head
      game.deploy(enemy)
      Platform.runLater(() -> {
        enemy.enemyImage.image = Image(enemy.picturePath)
        paneForEnemy.children += enemy.imageView})
      game.toBeDeployed = game.toBeDeployed.drop(1)
    //SHOOT
    game.gunTowers.foreach(_.shoot())
    //END ABILITY
    if game.getSurvivingTimeInOneFifthSec == game.getRageEndTime.toLong then game.derage()
    if game.getSurvivingTimeInOneFifthSec == game.getFreezeEndTime.toLong then game.defrost()

  def increaseTime(): Unit =
    if !game.getIsPaused then
      game.increaseTime()
      updateTimerText()

  val timerThread = new Thread(() =>
    while !game.getIsOver do
      Thread.sleep((200*1.0/game.pace).toLong)
      increaseTime())
  timerThread.start()

  /** ABILITIES */
  val btWidth = 100
  val btHeight = 130
  val abilityButtonArray = Array.tabulate(3) { i =>
    val bg = new Rectangle:
      width = btWidth
      height = btHeight
      arcWidth = 20
      arcHeight = 20
      fill = buttonColor
    val abilityPicture = new StackPane:
      val abilityPictureContent = new ImageView:
        fitWidth = btWidth*4/5
        fitHeight = btWidth*4/5
        image = abilityCollection(i)
        padding = Insets(btWidth/15, 0, 0, 0)
      children += abilityPictureContent

    val priceLabel = Label(game.abilityPrice.toString)
    priceLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)

    val contentOfButton = new VBox:
      alignment = BaselineCenter
      children = Array(abilityPicture, priceLabel)
      padding = Insets(btWidth/15, 0, 0, 0)

    new StackPane:
      maxHeight = btHeight
      children = Seq(bg, contentOfButton)
      alignment = scalafx.geometry.Pos.Center
      onMouseClicked = (event) =>
        selectedGunIndex = i
          onMouseClicked = (event) =>
            i match
              case 0 => game.poison()
              case 1 => game.freeze()
              case 2 => game.rage()
      }

  /** GUN BUTTONS */
  val gunButtonsArray = Array.tabulate(6) { i =>
    val bg = new Rectangle:
      width = btWidth
      height = btHeight
      arcWidth = 20
      arcHeight = 20
      fill = buttonColor
    val gunPicture = new StackPane:
      val gunPictureContent = new ImageView:
        fitWidth = btWidth*4/5
        fitHeight = btWidth*4/5

        image = sqGunCollection(i)
        padding = Insets(btWidth/15, 0, 0, 0)
      children += gunPictureContent

    val priceLabel = Label(s"${gunPrice(i)}")
    priceLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)

    val contentOfButton = new VBox:
      alignment = BaselineCenter
      children = Array(gunPicture, priceLabel)
      padding = Insets(btWidth/15, 0, 0, 0)
  

    new StackPane:
      maxHeight = btHeight
      children = Seq(bg, contentOfButton)
      alignment = scalafx.geometry.Pos.Center
      onMouseClicked = (event) =>
        selectedGunIndex = i
        bottomCenter.children = game.infoGunTowers(i).description
}

  /** GAME OVER */
  val gameOver = new Text:
    text = "GAME OVER"
    style = "-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 45;"
  val scoreStat = new Text:
    style = "-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 35;"
  val survivingTime = new Text:
    style = "-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 20;"
  val wavesSurvived = new Text:
    style = "-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 25;"
  val enemiesKilled = new Text:
    style = "-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 25;"

  val waveEnemyVbox = new StackPane:
    val content = new VBox:
      children = Seq(wavesSurvived, enemiesKilled)
      alignment = scalafx.geometry.Pos.Center
    val bg = new Rectangle:
      height = 100
      width = 300
      arcHeight = 30
      arcWidth = 30
      fill = Color.web("#F4C55E") //LIGHT BROWN
      stroke = Color.web("#6C5200")
      strokeWidth = 5
    padding = Insets(50, 0, 0, 0)
    children = Seq(bg, content)

  val playAgain = new StackPane:
    val content = new Text:
      text = "PLAY AGAIN"
      style = "-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 25;"
    val bg = new Rectangle:
      width = 200
      height = 50
      arcWidth = 30
      arcHeight = 30
      fill = Color.web("#F4C55E") //LIGHT BROWN
      stroke = Color.web("#6C5200")
      strokeWidth = 5
    padding = Insets(50, 0, 0, 0)
    children = Seq(bg, content)

  val statContent = new VBox:
    spacing = 5
    alignment = scalafx.geometry.Pos.Center
    prefWidth = 200
    children = Seq(gameOver, scoreStat, survivingTime, waveEnemyVbox, playAgain)
  val statBg = new Rectangle:
    height = 500
    width = 400
    arcHeight = 30
    arcWidth = 30
    fill = buttonColor
  val statBlurBg = new Rectangle:
    height <== paneForEnemy.height
    width <== paneForEnemy.width
    fill = Color.web("#000000", 0.7)
  val stat = new StackPane:
    children = Seq(statBlurBg, statBg, statContent)

  /** Center */
  val center = new StackPane:
    children = Seq(gridMap, paneForEnemy)
    translateX = -51

  /** Top */
  val topleft = new VBox:
    spacing = 10
    padding = Insets(0, 20, 10, 20)
    children = Seq(levelWaveLabel, goldText, HPText)
  val topcenter = new VBox:
    alignment = scalafx.geometry.Pos.Center
    spacing = 10
    children = Seq(scoreText, timerText)
  val topright = new HBox:
    children = Seq(speedup, setting)
    padding = Insets(0, 70, 0, 0)
    spacing = 10
  val top = new BorderPane(topcenter, null, topright, null, topleft)

  /** Bottom */
  val bottomRight = new HBox:
    children = gunButtonsArray
    spacing = 20
    padding = Insets(0, 70, 10, 0)
    alignment = scalafx.geometry.Pos.Center
  val bottomLeft = new HBox:
    children = abilityButtonArray
    spacing = 10
    padding = Insets(0, 0 , 10, 15)
    alignment = scalafx.geometry.Pos.Center
  private var bottomCenter = new StackPane:
    alignment = scalafx.geometry.Pos.Center
    padding = Insets(0, 0 , 10, 0)
    maxHeight = 130
    maxWidth = 300
  val bottom = new BorderPane(bottomCenter, null, bottomRight, null, bottomLeft)


  /** Root */
  val maincontainer = BorderPane(center, top, null, bottom, null)
  root = maincontainer
  maincontainer.prefWidth = 1920
  maincontainer.prefHeight = 1080
  root.value.style = s"-fx-background-color: $bgColor;"

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
    if wave != 0 then game.toBeDeployed = troops else ()

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
        if selectedGridPos != GridPos(-1, -1) then game.upgrade(selectedGridPos)
      case "f" => widthPropertyOfHQHP.value = 0
      case "s" => game.saveGame()
      case e => ()

  widthPropertyOfHQHP.onChange((_, _, newValue) =>
    if newValue.intValue() <= 0 then
      game.saveRecord()
      scoreStat.text = score.toString
      survivingTime.text = timerText.text.value
      enemiesKilled.text = "Killed " + game.getEnemyKilled.toString + " enemies"
      wavesSurvived.text = "Survived " + ((wave-1).toString) + " waves"
      Platform.runLater(() -> center.children.append(stat))
      println("saved"))


