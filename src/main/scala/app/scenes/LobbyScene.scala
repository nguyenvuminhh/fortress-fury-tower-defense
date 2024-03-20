package app.scenes

import app.Scenes
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.scene.image.{Image, ImageView}

class LobbyScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  val bg = new ImageView:
    image = Image("image/startBg.png")
    fitWidth <== mainStage.width
    fitHeight <== mainStage.height
  val playButton = new ImageView:
    image = Image("image/playButton.png")
    fitWidth <== mainStage.width/3.5
    preserveRatio = true
    translateY = 140
    alignmentInParent = scalafx.geometry.Pos.Center
    onMouseClicked = (event) =>
      selectedScene.value = Scenes.ContinueGameScene

  val leaderboardButton = new ImageView:
    image = Image("image/leaderboardButton.png")
    fitWidth <== mainStage.width/9
    preserveRatio = true
    translateX = -50
    translateY = 10
    alignmentInParent = scalafx.geometry.Pos.CenterRight

  val container = new StackPane:
    children = Seq(bg, playButton, leaderboardButton)

  val maincontainer = new BorderPane(container, null, null, null, null)
  root = maincontainer
  maincontainer.prefWidth = 1920
  maincontainer.prefHeight = 1080


