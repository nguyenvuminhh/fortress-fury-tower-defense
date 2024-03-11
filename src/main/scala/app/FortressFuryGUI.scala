package app


import app.scenes.{GameScene, LobbyScene}
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
import scalafx.stage.Screen

import scala.util.Random
import scala.math.min


object FortressFuryGUI extends JFXApp3:

  def start() =
    val selectedScene = ObjectProperty(Scenes.LobbyScene)

    val mainStage = new JFXApp3.PrimaryStage:
      title = "Fortress Fury"
      width = 1650
      height = 750
      minWidth = 400
      minHeight = 300
      icons += Image("image/icon.jpg")


    selectedScene.onChange((_, _, newValue) =>
      newValue match
        case Scenes.GameScene =>
          stage.setScene(GameScene(mainStage, selectedScene))

        case Scenes.LobbyScene =>
          stage.setScene(LobbyScene(mainStage, selectedScene))
    )
    mainStage.scene = GameScene(mainStage, selectedScene)//LobbyScene(mainStage, selectedScene)
    stage = mainStage

  end start

end FortressFuryGUI


