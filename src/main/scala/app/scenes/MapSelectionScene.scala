package app.scenes
import app.Scenes

/** SCALAFX IMPORT */
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.*
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.scene.text.Text

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
    val map3 = new ImageView:
      image = Image("image/map3.png")
      onMouseClicked = (_) => selectedScene.value = Scenes.NewGameScene3
      preserveRatio = true
      fitWidth = 400
    children = Seq(map1, map2, map3)
    prefWidth = 600
    spacing = 50
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
    translateY = 40
    alignmentInParent = BottomCenter

  /** ROOT */
  val maincontainer = GridPane()
  root = maincontainer
  val col0 = new ColumnConstraints:
    percentWidth = 25
  val col1 = new ColumnConstraints:
    percentWidth = 50
  val col2 = new ColumnConstraints:
    percentWidth = 25
  val row0 = new RowConstraints:
    percentHeight = 15
  val row1 = new RowConstraints:
    percentHeight = 85

  maincontainer.columnConstraints = Array(col0, col1, col2)
  maincontainer.rowConstraints = Array(row0, row1)

  maincontainer.add(backButton, 0, 0, 1, 1)
  maincontainer.add(banner, 1, 0, 1, 1)
  maincontainer.add(center, 0, 1, 3, 1)

