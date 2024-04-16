package app.scenes

/** PACKAGE IMPORT */
import app.FortressFuryApp.stage
import app.Scenes
import logic.*
import logic.grid.GridPos
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

import java.io.{File, FileWriter, PrintWriter}
import scala.io.Source

/** SCALAFX IMPORT */
import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{DoubleProperty, ObjectProperty, StringProperty}
import scalafx.delegate.SFXDelegate
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.SceneIncludes.jfxNode2sfx
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.*
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Blue, Green, Red, Transparent, White}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scalafx.beans.binding.BindingIncludes._

/** OTHER IMPORTS */
import java.util.{Timer, TimerTask}
import scala.collection.mutable.Buffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.{cos, min}
import scala.util.Random

class GameScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
    game: Game,
    needLoad: Boolean
) extends Scene:

  /** COLOR VARIABLES */
  val buttonColor = Color.web("#BF9000") //DARK BROWN
  val topTextColor = "#000000"
  val fontName = "Gotham"
  val bgColor = "#ffffff"

  /** UI VARIABLES */
  val squareside = 45
  val colNum = 33
  val rowNum = 10
  private var selectedGunIndex = -1
  private var gunNameCollection: Vector[(String, Int)] = Vector(("Sharpshooter", 120), ("Cannon", 150), ("Turret", 200), ("GrenadeLauncher", 250), ("Sniper", 300), ("RocketLauncher", 500))
  private var selectedGridPos = GridPos(-1, -1)

  /** GAME VARIABLES */
  def wave = game.getWave
  def gold = game.getGold
  def score = game.getScore
  def HP = game.headquarter.getHP.toString + "/" + game.headquarter.getMaxHP.toString

  /** IMAGE VARIABLES */
  val poisonPic = Image("image/poison.png")
  val freezePic = Image("image/freeze.png")
  val ragePic = Image("image/rage.png")
  val abilityCollection = Vector(poisonPic, freezePic, ragePic)
  val clearPlaceHolder = Image("image/clearPlaceHolder.png")

  /** GRID */
  val gridMap = new GridPane():
    vgap = -1
    hgap = -1
    for
      row <- 0 until rowNum
      col <- 0 until colNum
    do
      val squareType = game.map.squareType(col, row)
      val square = new StackPane:
        val squareImage = new ImageView:
          image =
            if squareType == "buildable" then
              new Image("image/" + squareType + min(Random.nextInt(30)+1, 7).toString + ".png")
            else new Image("image/" + squareType +  ".png")
          fitHeight = squareside
          fitWidth = squareside

        val gunImage = new ImageView:
          fitHeight = squareside*0.9
          fitWidth = squareside*0.9
          image = clearPlaceHolder

        val HPimage = new StackPane:
          val maxHPbar = new Rectangle:
            width = squareside
            height = 5
            fill = Color.Black
            alignmentInParent = Pos.TopLeft
          val HPbar = new Rectangle:
            width <== game.widthPropertyOfHQHP
            height = 5
            fill = Color.Green
            alignmentInParent = Pos.TopLeft
          children = Seq(maxHPbar, HPbar)
          alignmentInParent = Pos.TopCenter

        if GridPos(col, row) == game.map.HQSquare then
          gunImage.image = Image("image/hqImage.png")
          children = Seq(squareImage, gunImage, HPimage)
        else children = Seq(squareImage, gunImage)

        onMouseClicked = () =>
          if selectedGunIndex != -1 then
            val success = game.place(gunNameCollection(selectedGunIndex), col, row)
            if success then
              val gun = game.map.elementAt(GridPos(col, row)).tower.get.asInstanceOf[GunTower]
              gunImage.image <== gun.image
              gunImage.rotate <== game.map.elementAt(GridPos(col, row)).tower.get.asInstanceOf[GunTower].prevAngle
              updateGold()
            selectedGunIndex = -1
          else if !game.map.elementAt(GridPos(col, row)).isEmpty && game.map.elementAt(GridPos(col, row)).isPlacable then
            selectedGridPos = GridPos(col, row)
            infoBox.children = game.map.elementAt(selectedGridPos).tower.get.description

      add(square, col, row)

  val paneForEnemy = new Pane:
    prefWidth = mainStage.width.value
    prefHeight = mainStage.height.value * 0.65
  paneForEnemy.setMouseTransparent(true)

  /** LEVEL WAVE TEXT */
  val waveProperty = StringProperty("Wave" + (wave -1))
  val levelWaveLabel = new Text:
    style = s"-fx-font-family: $fontName; -fx-font-weight: bold; -fx-font-size: 20px; -fx-fill: $topTextColor;"
  levelWaveLabel.text <== waveProperty
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
  val pause = new StackPane:
    val pauseBg = new Rectangle:
      arcHeight = 30
      arcWidth = 30
      stroke = White
      height = bgSize
      width = bgSize
      fill = buttonColor
    val pauseIcon = new ImageView:
      fitHeight = iconSize
      fitWidth = iconSize
      image = Image("image/pauseButton.png")
      preserveRatio = true

    children = Seq(pauseBg, pauseIcon)
    alignment = Pos.Center
    onMouseClicked = () =>
      if !game.getIsPaused then
        game.pause()
        pauseIcon.image = Image("image/resumeButton.png")
      else
        game.resume()
        pauseIcon.image = Image("image/pauseButton.png")

  /** SPEEDUP BUTTON */
  val speedupIcons = Vector("05", "075", "1", "15", "2")
  var speedupIconsIndex = 2
  val speedup = new StackPane:
    val speedupBg = new Rectangle:
      arcHeight = 30
      arcWidth = 30
      height = bgSize
      width = bgSize
      fill = buttonColor
    val speedupIcon = new ImageView:
      fitHeight = iconSize
      image = Image("image/speed" + speedupIcons(speedupIconsIndex) + ".png")
      preserveRatio = true
    children = Seq(speedupBg, speedupIcon)
    alignment = Pos.Center
    onMouseClicked = () =>
      game.speedup()
      speedupIconsIndex = (speedupIconsIndex+1)%5
      speedupIcon.image = Image("image/speed" + speedupIcons(speedupIconsIndex) + ".png")


    /** QUIT */
  val quit = new StackPane:
    val quitBg = new Rectangle:
      arcHeight = 30
      arcWidth = 30
      height = bgSize
      width = bgSize
      fill = buttonColor
    val quitIcon = new ImageView:
      fitHeight = iconSize
      fitWidth = iconSize
      image = Image("image/quitButton.png")
      preserveRatio = true
    children = Seq(quitBg, quitIcon)
    alignment = Pos.Center
    onMouseClicked = () =>
      game.saveGame()
      selectedScene.value = Scenes.LobbyScene

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
    updateGold()
    if game.getSurvivingTimeInOneFifthSec%50 == 0 then
      game.giveGold()
    HPProperty.value = HP
    //ADVANCE THE ENEMY
    if game.getFreezeEndTime == 0 then
      game.enemies.foreach(_.advance())
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
      waveProperty.value = "Wave " + (wave - 1)
      game.cannotDeployUntil = game.getSurvivingTimeInOneFifthSec + 10*5
    if game.toBeDeployed.nonEmpty && game.getSurvivingTimeInOneFifthSec%5 == 0 then
      val enemy = game.toBeDeployed.head
      game.deploy(enemy)
      Platform.runLater(() -> {
        enemy.enemyImage.image = Image(enemy.picturePath)
        paneForEnemy.children += enemy.imageView})
      game.toBeDeployed = game.toBeDeployed.drop(1)
    //SHOOT
    val temp = game.gunTowerCollection.clone()
    temp.foreach(_.shoot())
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
      alignment = Pos.BaselineCenter
      children = Array(abilityPicture, priceLabel)
      padding = Insets(btWidth/15, 0, 0, 0)

    new StackPane:
      maxHeight = btHeight
      children = Seq(bg, contentOfButton)
      alignment = Pos.Center
      onMouseClicked = () =>
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
        image = Image(s"image/gunIcon${i+1}.png")
        padding = Insets(btWidth/15, 0, 0, 0)
      children += gunPictureContent

    val priceLabel = Label(s"${gunNameCollection(i)._2}")
    priceLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)

    val contentOfButton = new VBox:
      alignment = Pos.BaselineCenter
      children = Array(gunPicture, priceLabel)
      padding = Insets(btWidth/15, 0, 0, 0)

    new StackPane:
      maxHeight = btHeight
      children = Seq(bg, contentOfButton)
      alignment = Pos.Center
      onMouseClicked = () =>
        selectedGunIndex = i
        infoBox.children = game.infoGunTowers(i).description
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
      alignment = Pos.Center
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

  val returnHome = new StackPane:
    val content = new Text:
      text = "RETURN HOME"
      style = "-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 25;"
      onMouseClicked = (_) => selectedScene.value = Scenes.LobbyScene
    val bg = new Rectangle:
      width = 200
      height = 50
      arcWidth = 30
      arcHeight = 30//TODO: QUIT
      fill = Color.web("#F4C55E") //LIGHT BROWN
      stroke = Color.web("#6C5200")
      strokeWidth = 5
    padding = Insets(50, 0, 0, 0)
    children = Seq(bg, content)

  val statContent = new VBox:
    spacing = 5
    alignment = Pos.Center
    prefWidth = 200
    children = Seq(gameOver, scoreStat, survivingTime, waveEnemyVbox, returnHome)
  val statBg = new Rectangle:
    height = 500
    width = 400
    arcHeight = 30
    arcWidth = 30
    fill = buttonColor
  val statBlurBg = new Rectangle:
    height <== mainStage.height
    width <== mainStage.width
    fill = Color.web("#000000", 0.7)
  val stat = new StackPane:
    children = Seq(statBlurBg, statBg, statContent)

  /** Center */
  val center = new StackPane:
    children = Seq(gridMap, paneForEnemy)
    translateX = -46

  /** Top */
  val topLeft = new VBox:
    spacing = 10
    padding = Insets(0, 20, 10, 20)
    children = Seq(levelWaveLabel, goldText, HPText)
  val topCenter = new VBox:
    alignment = Pos.Center
    spacing = 10
    children = Seq(scoreText, timerText)
  val topRight = new HBox:
    children = Seq(quit, speedup, pause)
    padding = Insets(0, 10, 0, 0)
    spacing = 20
    alignment = Pos.CenterRight

  /** Bottom */
  private var infoBox = new StackPane:
    alignment = Pos.Center
    maxHeight = 130
    maxWidth = 300

  val bottom = new HBox:
    children = gunButtonsArray ++ abilityButtonArray ++ Array(infoBox)
    spacing = 15
    padding = Insets(0, 70, 10, 10)
    alignment = Pos.CenterLeft

  /** ROOT */
  val maincontainer = GridPane()
  root = maincontainer

  val column = new ColumnConstraints:
    percentWidth = 25
  val row0 = new RowConstraints:
    percentHeight = 15
  val row1 = new RowConstraints:
    percentHeight = 65
  val row2 = new RowConstraints:
      percentHeight = 20

  maincontainer.columnConstraints = Array(column, column, column, column)
  maincontainer.rowConstraints = Array(row0, row1, row2)

  maincontainer.add(topLeft, 0, 0, 1, 1)
  maincontainer.add(topCenter, 1, 0, 2, 1)
  maincontainer.add(topRight, 3, 0, 1, 1)
  maincontainer.add(bottom, 0, 2, 4, 1)
  maincontainer.add(center, 0, 1, 4, 1)

  root.value.style = s"-fx-background-color: $bgColor;"
  
  /** HELPER METHODS */
  def squareInGrid(pos: GridPos, gridd: GridPane) =
    gridd.children.find(node => getRowIndex(node) == pos.y && getColumnIndex(node) == pos.x).get.asInstanceOf[javafx.scene.layout.StackPane]
  def unit(amount: Int, unitType: Int): Vector[EnemySoldier] =
    Vector.tabulate(amount){_ =>
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
    if wave != 0 then game.toBeDeployed = troops else ()

 
  /** DETECTING GAMEOVER */
  game.widthPropertyOfHQHP.onChange((_, _, newValue) =>
    if newValue.intValue() <= 0 then
      game.saveRecord()
      scoreStat.text = score.toString
      survivingTime.text = timerText.text.value
      enemiesKilled.text = "Killed " + game.getEnemyKilled.toString + " enemies"
      wavesSurvived.text = "Survived " + ((wave-1).toString) + " waves"
      Platform.runLater(() -> maincontainer.add(stat, 0, 0, 4, 3)))

  /** LOAD GAME */
  val data = Source.fromFile("src/main/resources/savedGame.txt")
  if data.getLines().nonEmpty && needLoad then 
    game.load()
    waveProperty.value = "Wave " + (wave - 1)
    game.enemies.foreach(enemy =>
      enemy.enemyImage.image = Image(enemy.picturePath)
      paneForEnemy.children += enemy.imageView)
    game.gunTowers.foreach(gun =>
      val gridPos = GridPos(gun.getX, gun.getY)
      squareInGrid(gridPos, gridMap).children(1).asInstanceOf[javafx.scene.image.ImageView].image = Image("image/ci" + gun.name + ".png")
      squareInGrid(gridPos, gridMap).children(1).asInstanceOf[javafx.scene.image.ImageView].rotate <== gun.prevAngle)
    game.widthPropertyOfHQHP.value = 45*game.headquarter.HPpercentage
  data.close()

  /** DELETE THE SAVED GAME FILE */
  val file = new File("src/main/resources/savedGame.txt")
  val writer = new PrintWriter(file)
  writer.close()

 /** SETTING KEYS */
  onKeyTyped = (ke: KeyEvent) =>
    ke.character.toLowerCase match
      /** REMOVE GUNS */
      case "r" =>
        if selectedGridPos == game.map.HQSquare then Alert(AlertType.Warning, "You cannot remove the headquarter").showAndWait()
        else if selectedGridPos != GridPos(-1, -1) then
          squareInGrid(selectedGridPos, gridMap).children(1).asInstanceOf[javafx.scene.image.ImageView].image.unbind()
          squareInGrid(selectedGridPos, gridMap).children(1).asInstanceOf[javafx.scene.image.ImageView].image = clearPlaceHolder
          game.remove(selectedGridPos)
          selectedGridPos = GridPos(-1, -1)
          updateGold()
        else Alert(AlertType.Warning, "You did not select a tower").showAndWait()
      /** CLEAR GUN SELECTION */
      case "y" =>
        selectedGridPos = GridPos(-1, -1)
        selectedGunIndex = -1
      /** UPGRADE GUN */
      case "u" =>
        if selectedGridPos != GridPos(-1, -1) then game.upgrade(selectedGridPos)
        else Alert(AlertType.Warning, "You did not select a tower").showAndWait()
      case _ => ()


