package app


import app.scenes.GameScene
import scalafx.application.JFXApp3
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.BaselineCenter
import scalafx.geometry.VPos.Baseline
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}

import scala.util.Random
import scala.math.min


object FortressFuryGUI extends JFXApp3:

  def start() =
    val selectedScene = ObjectProperty(Scenes.GameScene)

    val mainStage = new JFXApp3.PrimaryStage:
      title = "Speedtyper"
      width = 800
      height = 600
      minWidth = 400
      minHeight = 300


    mainStage.scene = GameScene(mainStage, selectedScene)
    stage = mainStage

  end start

end FortressFuryGUI


