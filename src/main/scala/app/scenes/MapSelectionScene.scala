package app.scenes

import app.Scenes
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.*
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.text.Text

import scala.io.Source

class MapSelectionScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  val center = new HBox:
    val map1 = new ImageView:
      image = Image("image/map1.png")
      onMouseClicked = (_) => selectedScene.value = Scenes.NewGameScene1
      preserveRatio = true
      fitWidth = 400
    val map2 = new ImageView:
      image = Image("image/map2.png")
      onMouseClicked = (_) => selectedScene.value = Scenes.NewGameScene2
      preserveRatio = true
      fitWidth = 400
    children = Seq(map1, map2)
    prefWidth = 600
    spacing = 300
    translateY = 60
    alignment = Center

  val backButton = new ImageView:
    fitWidth = 100
    fitHeight = 100
    image = Image("image/backButton.png")
    alignmentInParent = TopLeft
    margin = Insets(20)
    onMouseClicked = (_) => selectedScene.value = Scenes.LobbyScene

  val banner = new ImageView:
    image = Image("image/mapChoosingBanner.png")
    fitWidth = 500
    preserveRatio = true
    alignmentInParent = TopCenter

  val top = new BorderPane(banner, null, null, null, backButton)
  top.padding = Insets(20, 20, 20, 20)
  val maincontainer = new StackPane:
    children = Seq(backButton, banner, center)

  root = maincontainer