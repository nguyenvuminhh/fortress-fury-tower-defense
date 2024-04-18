package app.scenes
import app.FortressFuryApp.stage
import app.Scenes
import logic.*
import logic.grid.GridPos
import logic.Helper

/** I/O IMPORT */
import java.io.{File, FileWriter, PrintWriter}
import scala.io.Source

/** SCALAFX IMPORT */
import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.binding.BindingIncludes.*
import scalafx.beans.property.{DoubleProperty, ObjectProperty, StringProperty}
import scalafx.delegate.SFXDelegate
import scalafx.geometry.Insets
import scalafx.geometry.Pos.*
import scalafx.scene.Scene
import scalafx.scene.SceneIncludes.jfxNode2sfx
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.*
import scalafx.scene.layout.GridPane.{getColumnIndex, getRowIndex}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}

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
  val bgColor = "#ffffff"

  /** SELECTION VARIABLES */
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
  val colNum = 33
  val rowNum = 10
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
              //rate of spawning rock square or cactus square is 1/15, and rate of spawning empty square is 13/15
              new Image("image/" + squareType + min(Random.nextInt(15)+1, 3).toString + ".png")
            else
              new Image("image/" + squareType +  ".png")
          fitHeight = Helper.squareside
          fitWidth = Helper.squareside

        /** GUN IMAGE */
        val gunImage = new ImageView:
          fitHeight = Helper.squareside*0.9
          fitWidth = Helper.squareside*0.9
          image = clearPlaceHolder
        val HPimage = new StackPane:
          val maxHPbar = new Rectangle:
            width = Helper.squareside
            height = 5
            fill = Color.Black
            alignmentInParent = TopLeft
          val HPbar = new Rectangle:
            width <== game.widthPropertyOfHQHP
            height = 5
            fill = Color.Green
            alignmentInParent = TopLeft
          children = Seq(maxHPbar, HPbar)
          alignmentInParent = TopCenter

        /** PLACING HEADQUARTER */
        if GridPos(col, row) == game.map.HQSquare then
          gunImage.image = Image("image/hqImage.png")
          children = Seq(squareImage, gunImage, HPimage)
        else children = Seq(squareImage, gunImage)

        onMouseClicked = () =>
          /** PLACING A GUN */
          if selectedGunIndex != -1 && !game.getIsPaused then
            val success = game.place(gunNameCollection(selectedGunIndex), col, row)
            if success then
              val gun = game.map.elementAt(GridPos(col, row)).tower.get.asInstanceOf[GunTower]
              gunImage.image <== gun.image
              gunImage.rotate <== gun.prevAngle
              updateGold()
            selectedGunIndex = -1
          /** SELECTING EXISTING GUN */
          //check if the square has a tower
          else if game.map.elementAt(GridPos(col, row)).tower.nonEmpty then
            selectedGridPos = GridPos(col, row)
            infoBox.children = game.map.elementAt(selectedGridPos).tower.get.description

      add(square, col, row)

  val paneForEnemy = new Pane:
    prefWidth = mainStage.width.value
    prefHeight = mainStage.height.value * 0.65 // 0.65 is 65%, the percentage of the map in the window
  paneForEnemy.setMouseTransparent(true)

  /** LEVEL WAVE TEXT */
  val waveProperty = StringProperty("Wave" + (wave - 1))
  val levelWaveLabel = new Text:
    style = Helper.gothamBold(20)
  levelWaveLabel.text <== waveProperty

  /** GOLD TEXT */
  val goldValueProperty = StringProperty(gold.toString)
  def updateGold() = goldValueProperty.value = gold.toString
  val goldText = new TextFlow:
    val goldLabel = new Text:
      text = "Gold: "
      style = Helper.gothamBold(15)
    val goldValue = new Text:
      style = Helper.gothamNormal(15)
    goldValue.text <== goldValueProperty
    children = Seq(goldLabel, goldValue)

  /** HP TEXT */
  val HPProperty = StringProperty(HP)
  val HPText = new TextFlow:
    val HPLabel = new Text:
      text = "HP: "
      style = Helper.gothamBold(15)
    val HPValue = new Text:
      style = Helper.gothamNormal(15)
    HPValue.text <== HPProperty
    children = Seq(HPLabel, HPValue)

  /** SCORE TEXT */
  val scoreProperty = StringProperty(score.toString)
  val scoreText = new Text:
    style = Helper.gothamBold(36)
  scoreText.text <== scoreProperty

  /** BUTTONS VARIABLES */
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
    alignment = Center
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
    alignment = Center
    onMouseClicked = () =>
      game.speedup()
      speedupIconsIndex = (speedupIconsIndex+1)%5
      speedupIcon.image = Image("image/speed" + speedupIcons(speedupIconsIndex) + ".png")

  /** QUIT BUTTON */
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
    alignment = Center
    onMouseClicked = () =>
      game.saveGame()
      selectedScene.value = Scenes.LobbyScene

  /** TIME CLOCK */
  def elapsedTime: Long = game.getSurvivingTimeInOneFifthSec/5
  val timerText = new Text:
    style = Helper.gothamNormal(30)

  def tick(): Unit =
    //UPDATE TIMETEXT
    timerText.text = Helper.secondToHMS(elapsedTime)
    //UPDATE SCORE AND GOLD AND HP
    scoreProperty.value = game.getScore.toString
    updateGold()
    if game.getSurvivingTimeInOneFifthSec%50 == 0 then game.giveGold()
    HPProperty.value = HP
    //ADVANCE THE ENEMY
    if game.getFreezeEndTime == 0 then game.enemies.foreach(_.advance())
    //FILTER DEAD ENEMY
    game.enemies.foreach(enemy =>
      if enemy.isDead then
        game.addScore(enemy.point)
        game.increaseEnemyKilled()
        game.addGold(enemy.gold)
        Platform.runLater(() -> {paneForEnemy.children -= enemy.imageView}))
    game.filterDeadEnemy()
    //DEPLOY ENEMY
      //check if its time to deploy AND it is allowed to deploy
    if (game.getSurvivingTimeInOneFifthSec == game.nextDeployTime || game.enemies.isEmpty) && game.getSurvivingTimeInOneFifthSec >= game.cannotDeployUntil then
      deployWave(wave)
      game.nextDeployTime = game.getSurvivingTimeInOneFifthSec + 120*5
      game.nextWave
      waveProperty.value = "Wave " + (wave - 1)
      game.cannotDeployUntil = game.getSurvivingTimeInOneFifthSec + 10*5
      //deploy one troop every 1 second
    if game.toBeDeployed.nonEmpty && game.getSurvivingTimeInOneFifthSec%5 == 0 then
      val enemy = game.toBeDeployed.head
      game.deploy(enemy)
      Platform.runLater(() -> {
        enemy.enemyImage.image = Image(enemy.picturePath)
        paneForEnemy.children += enemy.imageView})
      game.toBeDeployed = game.toBeDeployed.drop(1)
    //SHOOT
    val temp = game.gunTowerCollection.clone() //to avoid mutation while iteration error
    temp.foreach(_.shoot())
    //END ABILITY
    if game.getSurvivingTimeInOneFifthSec == game.getRageEndTime.toLong then game.derage()
    if game.getSurvivingTimeInOneFifthSec == game.getFreezeEndTime.toLong then game.defrost()

  def increaseTime(): Unit =
    if !game.getIsPaused then
      game.increaseTime()
      tick()

  val timerThread = new Thread{
    override def run() =
      while !game.getIsOver do
        Thread.sleep((200.0/game.pace).toLong)
        increaseTime()
  }
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

    val priceLabel = Text(game.abilityPrice.toString)
    priceLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)

    val contentOfButton = new VBox:
      alignment = BaselineCenter
      children = Array(abilityPicture, priceLabel)
      padding = Insets(btWidth/15, 0, 0, 0)

    new StackPane:
      maxHeight = btHeight
      children = Seq(bg, contentOfButton)
      alignment = Center
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

    val priceLabel = Text(s"${gunNameCollection(i)._2}")
    priceLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)

    val contentOfButton = new VBox:
      alignment = BaselineCenter
      children = Array(gunPicture, priceLabel)
      padding = Insets(btWidth/15, 0, 0, 0)

    new StackPane:
      maxHeight = btHeight
      children = Seq(bg, contentOfButton)
      alignment = Center
      onMouseClicked = () =>
        selectedGridPos = GridPos(-1, -1) //reset to avoid accident upgrade/remove
        selectedGunIndex = i
        infoBox.children = game.infoGunTowers(i).description
}

  /** GAME OVER */
  val gameOver = new Text:
    text = "GAME OVER"
    style = Helper.gothamBold(45)
  val scoreStat = new Text:
    style = Helper.gothamBold(35)
  val survivingTime = new Text:
    style = Helper.gothamBold(20)
  val wavesSurvived = new Text:
    style = Helper.gothamBold(25)
  val enemiesKilled = new Text:
    style = Helper.gothamBold(25)

  val waveEnemyVbox = new StackPane:
    val content = new VBox:
      children = Seq(wavesSurvived, enemiesKilled)
      alignment = Center
    val bg = new Rectangle:
      height = 100
      width = 300
      arcHeight = 30
      arcWidth = 30
      fill = Color.web("#F4C55E") //LIGHT BROWN
      stroke = Color.web("#6C5200") //DARK BROWN
      strokeWidth = 5
    padding = Insets(50, 0, 0, 0)
    children = Seq(bg, content)

  val returnHome = new StackPane:
    val content = new Text:
      text = "RETURN HOME"
      style = Helper.gothamBold(25)
      onMouseClicked = (_) => selectedScene.value = Scenes.LobbyScene
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
    alignment = Center
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

  /** CENTER */
  val center = new StackPane:
    children = Seq(gridMap, paneForEnemy)
    translateX = -46

  /** TOP */
  val topLeft = new VBox:
    spacing = 10
    padding = Insets(0, 20, 10, 20)
    children = Seq(levelWaveLabel, goldText, HPText)
  val topCenter = new VBox:
    alignment = Center
    spacing = 10
    children = Seq(scoreText, timerText)
  val topRight = new HBox:
    children = Seq(quit, speedup, pause)
    padding = Insets(0, 10, 0, 0)
    spacing = 20
    alignment = CenterRight

  /** BOTTOM */
  private var infoBox = new StackPane:
    alignment = Center
    maxHeight = 130
    maxWidth = 300
  val bottom = new HBox:
    children = gunButtonsArray ++ abilityButtonArray ++ Array(infoBox)
    spacing = 15
    padding = Insets(0, 70, 10, 10)
    alignment = CenterLeft

  /** ROOT */
  val maincontainer = GridPane()
  root = maincontainer
  val col = new ColumnConstraints:
    percentWidth = 25
  val row0 = new RowConstraints:
    percentHeight = 15
  val row1 = new RowConstraints:
    percentHeight = 65
  val row2 = new RowConstraints:
      percentHeight = 20

  maincontainer.columnConstraints = Array(col, col, col, col)
  maincontainer.rowConstraints = Array(row0, row1, row2)

  maincontainer.add(topLeft, 0, 0, 1, 1)
  maincontainer.add(topCenter, 1, 0, 2, 1)
  maincontainer.add(topRight, 3, 0, 1, 1)
  maincontainer.add(bottom, 0, 2, 4, 1)
  maincontainer.add(center, 0, 1, 4, 1)

  root.value.style = s"-fx-background-color: $bgColor;"

  /** HELPER METHODS */
  def squareInGrid(pos: GridPos, grid: GridPane) =
    grid.children.find(node => getRowIndex(node) == pos.y && getColumnIndex(node) == pos.x).get.asInstanceOf[javafx.scene.layout.StackPane]
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
    //LOAD
    game.load()
    //UPDATE UI OF TEXT AND BUTTON
    pause.children(1).asInstanceOf[javafx.scene.image.ImageView].image = Image("image/resumeButton.png")
    waveProperty.value = "Wave " + (wave - 1)
    //UPDATE UI OF ENEMY
    game.enemies.foreach(enemy =>
      enemy.enemyImage.image = Image(enemy.picturePath)
      paneForEnemy.children += enemy.imageView)
    //UPDATE UI OF GUN
    game.gunTowers.foreach(gun =>
      val gridPos = GridPos(gun.getX, gun.getY)
      val gunImageView = squareInGrid(gridPos, gridMap).children(1).asInstanceOf[javafx.scene.image.ImageView]
      gunImageView.image <== gun.image
      gunImageView.rotate <== gun.prevAngle)
    //UPDATE UI OF HEADQUARTER
    game.widthPropertyOfHQHP.value = Helper.squareside*game.headquarter.HPpercentage
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
          val gunImageView = squareInGrid(selectedGridPos, gridMap).children(1).asInstanceOf[javafx.scene.image.ImageView]
          gunImageView.image.unbind()
          gunImageView.image = clearPlaceHolder
          game.remove(selectedGridPos)
          selectedGridPos = GridPos(-1, -1) //reset
          updateGold()
        else Alert(AlertType.Warning, "You did not select a tower").showAndWait()
      /** CLEAR GUN SELECTION */
      case "y" =>
        selectedGridPos = GridPos(-1, -1)
        selectedGunIndex = -1
      /** UPGRADE GUN */
      case "u" =>
        if selectedGridPos != GridPos(-1, -1) && !game.getIsPaused then game.upgrade(selectedGridPos)
        else if selectedGridPos == GridPos(-1, -1) then Alert(AlertType.Warning, "You did not select a tower").showAndWait()
      case _ => ()