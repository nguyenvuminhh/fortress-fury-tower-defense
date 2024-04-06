package app.scenes

import app.Scenes
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.geometry.Pos.*

import scala.io.Source

class LobbyScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  val data = Source.fromFile("src/main/resources/savedGame.txt")
  val dataToSeq = data.getLines().toSeq


  val bg = new ImageView:
    image = Image("image/startBg.png")
    fitWidth <== mainStage.width
    fitHeight <== mainStage.height
  val playButton = new ImageView:
    image = Image("image/playButton.png")
    fitWidth <== mainStage.width/3.5
    preserveRatio = true
    translateY = 60
    alignmentInParent = Center
    onMouseClicked = (_) => selectedScene.value = Scenes.MapSelectionScene

  val leaderboardButton = new ImageView:
    image = Image("image/leaderboardButton.png")
    fitWidth <== mainStage.width/9
    preserveRatio = true
    translateX = -50
    translateY = 10
    alignmentInParent = CenterRight
    onMouseClicked = (_) => selectedScene.value = Scenes.LeaderboardScene

  val container = new StackPane:
    if dataToSeq.nonEmpty then
      val mapType = dataToSeq(1).split("\t")(7).toInt
      val continueButton = new ImageView:
        image = Image("image/continueButton.png")
        fitWidth <== mainStage.width/3.5
        preserveRatio = true
        translateY = 230
        alignmentInParent = Center
        onMouseClicked = (_) => selectedScene.value = if mapType == 1 then Scenes.ContinueGameScene1 else Scenes.ContinueGameScene2
      children = Seq(bg, playButton, leaderboardButton, continueButton)
    else children = Seq(bg, playButton, leaderboardButton)

  val maincontainer = new BorderPane(container, null, null, null, null)
  root = maincontainer
  maincontainer.prefWidth = 1920
  maincontainer.prefHeight = 1080
  data.close()




