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
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text

class TutorialScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  /** CENTER */
  val center = new StackPane:
    val bg = new Rectangle:
      width = 800
      height = 300
      arcWidth = 20
      arcHeight = 20
      fill = Color.web("#F4C55E")
    val content = new Text:
      text = "Place a gun: Click a gun from the shop, then click an empty square on the map.\n\n" +
        "Upgrade a gun: Click an existing gun on the map, then press key \"u\".\n\n" +
        "Remove a gun: Click an existing gun on the map, then press key \"r\".\n\n" +
        "Deselect a gun: Press key \"y\".\n\nUse ability: Click the ability."

      style = s"-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 20px;"
    children = Seq(bg, content)
  /** BUTTONS */
  val backButton = new ImageView:
    fitWidth = 100
    fitHeight = 100
    image = Image("image/backButton.png")
    alignmentInParent = TopLeft
    margin = Insets(20)
    onMouseClicked = (_) => selectedScene.value = Scenes.LobbyScene

  val nextButton = new ImageView:
    fitWidth = 100
    fitHeight = 100
    image = Image("image/backButton.png")
    rotate = 180
    alignmentInParent = TopRight
    margin = Insets(20)
    onMouseClicked = (_) => selectedScene.value = Scenes.MapSelectionScene

  val banner = new ImageView:
    image = Image("image/tutorialBanner.png")
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
  maincontainer.add(nextButton, 2, 0, 1, 1)
  maincontainer.add(banner, 1, 0, 1, 1)
  maincontainer.add(center, 0, 1, 3, 1)

