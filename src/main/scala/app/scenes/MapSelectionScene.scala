package app.scenes

import app.Scenes
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.*
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, ColumnConstraints, GridPane, HBox, RowConstraints, StackPane, VBox}
import scalafx.scene.text.Text

import scala.io.Source

class MapSelectionScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  /** CENTER */
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
    alignment = Center

  /** BACK BUTTON */
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
    translateY = 20
    alignmentInParent = BottomCenter

  /** ROOT */
  val maincontainer = GridPane()
  root = maincontainer
  val column0 = new ColumnConstraints:
    percentWidth = 25
  val column1 = new ColumnConstraints:
    percentWidth = 50
  val column2 = new ColumnConstraints:
    percentWidth = 25
  val row0 = new RowConstraints:
    percentHeight = 15
  val row1 = new RowConstraints:
    percentHeight = 85

  maincontainer.columnConstraints = Array(column0, column1, column2)
  maincontainer.rowConstraints = Array(row0, row1)

  maincontainer.add(backButton, 0, 0, 1, 1)
  maincontainer.add(banner, 1, 0, 1, 1)
  maincontainer.add(center, 0, 1, 3, 1)

